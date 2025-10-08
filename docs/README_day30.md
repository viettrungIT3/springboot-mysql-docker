## Day 30 — Demo data & Postman/Insomnia

Mục tiêu: Bộ collection + hướng dẫn E2E (create customer → add products → create order → confirm).

### Chuẩn bị
- Khởi động backend + DB:
```
make dev-backend
```

### Postman Collection
- File: `docs/collections/postman_collection.json`
- Use: Import vào Postman, chạy lần lượt:
  - Health → 200
  - Create Product → 200
  - List Products → 200
  - Create Order → 200 (có Idempotency-Key)
  - Confirm Order → 200 (tổng tiền được tính, ghi StockEntry)

### Insomnia Collection
- File: `docs/collections/insomnia.yaml`
- Import vào Insomnia và chạy tương tự Postman.

### Gợi ý E2E
1. Tạo 1-2 sản phẩm.
2. Tạo đơn hàng với các item.
3. Gọi confirm để ghi nhận xuất kho và tính tổng.
4. Kiểm tra `/api/v1/products` để xem tồn kho đã giảm.


