#!/bin/bash

# ============================================
# 🎯 CENTRALIZED CONFIGURATION SCRIPT
# ============================================
# Script này giúp bạn thay đổi cấu hình chỉ bằng 1 lệnh

# Màu sắc cho output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🎯 CENTRALIZED CONFIGURATION MANAGER${NC}"
echo "=================================="

# Function để hiển thị menu
show_menu() {
    echo -e "\n${YELLOW}Chọn cấu hình bạn muốn thay đổi:${NC}"
    echo "1. Backend Port (hiện tại: $(grep BACKEND_PORT .env | cut -d'=' -f2))"
    echo "2. MySQL Port (hiện tại: $(grep MYSQL_PORT .env | cut -d'=' -f2))"
    echo "3. Database Name (hiện tại: $(grep MYSQL_DATABASE .env | cut -d'=' -f2))"
    echo "4. Database User (hiện tại: $(grep MYSQL_USER .env | cut -d'=' -f2))"
    echo "5. Database Password (hiện tại: $(grep MYSQL_PASSWORD .env | cut -d'=' -f2))"
    echo "6. Spring Profile (hiện tại: $(grep SPRING_PROFILES_ACTIVE .env | cut -d'=' -f2))"
    echo "7. Seed Products Count (hiện tại: $(grep APP_SEED_PRODUCTS .env | cut -d'=' -f2))"
    echo "8. Seed Customers Count (hiện tại: $(grep APP_SEED_CUSTOMERS .env | cut -d'=' -f2))"
    echo "9. JWT Secret (hiện tại: $(grep JWT_SECRET .env | cut -d'=' -f2 | cut -c1-20)...)"
    echo "10. JWT Expiration (hiện tại: $(grep JWT_EXPIRATION .env | cut -d'=' -f2))"
    echo "11. Hiển thị tất cả cấu hình hiện tại"
    echo "12. Quản lý backup cấu hình"
    echo "0. Thoát"
    echo -e "\n${BLUE}Lưu ý: Sau khi thay đổi, chạy 'make restart' để áp dụng${NC}"
}

# Function để cập nhật giá trị
update_config() {
    local key=$1
    local current_value=$2
    local description=$3
    
    echo -e "\n${YELLOW}$description${NC}"
    echo -e "Giá trị hiện tại: ${GREEN}$current_value${NC}"
    read -p "Nhập giá trị mới (Enter để giữ nguyên): " new_value
    
    if [ ! -z "$new_value" ]; then
        # Backup file hiện tại vào folder backups/env
        mkdir -p backups/env
        cp .env backups/env/.env.backup.$(date +%Y%m%d_%H%M%S)
        
        # Cập nhật giá trị
        sed -i.tmp "s/^${key}=.*/${key}=${new_value}/" .env
        rm .env.tmp
        
        echo -e "${GREEN}✅ Đã cập nhật $key từ '$current_value' thành '$new_value'${NC}"
        echo -e "${BLUE}💾 Backup đã được lưu vào backups/env/${NC}"
    else
        echo -e "${YELLOW}⚠️  Giữ nguyên giá trị hiện tại${NC}"
    fi
}

# Function để hiển thị tất cả cấu hình
show_all_config() {
    echo -e "\n${BLUE}📋 TẤT CẢ CẤU HÌNH HIỆN TẠI:${NC}"
    echo "=================================="
    cat .env | grep -v "^#" | grep -v "^$" | while read line; do
        if [ ! -z "$line" ]; then
            key=$(echo $line | cut -d'=' -f1)
            value=$(echo $line | cut -d'=' -f2)
            echo -e "${GREEN}$key${NC} = ${YELLOW}$value${NC}"
        fi
    done
}

# Function để quản lý backup
manage_backups() {
    echo -e "\n${BLUE}💾 QUẢN LÝ BACKUP CẤU HÌNH${NC}"
    echo "=============================="
    echo "1. Tạo backup hiện tại"
    echo "2. Liệt kê tất cả backup"
    echo "3. Khôi phục từ backup"
    echo "4. Xóa backup cũ (giữ lại 5 file gần nhất)"
    echo "0. Quay lại menu chính"
    
    read -p "Chọn tùy chọn (0-4): " backup_choice
    
    case $backup_choice in
        1)
            mkdir -p backups/env
            cp .env backups/env/.env.backup.$(date +%Y%m%d_%H%M%S)
            echo -e "${GREEN}✅ Backup đã được tạo trong backups/env/${NC}"
            ;;
        2)
            echo -e "\n${YELLOW}📋 Available Backups:${NC}"
            if [ -d "backups/env" ] && [ "$(ls -A backups/env 2>/dev/null)" ]; then
                ls -la backups/env/.env.backup.* 2>/dev/null | while read line; do
                    filename=$(echo $line | awk '{print $9}')
                    date=$(echo $filename | sed 's/.*\.env\.backup\.//')
                    size=$(echo $line | awk '{print $5}')
                    echo -e "  📄 $filename ($size bytes) - $date"
                done
            else
                echo -e "  ❌ No backups found in backups/env/"
            fi
            ;;
        3)
            echo -e "\n${YELLOW}Available Backups:${NC}"
            if [ -d "backups/env" ] && [ "$(ls -A backups/env 2>/dev/null)" ]; then
                ls -t backups/env/.env.backup.* | head -5 | while read file; do
                    echo "  📄 $file"
                done
                echo ""
                read -p "Nhập tên file backup để khôi phục: " backup_file
                if [ -f "backups/env/$backup_file" ]; then
                    cp backups/env/$backup_file .env
                    echo -e "${GREEN}✅ Configuration restored from $backup_file${NC}"
                    echo -e "${BLUE}💡 Run 'make restart' to apply changes${NC}"
                else
                    echo -e "${RED}❌ Backup file '$backup_file' not found${NC}"
                fi
            else
                echo -e "  ❌ No backups found in backups/env/"
            fi
            ;;
        4)
            if [ -d "backups/env" ]; then
                count=$(ls backups/env/.env.backup.* 2>/dev/null | wc -l)
                if [ $count -gt 5 ]; then
                    ls -t backups/env/.env.backup.* | tail -n +6 | xargs rm -f
                    echo -e "${GREEN}✅ Removed $(($count - 5)) old backup(s)${NC}"
                else
                    echo -e "${YELLOW}✅ No old backups to clean ($count backups total)${NC}"
                fi
            else
                echo -e "${RED}❌ No backup directory found${NC}"
            fi
            ;;
        0)
            return
            ;;
        *)
            echo -e "${RED}❌ Lựa chọn không hợp lệ${NC}"
            ;;
    esac
    
    echo -e "\n${BLUE}Press Enter to continue...${NC}"
    read
}

# Main menu loop
while true; do
    show_menu
    read -p "Chọn tùy chọn (0-12): " choice
    
    case $choice in
        1)
            current=$(grep BACKEND_PORT .env | cut -d'=' -f2)
            update_config "BACKEND_PORT" "$current" "🌐 Backend Port Configuration"
            ;;
        2)
            current=$(grep MYSQL_PORT .env | cut -d'=' -f2)
            update_config "MYSQL_PORT" "$current" "🗄️ MySQL Port Configuration"
            ;;
        3)
            current=$(grep MYSQL_DATABASE .env | cut -d'=' -f2)
            update_config "MYSQL_DATABASE" "$current" "📊 Database Name Configuration"
            ;;
        4)
            current=$(grep MYSQL_USER .env | cut -d'=' -f2)
            update_config "MYSQL_USER" "$current" "👤 Database User Configuration"
            ;;
        5)
            current=$(grep MYSQL_PASSWORD .env | cut -d'=' -f2)
            update_config "MYSQL_PASSWORD" "$current" "🔐 Database Password Configuration"
            ;;
        6)
            current=$(grep SPRING_PROFILES_ACTIVE .env | cut -d'=' -f2)
            update_config "SPRING_PROFILES_ACTIVE" "$current" "⚙️ Spring Profile Configuration"
            ;;
        7)
            current=$(grep APP_SEED_PRODUCTS .env | cut -d'=' -f2)
            update_config "APP_SEED_PRODUCTS" "$current" "🌱 Seed Products Count Configuration"
            ;;
        8)
            current=$(grep APP_SEED_CUSTOMERS .env | cut -d'=' -f2)
            update_config "APP_SEED_CUSTOMERS" "$current" "👥 Seed Customers Count Configuration"
            ;;
        9)
            current=$(grep JWT_SECRET .env | cut -d'=' -f2)
            update_config "JWT_SECRET" "$current" "🔐 JWT Secret Configuration"
            ;;
        10)
            current=$(grep JWT_EXPIRATION .env | cut -d'=' -f2)
            update_config "JWT_EXPIRATION" "$current" "⏰ JWT Expiration Configuration (milliseconds)"
            ;;
        11)
            show_all_config
            ;;
        12)
            manage_backups
            ;;
        0)
            echo -e "\n${GREEN}👋 Tạm biệt!${NC}"
            echo -e "${BLUE}💡 Nhớ chạy 'make restart' để áp dụng các thay đổi${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}❌ Lựa chọn không hợp lệ. Vui lòng chọn từ 0-12${NC}"
            ;;
    esac
    
    echo -e "\n${BLUE}Press Enter to continue...${NC}"
    read
done
