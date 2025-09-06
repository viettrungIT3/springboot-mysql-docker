package com.backend.backend.controller;

import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StockEntryControllerIT extends IntegrationTestBase {

    @Autowired private MockMvc mvc;

    @Test
    void create_then_list_shouldPersistIntoMySQL() throws Exception {
        // Create dependencies first
        var productBody = """
            { "name": "Stock Product", "description": "Stock Description", "price": 100.00, "quantityInStock": 50 }
        """;

        var productResult = mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productBody))
           .andExpect(status().isOk())
           .andReturn();

        var productId = productResult.getResponse().getContentAsString()
                .substring(productResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var supplierBody = """
            { "name": "Stock Supplier", "contactInfo": "stock@supplier.com" }
        """;

        var supplierResult = mvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(supplierBody))
           .andExpect(status().isOk())
           .andReturn();

        var supplierId = supplierResult.getResponse().getContentAsString()
                .substring(supplierResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create stock entry
        var stockEntryBody = """
            { 
                "productId": %s, 
                "supplierId": %s, 
                "quantity": 10, 
                "entryDate": "2024-01-01T10:00:00Z" 
            }
        """.formatted(productId, supplierId);

        mvc.perform(post("/api/v1/stock-entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stockEntryBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", notNullValue()))
           .andExpect(jsonPath("$.quantity").value(10));

        // list with pagination/sort
        mvc.perform(get("/api/v1/stock-entries/page?page=0&size=10&sort=id,desc")
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
                "productId": 999, 
                "supplierId": 999, 
                "quantity": -5, 
                "entryDate": "invalid-date" 
            }
        """;

        mvc.perform(post("/api/v1/stock-entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error", containsString("Validation")));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        mvc.perform(get("/api/v1/stock-entries/999")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void update_shouldWorkWithRealMySQL() throws Exception {
        // Create dependencies
        var productBody = """
            { "name": "Update Product", "description": "Update Description", "price": 75.00, "quantityInStock": 25 }
        """;

        var productResult = mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productBody))
           .andExpect(status().isOk())
           .andReturn();

        var productId = productResult.getResponse().getContentAsString()
                .substring(productResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var supplierBody = """
            { "name": "Update Supplier", "contactInfo": "update@supplier.com" }
        """;

        var supplierResult = mvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(supplierBody))
           .andExpect(status().isOk())
           .andReturn();

        var supplierId = supplierResult.getResponse().getContentAsString()
                .substring(supplierResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create stock entry
        var createStockEntryBody = """
            { 
                "productId": %s, 
                "supplierId": %s, 
                "quantity": 5, 
                "entryDate": "2024-01-01T10:00:00Z" 
            }
        """.formatted(productId, supplierId);

        var stockEntryResult = mvc.perform(post("/api/v1/stock-entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createStockEntryBody))
           .andExpect(status().isOk())
           .andReturn();

        var stockEntryId = stockEntryResult.getResponse().getContentAsString()
                .substring(stockEntryResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Update stock entry
        var updateBody = """
            { 
                "productId": %s, 
                "supplierId": %s, 
                "quantity": 25, 
                "entryDate": "2024-01-02T10:00:00Z" 
            }
        """.formatted(productId, supplierId);

        mvc.perform(patch("/api/v1/stock-entries/" + stockEntryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.quantity").value(25));
    }

    @Test
    void delete_shouldWorkWithRealMySQL() throws Exception {
        // Create dependencies
        var productBody = """
            { "name": "Delete Product", "description": "Delete Description", "price": 30.00, "quantityInStock": 15 }
        """;

        var productResult = mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productBody))
           .andExpect(status().isOk())
           .andReturn();

        var productId = productResult.getResponse().getContentAsString()
                .substring(productResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var supplierBody = """
            { "name": "Delete Supplier", "contactInfo": "delete@supplier.com" }
        """;

        var supplierResult = mvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(supplierBody))
           .andExpect(status().isOk())
           .andReturn();

        var supplierId = supplierResult.getResponse().getContentAsString()
                .substring(supplierResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create stock entry
        var createStockEntryBody = """
            { 
                "productId": %s, 
                "supplierId": %s, 
                "quantity": 8, 
                "entryDate": "2024-01-01T10:00:00Z" 
            }
        """.formatted(productId, supplierId);

        var stockEntryResult = mvc.perform(post("/api/v1/stock-entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createStockEntryBody))
           .andExpect(status().isOk())
           .andReturn();

        var stockEntryId = stockEntryResult.getResponse().getContentAsString()
                .substring(stockEntryResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Delete stock entry
        mvc.perform(delete("/api/v1/stock-entries/" + stockEntryId))
           .andExpect(status().isNoContent());

        // Verify deletion
        mvc.perform(get("/api/v1/stock-entries/" + stockEntryId)
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void list_shouldWorkWithRealMySQL() throws Exception {
        // Create dependencies
        var productBody = """
            { "name": "List Product", "description": "List Description", "price": 50.00, "quantityInStock": 20 }
        """;

        var productResult = mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productBody))
           .andExpect(status().isOk())
           .andReturn();

        var productId = productResult.getResponse().getContentAsString()
                .substring(productResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        var supplierBody = """
            { "name": "List Supplier", "contactInfo": "list@supplier.com" }
        """;

        var supplierResult = mvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(supplierBody))
           .andExpect(status().isOk())
           .andReturn();

        var supplierId = supplierResult.getResponse().getContentAsString()
                .substring(supplierResult.getResponse().getContentAsString().indexOf("\"id\":") + 6)
                .split(",")[0];

        // Create multiple stock entries
        for (int i = 1; i <= 3; i++) {
            var stockEntryBody = """
                { 
                    "productId": %s, 
                    "supplierId": %s, 
                    "quantity": %d, 
                    "entryDate": "2024-01-0%dT10:00:00Z" 
                }
            """.formatted(productId, supplierId, i * 5, i);

            mvc.perform(post("/api/v1/stock-entries")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(stockEntryBody))
               .andExpect(status().isOk());
        }

        // List all stock entries
        mvc.perform(get("/api/v1/stock-entries")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$", hasSize(3)));
    }
}
