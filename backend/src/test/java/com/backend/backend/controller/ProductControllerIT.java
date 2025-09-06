package com.backend.backend.controller;

import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerIT extends IntegrationTestBase {

    @Autowired private MockMvc mvc;

    @Test
    void create_then_list_shouldPersistIntoMySQL() throws Exception {
        var body = """
            { "name": "iPhone 15", "description": "Latest iPhone", "price": 1200.00, "quantityInStock": 5 }
        """;

        // create
        mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", notNullValue()))
           .andExpect(jsonPath("$.name").value("iPhone 15"));

        // list with pagination/sort
        mvc.perform(get("/api/v1/products?page=0&size=10&sort=id,desc")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items", not(empty())))
           .andExpect(jsonPath("$.page").value(0))
           .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void create_whenInvalid_shouldReturn400() throws Exception {
        var invalid = """
            { "name": "", "price": -1, "quantityInStock": -2 }
        """;

        mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error", containsString("Validation")))
           .andExpect(jsonPath("$.fieldErrors.name", not(emptyString())));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/v1/products/999")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void search_shouldWorkWithRealMySQL() throws Exception {
        // Create test data
        var product1 = """
            { "name": "iPhone 15 Pro", "description": "Latest iPhone", "price": 1299.00, "quantityInStock": 10 }
        """;
        var product2 = """
            { "name": "MacBook Air M3", "description": "Apple laptop", "price": 1199.00, "quantityInStock": 5 }
        """;

        mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(product1))
           .andExpect(status().isOk());

        mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(product2))
           .andExpect(status().isOk());

        // Search for iPhone
        mvc.perform(get("/api/v1/products?search=iphone")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items", not(empty())))
           .andExpect(jsonPath("$.items[0].name", containsString("iPhone")));
    }
}
