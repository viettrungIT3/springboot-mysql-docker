-- Migration để cập nhật Administrator table cho JWT authentication
-- Đổi tên cột password thành password_hash và thêm cột role

-- Đổi tên cột password thành password_hash
ALTER TABLE administrators CHANGE COLUMN password password_hash VARCHAR(255) NOT NULL;

-- Thêm cột role với giá trị mặc định là MANAGER
ALTER TABLE administrators ADD COLUMN role ENUM('ADMIN', 'MANAGER', 'SALE') NOT NULL DEFAULT 'MANAGER';

-- Đặt role ADMIN cho user đầu tiên (nếu có)
UPDATE administrators SET role = 'ADMIN' WHERE id = 1;
