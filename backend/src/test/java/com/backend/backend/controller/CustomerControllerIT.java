package com.backend.backend.controller;

import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerIT extends IntegrationTestBase {

    @Autowired private MockMvc mvc;

    @Test
    void create_then_list_shouldPersistIntoMySQL() throws Exception {
        var body = """
            { "name": "Nguyễn Văn A", "contactInfo": "email: nguyenvana@example.com, phone: 0123456789" }
        """;

        // create
        mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", notNullValue()))
           .andExpect(jsonPath("$.name").value("Nguyễn Văn A"));

        // list with pagination/sort
        mvc.perform(get("/api/v1/customers?page=0&size=10&sort=id,desc")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items", not(empty())))
           .andExpect(jsonPath("$.page").value(0))
           .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void create_whenInvalid_shouldReturn400() throws Exception {
        var invalid = """
            { "name": "", "contactInfo": "" }
        """;

        mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error", containsString("Validation")))
           .andExpect(jsonPath("$.fieldErrors.name", not(emptyString())));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/v1/customers/999")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void search_shouldWorkWithRealMySQL() throws Exception {
        // Create test data
        var customer1 = """
            { "name": "Nguyễn Văn A", "contactInfo": "email: nguyenvana@example.com" }
        """;
        var customer2 = """
            { "name": "Trần Thị B", "contactInfo": "email: tranthib@example.com" }
        """;

        mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customer1))
           .andExpect(status().isOk());

        mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customer2))
           .andExpect(status().isOk());

        // Search for Nguyễn
        mvc.perform(get("/api/v1/customers?search=nguyễn")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items", not(empty())))
           .andExpect(jsonPath("$.items[0].name", containsString("Nguyễn")));
    }

    @Test
    void update_shouldWorkWithRealMySQL() throws Exception {
        // Create customer
        var createBody = """
            { "name": "Nguyễn Văn A", "contactInfo": "email: nguyenvana@example.com" }
        """;

        var createResult = mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
           .andExpect(status().isOk())
           .andReturn();

        var customerId = createResult.getResponse().getContentAsString()
                .substring(createResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Update customer
        var updateBody = """
            { "name": "Nguyễn Văn A Updated", "contactInfo": "email: updated@example.com, phone: 9999999999" }
        """;

        mvc.perform(patch("/api/v1/customers/" + customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("Nguyễn Văn A Updated"))
           .andExpect(jsonPath("$.contactInfo").value("email: updated@example.com, phone: 9999999999"));
    }

    @Test
    void delete_shouldWorkWithRealMySQL() throws Exception {
        // Create customer
        var createBody = """
            { "name": "Customer To Delete", "contactInfo": "email: delete@example.com" }
        """;

        var createResult = mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
           .andExpect(status().isOk())
           .andReturn();

        var customerId = createResult.getResponse().getContentAsString()
                .substring(createResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Delete customer
        mvc.perform(delete("/api/v1/customers/" + customerId))
           .andExpect(status().isNoContent());

        // Verify deletion
        mvc.perform(get("/api/v1/customers/" + customerId)
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }
}
