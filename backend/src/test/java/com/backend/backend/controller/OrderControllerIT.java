package com.backend.backend.controller;

import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerIT extends IntegrationTestBase {

    @Autowired private MockMvc mvc;

    @Test
    void create_then_list_shouldPersistIntoMySQL() throws Exception {
        // First create a customer
        var customerBody = """
            { "name": "Order Customer", "contactInfo": "customer@example.com" }
        """;

        var customerResult = mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerBody))
           .andExpect(status().isOk())
           .andReturn();

        var customerId = customerResult.getResponse().getContentAsString()
                .substring(customerResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create order
        var orderBody = """
            { 
                "customerId": %s, 
                "orderDate": "2024-01-01T10:00:00Z", 
                "totalAmount": 150.00 
            }
        """.formatted(customerId);

        mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", notNullValue()))
           .andExpect(jsonPath("$.totalAmount").value(150.00));

        // list with pagination/sort
        mvc.perform(get("/api/v1/orders/page?page=0&size=10&sort=id,desc")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items", not(empty())))
           .andExpect(jsonPath("$.page").value(0))
           .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void create_whenInvalid_shouldReturn400() throws Exception {
        var invalid = """
            { 
                "customerId": 999, 
                "orderDate": "invalid-date", 
                "totalAmount": -100.00 
            }
        """;

        mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error", containsString("Validation")));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/v1/orders/999")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void update_shouldWorkWithRealMySQL() throws Exception {
        // Create customer and order
        var customerBody = """
            { "name": "Update Customer", "contactInfo": "update@example.com" }
        """;

        var customerResult = mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerBody))
           .andExpect(status().isOk())
           .andReturn();

        var customerId = customerResult.getResponse().getContentAsString()
                .substring(customerResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var createOrderBody = """
            { 
                "customerId": %s, 
                "orderDate": "2024-01-01T10:00:00Z", 
                "totalAmount": 100.00 
            }
        """.formatted(customerId);

        var orderResult = mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOrderBody))
           .andExpect(status().isOk())
           .andReturn();

        var orderId = orderResult.getResponse().getContentAsString()
                .substring(orderResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Update order
        var updateBody = """
            { 
                "customerId": %s, 
                "orderDate": "2024-01-02T10:00:00Z", 
                "totalAmount": 200.00 
            }
        """.formatted(customerId);

        mvc.perform(patch("/api/v1/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalAmount").value(200.00));
    }

    @Test
    void delete_shouldWorkWithRealMySQL() throws Exception {
        // Create customer and order
        var customerBody = """
            { "name": "Delete Customer", "contactInfo": "delete@example.com" }
        """;

        var customerResult = mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerBody))
           .andExpect(status().isOk())
           .andReturn();

        var customerId = customerResult.getResponse().getContentAsString()
                .substring(customerResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var createOrderBody = """
            { 
                "customerId": %s, 
                "orderDate": "2024-01-01T10:00:00Z", 
                "totalAmount": 100.00 
            }
        """.formatted(customerId);

        var orderResult = mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOrderBody))
           .andExpect(status().isOk())
           .andReturn();

        var orderId = orderResult.getResponse().getContentAsString()
                .substring(orderResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Delete order
        mvc.perform(delete("/api/v1/orders/" + orderId))
           .andExpect(status().isNoContent());

        // Verify deletion
        mvc.perform(get("/api/v1/orders/" + orderId)
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getOrdersByCustomer_shouldWorkWithRealMySQL() throws Exception {
        // Create customer
        var customerBody = """
            { "name": "Customer Orders", "contactInfo": "orders@example.com" }
        """;

        var customerResult = mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerBody))
           .andExpect(status().isOk())
           .andReturn();

        var customerId = customerResult.getResponse().getContentAsString()
                .substring(customerResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create multiple orders for the customer
        for (int i = 1; i <= 3; i++) {
            var orderBody = """
                { 
                    "customerId": %s, 
                    "orderDate": "2024-01-0%dT10:00:00Z", 
                    "totalAmount": %d00.00 
                }
            """.formatted(customerId, i, i);

            mvc.perform(post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(orderBody))
               .andExpect(status().isOk());
        }

        // Get orders by customer
        mvc.perform(get("/api/v1/orders/customer/" + customerId)
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(3)));
    }
}
