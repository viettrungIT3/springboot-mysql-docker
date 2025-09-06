package com.backend.backend.controller;

import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderItemControllerIT extends IntegrationTestBase {

    @Autowired private MockMvc mvc;

    @Test
    void create_then_list_shouldPersistIntoMySQL() throws Exception {
        // Create dependencies first
        var customerBody = """
            { "name": "OrderItem Customer", "contactInfo": "customer@example.com" }
        """;

        var customerResult = mvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerBody))
           .andExpect(status().isOk())
           .andReturn();

        var customerId = customerResult.getResponse().getContentAsString()
                .substring(customerResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var productBody = """
            { "name": "OrderItem Product", "description": "Test Product", "price": 50.00, "quantityInStock": 100 }
        """;

        var productResult = mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productBody))
           .andExpect(status().isOk())
           .andReturn();

        var productId = productResult.getResponse().getContentAsString()
                .substring(productResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var orderBody = """
            { 
                "customerId": %s, 
                "orderDate": "2024-01-01T10:00:00Z", 
                "totalAmount": 100.00 
            }
        """.formatted(customerId);

        var orderResult = mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody))
           .andExpect(status().isOk())
           .andReturn();

        var orderId = orderResult.getResponse().getContentAsString()
                .substring(orderResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create order item
        var orderItemBody = """
            { 
                "orderId": %s, 
                "productId": %s, 
                "quantity": 2, 
                "price": 50.00 
            }
        """.formatted(orderId, productId);

        mvc.perform(post("/api/v1/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderItemBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", notNullValue()))
           .andExpect(jsonPath("$.quantity").value(2))
           .andExpect(jsonPath("$.price").value(50.00));

        // list with pagination/sort
        mvc.perform(get("/api/v1/order-items/page?page=0&size=10&sort=id,desc")
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
                "orderId": 999, 
                "productId": 999, 
                "quantity": -1, 
                "price": -50.00 
            }
        """;

        mvc.perform(post("/api/v1/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error", containsString("Validation")));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/v1/order-items/999")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void update_shouldWorkWithRealMySQL() throws Exception {
        // Create dependencies
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

        var productBody = """
            { "name": "Update Product", "description": "Update Description", "price": 75.00, "quantityInStock": 50 }
        """;

        var productResult = mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productBody))
           .andExpect(status().isOk())
           .andReturn();

        var productId = productResult.getResponse().getContentAsString()
                .substring(productResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var orderBody = """
            { 
                "customerId": %s, 
                "orderDate": "2024-01-01T10:00:00Z", 
                "totalAmount": 150.00 
            }
        """.formatted(customerId);

        var orderResult = mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody))
           .andExpect(status().isOk())
           .andReturn();

        var orderId = orderResult.getResponse().getContentAsString()
                .substring(orderResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create order item
        var createOrderItemBody = """
            { 
                "orderId": %s, 
                "productId": %s, 
                "quantity": 1, 
                "price": 75.00 
            }
        """.formatted(orderId, productId);

        var orderItemResult = mvc.perform(post("/api/v1/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOrderItemBody))
           .andExpect(status().isOk())
           .andReturn();

        var orderItemId = orderItemResult.getResponse().getContentAsString()
                .substring(orderItemResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Update order item
        var updateBody = """
            { 
                "orderId": %s, 
                "productId": %s, 
                "quantity": 3, 
                "price": 70.00 
            }
        """.formatted(orderId, productId);

        mvc.perform(patch("/api/v1/order-items/" + orderItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.quantity").value(3))
           .andExpect(jsonPath("$.price").value(70.00));
    }

    @Test
    void delete_shouldWorkWithRealMySQL() throws Exception {
        // Create dependencies
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

        var productBody = """
            { "name": "Delete Product", "description": "Delete Description", "price": 25.00, "quantityInStock": 10 }
        """;

        var productResult = mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productBody))
           .andExpect(status().isOk())
           .andReturn();

        var productId = productResult.getResponse().getContentAsString()
                .substring(productResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var orderBody = """
            { 
                "customerId": %s, 
                "orderDate": "2024-01-01T10:00:00Z", 
                "totalAmount": 50.00 
            }
        """.formatted(customerId);

        var orderResult = mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody))
           .andExpect(status().isOk())
           .andReturn();

        var orderId = orderResult.getResponse().getContentAsString()
                .substring(orderResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create order item
        var createOrderItemBody = """
            { 
                "orderId": %s, 
                "productId": %s, 
                "quantity": 2, 
                "price": 25.00 
            }
        """.formatted(orderId, productId);

        var orderItemResult = mvc.perform(post("/api/v1/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOrderItemBody))
           .andExpect(status().isOk())
           .andReturn();

        var orderItemId = orderItemResult.getResponse().getContentAsString()
                .substring(orderItemResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Delete order item
        mvc.perform(delete("/api/v1/order-items/" + orderItemId))
           .andExpect(status().isNoContent());

        // Verify deletion
        mvc.perform(get("/api/v1/order-items/" + orderItemId)
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }
}
