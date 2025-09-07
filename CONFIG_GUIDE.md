# 🎯 Centralized Configuration Guide

## Tổng quan
Project này đã được cấu hình để **chỉ cần sửa 1 chỗ duy nhất** - file `.env` - để thay đổi tất cả cấu hình của hệ thống.

## 🚀 Cách sử dụng

### 1. Thay đổi cấu hình nhanh
```bash
# Mở Configuration Manager (giao diện thân thiện)
make config

# Hoặc xem cấu hình hiện tại
make config-show

# Backup cấu hình trước khi thay đổi
make config-backup
```

### 2. Thay đổi cấu hình thủ công
Chỉ cần sửa file `.env`:
```bash
# Ví dụ: Thay đổi port backend từ 8081 → 8082
BACKEND_PORT=8082

# Ví dụ: Thay đổi port MySQL từ 3307 → 3308  
MYSQL_PORT=3308
```

### 3. Áp dụng thay đổi
```bash
# Restart toàn bộ hệ thống để áp dụng cấu hình mới
make restart
```

## 📋 Các biến cấu hình chính

| Biến | Mô tả | Mặc định |
|------|-------|----------|
| `BACKEND_PORT` | Port của Spring Boot API | 8081 |
| `MYSQL_PORT` | Port của MySQL database | 3307 |
| `MYSQL_DATABASE` | Tên database | appdb |
| `MYSQL_USER` | Username database | appuser |
| `MYSQL_PASSWORD` | Password database | apppass |
| `SPRING_PROFILES_ACTIVE` | Spring profile | dev |
| `APP_SEED_PRODUCTS` | Số sản phẩm seed | 15 |
| `APP_SEED_CUSTOMERS` | Số khách hàng seed | 10 |

## 🔄 Các file tự động cập nhật

Khi bạn thay đổi `.env`, các file sau sẽ tự động sử dụng cấu hình mới:

- ✅ `docker-compose.yml` - Port mapping và environment variables
- ✅ `backend/src/main/resources/application-dev.yml` - Spring Boot server port
- ✅ `backend/src/main/java/.../OpenApiConfig.java` - Swagger server URL
- ✅ `makefile` - Tất cả các lệnh sử dụng biến từ .env

## 🎯 Ví dụ thực tế

### Thay đổi port backend từ 8081 → 8082:

1. **Sửa file .env:**
   ```bash
   BACKEND_PORT=8082
   ```

2. **Restart hệ thống:**
   ```bash
   make restart
   ```

3. **Kiểm tra:**
   ```bash
   make test-swagger  # Swagger UI sẽ ở http://localhost:8082/swagger-ui/index.html
   ```

### Thay đổi database từ appdb → myapp:

1. **Sửa file .env:**
   ```bash
   MYSQL_DATABASE=myapp
   ```

2. **Restart hệ thống:**
   ```bash
   make restart
   ```

## 🛡️ Backup & Recovery

### 📁 Folder Backup
Tất cả backup được lưu trong folder `backups/env/` để tránh làm lộn xộn thư mục gốc.

### 🔧 Các lệnh quản lý backup:

```bash
# Backup cấu hình hiện tại
make config-backup

# Liệt kê tất cả backup có sẵn
make config-list-backups

# Khôi phục từ backup cụ thể
make config-restore BACKUP=.env.backup.20240907_032000

# Xóa backup cũ (giữ lại 5 file gần nhất)
make config-clean-backups

# Quản lý backup với giao diện thân thiện
make config  # Chọn option 10
```

### 📋 Ví dụ khôi phục:
```bash
# 1. Xem danh sách backup
make config-list-backups

# 2. Khôi phục từ backup cụ thể
make config-restore BACKUP=.env.backup.20240907_113905

# 3. Restart để áp dụng
make restart
```

## 🚨 Lưu ý quan trọng

1. **Luôn backup trước khi thay đổi:** `make config-backup`
2. **Restart sau khi thay đổi:** `make restart`
3. **Kiểm tra sau khi thay đổi:** `make test-swagger` và `make test-api`
4. **Port không được trùng:** Đảm bảo BACKEND_PORT và MYSQL_PORT không trùng với các service khác

## 📁 Cấu trúc thư mục

```
springboot-mysql-docker/
├── .env                    # 🎯 File cấu hình chính (chỉ sửa file này)
├── backups/
│   └── env/               # 📁 Folder chứa backup cấu hình
│       ├── .env.backup.20240907_113905
│       ├── .env.backup.20240907_113918
│       └── ...
├── config-manager.sh      # 🛠️ Script quản lý cấu hình
├── CONFIG_GUIDE.md        # 📖 Hướng dẫn này
└── makefile              # ⚙️ Các lệnh tiện ích
```

## 🎉 Lợi ích

- ✅ **Chỉ sửa 1 file** thay vì 4-5 file
- ✅ **Tự động đồng bộ** tất cả cấu hình
- ✅ **Giao diện thân thiện** với `make config`
- ✅ **Backup tự động** vào folder riêng biệt
- ✅ **Quản lý backup thông minh** (tự động xóa cũ)
- ✅ **Validation** và kiểm tra sau khi thay đổi
- ✅ **Không làm lộn xộn** thư mục gốc
- ✅ **Không cần nhớ** nhiều file cấu hình khác nhau
