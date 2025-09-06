package com.backend.backend.controller;

import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SupplierControllerIT extends IntegrationTestBase {

    @Autowired private MockMvc mvc;

    @Test
    void create_then_list_shouldPersistIntoMySQL() throws Exception {
        var body = """
            { "name": "Công ty TNHH ABC", "contactInfo": "email: contact@abc.com, phone: 0123456789" }
        """;

        // create
        mvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", notNullValue()))
           .andExpect(jsonPath("$.name").value("Công ty TNHH ABC"));

        // list
        mvc.perform(get("/api/v1/suppliers")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void create_whenInvalid_shouldReturn400() throws Exception {
        var invalid = """
            { "name": "", "contactInfo": "" }
        """;

        mvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error", containsString("Validation")))
           .andExpect(jsonPath("$.fieldErrors.name", not(emptyString())));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/v1/suppliers/999")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void update_shouldWorkWithRealMySQL() throws Exception {
        // Create supplier
        var createBody = """
            { "name": "Supplier Original", "contactInfo": "original@example.com" }
        """;

        var createResult = mvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
           .andExpect(status().isOk())
           .andReturn();

        var supplierId = createResult.getResponse().getContentAsString()
                .substring(createResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Update supplier
        var updateBody = """
            { "name": "Supplier Updated", "contactInfo": "updated@example.com, phone: 9999999999" }
        """;

        mvc.perform(patch("/api/v1/suppliers/" + supplierId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("Supplier Updated"))
           .andExpect(jsonPath("$.contactInfo").value("updated@example.com, phone: 9999999999"));
    }

    @Test
    void delete_shouldWorkWithRealMySQL() throws Exception {
        // Create supplier
        var createBody = """
            { "name": "Supplier To Delete", "contactInfo": "delete@example.com" }
        """;

        var createResult = mvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
           .andExpect(status().isOk())
           .andReturn();

        var supplierId = createResult.getResponse().getContentAsString()
                .substring(createResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Delete supplier
        mvc.perform(delete("/api/v1/suppliers/" + supplierId))
           .andExpect(status().isNoContent());

        // Verify deletion
        mvc.perform(get("/api/v1/suppliers/" + supplierId)
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void listWithPagination_shouldWorkWithRealMySQL() throws Exception {
        // Create multiple suppliers
        for (int i = 1; i <= 5; i++) {
            var body = """
                { "name": "Supplier %d", "contactInfo": "supplier%d@example.com" }
            """.formatted(i, i);

            mvc.perform(post("/api/v1/suppliers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
               .andExpect(status().isOk());
        }

        // Test pagination
        mvc.perform(get("/api/v1/suppliers/page?page=0&size=3&sort=id,desc")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items", hasSize(3)))
           .andExpect(jsonPath("$.page").value(0))
           .andExpect(jsonPath("$.size").value(3));
    }
}
