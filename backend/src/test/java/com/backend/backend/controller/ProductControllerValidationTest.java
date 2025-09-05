package com.backend.backend.controller;

import com.backend.backend.dto.product.ProductCreateRequest;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ProductController.class, useDefaultFilters = false)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ProductController Validation Tests")
class ProductControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductCreateRequest validRequest;
    private ProductResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = new ProductCreateRequest();
        validRequest.setName("iPhone 15 Pro");
        validRequest.setDescription("Latest iPhone model");
        validRequest.setPrice(new BigDecimal("1299.99"));
        validRequest.setQuantityInStock(50);

        mockResponse = ProductResponse.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .description("Latest iPhone model")
                .price(new BigDecimal("1299.99"))
                .quantityInStock(50)
                .build();
    }

    @Test
    @DisplayName("Should create product successfully with valid data")
    void create_withValidData_shouldReturnCreated() throws Exception {
        // Arrange
        given(productService.create(any(ProductCreateRequest.class))).willReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.quantityInStock").value(50))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return 400 when name is blank")
    void create_whenNameIsBlank_shouldReturn400WithFieldError() throws Exception {
        // Arrange
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setName(""); // Invalid: blank name
        invalidRequest.setDescription("Valid description");
        invalidRequest.setPrice(new BigDecimal("100.00"));
        invalidRequest.setQuantityInStock(10);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasKey("name")))
                .andExpect(jsonPath("$.fieldErrors.name").value("Tên sản phẩm là bắt buộc"))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return 400 when price is negative")
    void create_whenPriceIsNegative_shouldReturn400WithFieldError() throws Exception {
        // Arrange
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setName("Valid Product");
        invalidRequest.setDescription("Valid description");
        invalidRequest.setPrice(new BigDecimal("-10.00")); // Invalid: negative price
        invalidRequest.setQuantityInStock(10);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasKey("price")))
                .andExpect(jsonPath("$.fieldErrors.price").value("Giá phải lớn hơn hoặc bằng 0"))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return 400 when quantity is negative")
    void create_whenQuantityIsNegative_shouldReturn400WithFieldError() throws Exception {
        // Arrange
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setName("Valid Product");
        invalidRequest.setDescription("Valid description");
        invalidRequest.setPrice(new BigDecimal("100.00"));
        invalidRequest.setQuantityInStock(-5); // Invalid: negative quantity

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasKey("quantityInStock")))
                .andExpect(jsonPath("$.fieldErrors.quantityInStock").value("Số lượng tồn kho phải lớn hơn hoặc bằng 0"))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return 400 when name is too long")
    void create_whenNameIsTooLong_shouldReturn400WithFieldError() throws Exception {
        // Arrange
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setName("A".repeat(101)); // Invalid: name too long (>100 chars)
        invalidRequest.setDescription("Valid description");
        invalidRequest.setPrice(new BigDecimal("100.00"));
        invalidRequest.setQuantityInStock(10);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasKey("name")))
                .andExpect(jsonPath("$.fieldErrors.name").value("Tên sản phẩm không được vượt quá 100 ký tự"))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return 400 when description is too long")
    void create_whenDescriptionIsTooLong_shouldReturn400WithFieldError() throws Exception {
        // Arrange
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setName("Valid Product");
        invalidRequest.setDescription("A".repeat(501)); // Invalid: description too long (>500 chars)
        invalidRequest.setPrice(new BigDecimal("100.00"));
        invalidRequest.setQuantityInStock(10);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasKey("description")))
                .andExpect(jsonPath("$.fieldErrors.description").value("Mô tả không được vượt quá 500 ký tự"))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return 400 with multiple field errors")
    void create_withMultipleValidationErrors_shouldReturn400WithAllFieldErrors() throws Exception {
        // Arrange
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        invalidRequest.setName(""); // Invalid: blank name
        invalidRequest.setDescription("Valid description");
        invalidRequest.setPrice(new BigDecimal("-10.00")); // Invalid: negative price
        invalidRequest.setQuantityInStock(-5); // Invalid: negative quantity

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasKey("name")))
                .andExpect(jsonPath("$.fieldErrors", hasKey("price")))
                .andExpect(jsonPath("$.fieldErrors", hasKey("quantityInStock")))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return 400 when required fields are null")
    void create_whenRequiredFieldsAreNull_shouldReturn400WithFieldErrors() throws Exception {
        // Arrange
        ProductCreateRequest invalidRequest = new ProductCreateRequest();
        // All required fields are null

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasKey("name")))
                .andExpect(jsonPath("$.fieldErrors", hasKey("price")))
                .andExpect(jsonPath("$.fieldErrors", hasKey("quantityInStock")))
                .andDo(print());
    }
}
