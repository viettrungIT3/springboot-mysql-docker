package com.backend.backend.controller;

import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdministratorControllerIT extends IntegrationTestBase {

    @Autowired private MockMvc mvc;

    @Test
    void create_then_list_shouldPersistIntoMySQL() throws Exception {
        var body = """
            { 
                "username": "admin1", 
                "password": "password123", 
                "email": "admin1@example.com", 
                "fullName": "Administrator One" 
            }
        """;

        // create
        mvc.perform(post("/api/v1/administrators")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", notNullValue()))
           .andExpect(jsonPath("$.username").value("admin1"))
           .andExpect(jsonPath("$.email").value("admin1@example.com"));

        // list
        mvc.perform(get("/api/v1/administrators")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void create_whenInvalid_shouldReturn400() throws Exception {
        var invalid = """
            { 
                "username": "", 
                "password": "", 
                "email": "invalid-email", 
                "fullName": "" 
            }
        """;

        mvc.perform(post("/api/v1/administrators")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error", containsString("Validation")))
           .andExpect(jsonPath("$.fieldErrors.username", not(emptyString())))
           .andExpect(jsonPath("$.fieldErrors.password", not(emptyString())))
           .andExpect(jsonPath("$.fieldErrors.email", not(emptyString())));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/v1/administrators/999")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void update_shouldWorkWithRealMySQL() throws Exception {
        // Create administrator
        var createBody = """
            { 
                "username": "updateuser", 
                "password": "password123", 
                "email": "updateuser@example.com", 
                "fullName": "Update User" 
            }
        """;

        var createResult = mvc.perform(post("/api/v1/administrators")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
           .andExpect(status().isOk())
           .andReturn();

        var adminId = createResult.getResponse().getContentAsString()
                .substring(createResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Update administrator
        var updateBody = """
            { 
                "username": "updateuser", 
                "password": "newpassword123", 
                "email": "updated@example.com", 
                "fullName": "Updated User" 
            }
        """;

        mvc.perform(patch("/api/v1/administrators/" + adminId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.email").value("updated@example.com"))
           .andExpect(jsonPath("$.fullName").value("Updated User"));
    }

    @Test
    void delete_shouldWorkWithRealMySQL() throws Exception {
        // Create administrator
        var createBody = """
            { 
                "username": "deleteuser", 
                "password": "password123", 
                "email": "deleteuser@example.com", 
                "fullName": "Delete User" 
            }
        """;

        var createResult = mvc.perform(post("/api/v1/administrators")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
           .andExpect(status().isOk())
           .andReturn();

        var adminId = createResult.getResponse().getContentAsString()
                .substring(createResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Delete administrator
        mvc.perform(delete("/api/v1/administrators/" + adminId))
           .andExpect(status().isNoContent());

        // Verify deletion
        mvc.perform(get("/api/v1/administrators/" + adminId)
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void listWithPagination_shouldWorkWithRealMySQL() throws Exception {
        // Create multiple administrators
        for (int i = 1; i <= 5; i++) {
            var body = """
                { 
                    "username": "admin%d", 
                    "password": "password%d", 
                    "email": "admin%d@example.com", 
                    "fullName": "Administrator %d" 
                }
            """.formatted(i, i, i, i);

            mvc.perform(post("/api/v1/administrators")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
               .andExpect(status().isOk());
        }

        // Test pagination
        mvc.perform(get("/api/v1/administrators/page?page=0&size=3&sort=id,desc")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items", hasSize(3)))
           .andExpect(jsonPath("$.page").value(0))
           .andExpect(jsonPath("$.size").value(3));
    }

    @Test
    void createWithDuplicateUsername_shouldReturn400() throws Exception {
        // Create first administrator
        var body1 = """
            { 
                "username": "duplicateuser", 
                "password": "password123", 
                "email": "user1@example.com", 
                "fullName": "User One" 
            }
        """;

        mvc.perform(post("/api/v1/administrators")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body1))
           .andExpect(status().isOk());

        // Try to create second administrator with same username
        var body2 = """
            { 
                "username": "duplicateuser", 
                "password": "password456", 
                "email": "user2@example.com", 
                "fullName": "User Two" 
            }
        """;

        mvc.perform(post("/api/v1/administrators")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body2))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error", containsString("already exists")));
    }
}
