# Day 7 - Unit Test Cơ Bản ✅

## Mục tiêu hoàn thành
- ✅ Thêm JUnit 5 + Mockito cho service layer
- ✅ Coverage targeting > 50% cho service cốt lõi 
- ✅ Test validation và error handling
- ✅ JaCoCo coverage reporting

## Các thay đổi thực hiện

### 1. Cập nhật Build Configuration

**File:** `backend/build.gradle`

**Thêm JaCoCo Plugin:**
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.4'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'jacoco'  // ✅ Thêm JaCoCo plugin
}
```

**Test Dependencies đã có sẵn:**
```gradle
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'
testAnnotationProcessor 'org.mapstruct:mapstruct-processor:1.6.2'
```

**JaCoCo Configuration:**
```gradle
tasks.named('test') {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    reports {
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir('reports/jacoco/test/html')
    }
    finalizedBy jacocoTestCoverageVerification
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.50 // 50% coverage target
            }
        }
    }
}
```

### 2. Service Layer Unit Tests

**File:** `backend/src/test/java/com/backend/backend/service/ProductServiceTest.java`

**Highlights:**
- ✅ Comprehensive test coverage cho ProductService
- ✅ Mocking với @Mock và @InjectMocks
- ✅ Test các scenarios: create, read, update, delete, pagination, search
- ✅ Exception handling tests
- ✅ MockedStatic cho PageMapper utility
- ✅ AssertJ assertions cho readable tests

**Test Cases Covered:**
```java
// CRUD Operations
✅ create_shouldReturnMappedResponse()
✅ getById_shouldReturnProductWhenExists()
✅ getById_shouldThrowExceptionWhenNotFound()
✅ update_shouldUpdateAndReturnProduct()
✅ update_shouldThrowExceptionWhenNotFound()
✅ delete_shouldCallRepositoryDelete()
✅ delete_shouldThrowExceptionWhenNotFound()

// Business Logic
✅ search_withKeyword_shouldReturnFilteredResults()
✅ list_withPagination_shouldReturnPagedResults()
```

### 3. Controller Validation Tests

**File:** `backend/src/test/java/com/backend/backend/controller/ProductControllerValidationTest.java`

**Highlights:**
- ✅ @WebMvcTest cho slice testing
- ✅ MockMvc cho HTTP testing
- ✅ @MockitoBean thay vì deprecated @MockBean
- ✅ JSON content validation
- ✅ HTTP status code assertions

**Validation Test Cases:**
```java
✅ createProduct_withEmptyName_shouldReturnBadRequest()
✅ createProduct_withNegativePrice_shouldReturnBadRequest()
✅ createProduct_withNegativeStock_shouldReturnBadRequest()
✅ createProduct_withValidData_shouldReturnCreated()
```

### 4. Bug Fixes Thực Hiện

#### 4.1 PageResponse Constructor Issue
**Vấn đề:** PageResponse chỉ có @Builder, thiếu default constructor

**Sửa:** `backend/src/main/java/com/backend/backend/dto/common/PageResponse.java`
```java
@Data
@Builder
@NoArgsConstructor  // ✅ Thêm
@AllArgsConstructor // ✅ Thêm
public class PageResponse<T> {
    // ...
}
```

#### 4.2 Deprecated MockBean
**Vấn đề:** @MockBean deprecated trong Spring Boot 3.4+

**Sửa:** Thay thế với @MockitoBean
```java
// Before
import org.springframework.boot.test.mock.mockito.MockBean;
@MockBean private ProductService productService;

// After ✅  
import org.springframework.test.context.bean.override.mockito.MockitoBean;
@MockitoBean private ProductService productService;
```

#### 4.3 MapStruct Configuration
**Vấn đề:** MapStruct implementations không được generate cho test

**Sửa:** Thêm test annotation processor
```gradle
testAnnotationProcessor 'org.mapstruct:mapstruct-processor:1.6.2'
```

### 5. Docker Test Setup

**File:** `backend/Dockerfile.test`
- ✅ Dedicated test container configuration
- ✅ Gradle cache optimization
- ✅ Multi-stage build for testing

**File:** `docker-compose.yml`
- ✅ Thêm test-runner profile
- ✅ Volume mount cho test reports

**File:** `makefile` 
- ✅ `make unit-test` command
- ✅ `make unit-test-watch` for development
- ✅ `make coverage-report` để xem coverage

### 6. Test Execution Commands

```bash
# Chạy unit tests với coverage
make unit-test

# Xem coverage report
make coverage-report

# Test trong development mode (watch changes)
make unit-test-watch

# Test specific class
make unit-test-class CLASS=ProductServiceTest
```

### 7. Coverage Reports

Sau khi chạy tests, coverage reports sẽ có tại:
- **HTML:** `backend/build/reports/jacoco/test/html/index.html`
- **Console:** Hiển thị summary trong terminal

**Target Coverage:** ≥ 50% cho service layer

## 🔧 Lỗi đã sửa trong quá trình

1. **Generic Type Inference Error**: PageResponse constructor issues
2. **Deprecated API Warning**: MockBean → MockitoBean migration  
3. **MapStruct Implementation**: Annotation processor configuration
4. **Test Container Setup**: Docker configuration cho testing

## 📋 Best Practices Applied

- ✅ **Test Naming**: Descriptive method names với given_when_then pattern
- ✅ **Arrange-Act-Assert**: Clear test structure
- ✅ **Mockito Best Practices**: @ExtendWith(MockitoExtension.class)
- ✅ **AssertJ**: Fluent assertions cho readability
- ✅ **Test Isolation**: Mỗi test independent
- ✅ **Edge Cases**: Null handling, not found scenarios
- ✅ **MockedStatic**: Proper static method mocking cleanup

## 🚀 Highlights

### Test Coverage Areas
1. **Service Logic**: Create, Read, Update, Delete operations
2. **Validation**: Input parameter validation 
3. **Exception Handling**: ResourceNotFoundException scenarios
4. **Business Logic**: Search, pagination, filtering
5. **Integration Points**: Repository và Mapper interactions

### Production-Ready Features
- JaCoCo integration với coverage thresholds
- CI/CD ready test configuration
- Docker-based test execution  
- Coverage reporting và metrics
- Comprehensive test suite foundation

## ➡️ Next Steps (Day 8)

Với foundation testing được thiết lập, Day 8 có thể focus vào:
- Integration tests với @SpringBootTest
- Repository testing với @DataJpaTest  
- Test containers cho database testing
- API integration tests với TestRestTemplate

---

**🎯 Day 7 Goal Achieved:** Unit testing foundation với JUnit 5 + Mockito đã được thiết lập thành công với coverage > 50% target và comprehensive test suite cho service layer.
