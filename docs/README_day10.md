# Day 10 — Data Seeding với Docker & Makefile 🌱

## Tổng quan
Implement tính năng tự động seed dữ liệu mẫu cho môi trường development và testing, sử dụng Docker và Makefile để quản lý.

## Kiến trúc giải pháp

### 🏗️ **Thành phần chính:**
- **DataFaker**: Thư viện tạo dữ liệu ngẫu nhiên thực tế
- **Spring Profiles**: Quản lý cấu hình theo môi trường (dev/test/prod)
- **CommandLineRunner**: Chạy seeding khi ứng dụng khởi động
- **Configuration Properties**: Cấu hình linh hoạt qua YAML và environment variables

### 🔧 **Luồng hoạt động:**
1. Application khởi động với profile `dev` hoặc `test`
2. Spring Boot load `DevTestDataSeeder` component
3. Seeder kiểm tra `app.seed.enabled=true`
4. Tạo dữ liệu ngẫu nhiên với DataFaker
5. Kiểm tra idempotent trước khi lưu database
6. Commit transaction và log kết quả

## Implementation chi tiết

### 1. **Dependency Management**
```gradle
// backend/build.gradle
implementation 'net.datafaker:datafaker:2.3.1'
```

### 2. **Profile Configuration**
```yaml
# application-dev.yml
app:
  seed:
    enabled: true
    products: 15
    customers: 10

# application-test.yml  
app:
  seed:
    enabled: true
    products: 5
    customers: 5

# application-prod.yml
app:
  seed:
    enabled: false
```

### 3. **Configuration Properties**
```java
// SeedProperties.java
@ConfigurationProperties(prefix = "app.seed")
public class SeedProperties {
    private boolean enabled = true;
    private int products = 10;
    private int customers = 10;
}
```

### 4. **Repository Enhancement**
```java
// ProductRepository.java & CustomerRepository.java
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    boolean existsByName(String name);
}
```

### 5. **Data Seeder Implementation**
```java
@Component
@Profile({"dev", "test"})
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class DevTestDataSeeder implements CommandLineRunner {
    
    @Transactional
    public void run(String... args) {
        Faker faker = new Faker(new Locale("en"), new Random(12345));
        seedProducts(faker, props.getProducts());
        seedCustomers(faker, props.getCustomers());
    }
}
```

## Sử dụng với Docker & Makefile

### 🚀 **Khởi động Development Environment**
```bash
# Tạo file .env cho dev profile
cat > .env << 'EOF'
SPRING_PROFILES_ACTIVE=dev
APP_SEED_ENABLED=true
APP_SEED_PRODUCTS=15
APP_SEED_CUSTOMERS=10
EOF

# Khởi động services
make dev-start

# Xem logs seeding
make logs | grep "DevTestDataSeeder"
```

### 🧪 **Khởi động Test Environment**
```bash
# Tạo file .env cho test profile
cat > .env << 'EOF'
SPRING_PROFILES_ACTIVE=test
APP_SEED_ENABLED=true
APP_SEED_PRODUCTS=5
APP_SEED_CUSTOMERS=5
EOF

# Khởi động services
make dev-start
```

### 🏭 **Khởi động Production Environment**
```bash
# Tạo file .env cho prod profile
cat > .env << 'EOF'
SPRING_PROFILES_ACTIVE=prod
APP_SEED_ENABLED=false
EOF

# Khởi động services
make dev-start
```

## Kiểm tra kết quả

### 📊 **API Testing**
```bash
# Kiểm tra số lượng products
curl -s "http://localhost:8080/api/v1/products?page=0&size=5" | jq '.totalElements'

# Kiểm tra số lượng customers  
curl -s "http://localhost:8080/api/v1/customers?page=0&size=5" | jq '.totalElements'

# Xem dữ liệu mẫu
curl -s "http://localhost:8080/api/v1/products?page=0&size=3" | jq '.items[] | {name, price, quantityInStock}'
```

### 📝 **Log Monitoring**
```bash
# Xem logs seeding
docker compose logs backend | grep -i "seeding\|seeded"

# Xem logs chi tiết
docker compose logs backend | grep -A 5 -B 5 "DevTestDataSeeder"
```

## Tính năng nâng cao

### 🔄 **Idempotent Seeding**
- Kiểm tra `findByName()` trước khi tạo mới
- Không tạo dữ liệu trùng lặp khi restart
- Đảm bảo tính nhất quán của database

### 🎲 **Deterministic Data**
- Sử dụng `Random(12345)` để tạo dữ liệu nhất quán
- Phù hợp cho testing và development
- Có thể cấu hình seed number qua properties

### 🔧 **Environment Variables**
```bash
# Cấu hình qua Docker environment
APP_SEED_ENABLED=true
APP_SEED_PRODUCTS=20
APP_SEED_CUSTOMERS=15
```

## Troubleshooting

### ❌ **Seeding không chạy**
```bash
# Kiểm tra profile
docker compose logs backend | grep "SPRING_PROFILES_ACTIVE"

# Kiểm tra conditional property
docker compose logs backend | grep "DevTestDataSeeder matched"

# Kiểm tra environment variables
docker compose exec backend env | grep APP_SEED
```

### ❌ **Dữ liệu trùng lặp**
```bash
# Kiểm tra method findByName
docker compose logs backend | grep "findByName"

# Kiểm tra số lượng dữ liệu
curl -s "http://localhost:8080/api/v1/products" | jq '.totalElements'
```

### ❌ **DataFaker import error**
```bash
# Rebuild container để tải dependency mới
make dev-rebuild

# Kiểm tra build logs
docker compose logs backend | grep "datafaker"
```

## Mở rộng tương lai

### 🔮 **Tính năng có thể thêm:**
- **Admin seeding**: Tạo admin user với password hashed
- **Bulk seeding**: Seed dữ liệu lớn với batch processing
- **Custom seeders**: Tạo seeder cho các entity khác
- **Seed validation**: Kiểm tra tính hợp lệ của dữ liệu seed
- **Seed cleanup**: Xóa dữ liệu seed khi cần thiết

### 📈 **Performance optimization:**
- **Batch insert**: Sử dụng `saveAll()` thay vì `save()` từng item
- **Connection pooling**: Tối ưu database connections
- **Async seeding**: Chạy seeding bất đồng bộ

## Kết luận

✅ **Đã hoàn thành:**
- Profile-based seeding (dev/test/prod)
- Idempotent data creation
- Configurable seeding quantities
- Docker & Makefile integration
- Comprehensive logging và monitoring

🎯 **Lợi ích:**
- Tự động hóa việc tạo dữ liệu mẫu
- Môi trường development/test sẵn sàng ngay lập tức
- Không cần cài đặt Java trên máy local
- Dễ dàng switch giữa các môi trường
- Tính nhất quán cao cho team development