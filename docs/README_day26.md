# Day 26 — OpenAPI Client Generation

## Mục tiêu
Sinh sample API client từ OpenAPI spec sử dụng Docker để đảm bảo consistency và không cần cài đặt local.

## Thay đổi chính

### 1. Makefile Commands
- **File**: `makefile`
- **Commands**:
  - `make client-gen` - Sinh client TypeScript
  - `make client-test` - Test client trong Docker

### 2. Client Generation
- **Tool**: OpenAPI Generator CLI (Docker)
- **Language**: TypeScript Axios
- **Output**: `clients/typescript-axios/`

### 3. Docker-based Workflow
- **No local installs**: Tất cả chạy trong Docker
- **Consistency**: Môi trường giống nhau cho mọi dev
- **Isolation**: Không ảnh hưởng đến máy host

## Cách hoạt động

### 1. Client Generation Process
```bash
# 1. Fetch OpenAPI spec
curl -sSf http://localhost:8080/v3/api-docs -o clients/openapi.json

# 2. Generate TypeScript client
docker run --rm -v $(PWD):/local openapitools/openapi-generator-cli:v7.7.0 generate \
  -i /local/clients/openapi.json \
  -g typescript-axios \
  -o /local/clients/typescript-axios \
  --additional-properties=supportsES6=true,npmName=@app/api-client,withSeparateModelsAndApi=true,apiPackage=api,modelPackage=models
```

### 2. Client Testing
```bash
# Build client trong Docker
docker run --rm -v $(PWD)/clients/typescript-axios:/proj -w /proj node:20-alpine sh -lc "npm install --no-audit --no-fund && npm run build"

# Test client call
docker run --rm -v $(PWD)/clients/typescript-axios:/proj -w /proj node:20-alpine sh -lc "node -e \"const {ProductsApi, Configuration}=require('./dist'); (async()=>{const api=new ProductsApi(new Configuration({basePath:'http://host.docker.internal:8080'})); const res=await api.list3(0,5,'id,desc'); console.log('OK products page size:', res.data?.content?.length ?? 'unknown');})();\""
```

## Generated Client Structure

### 1. Directory Structure
```
clients/typescript-axios/
├── api/                    # API classes
│   ├── ProductsApi.ts
│   ├── OrdersApi.ts
│   ├── CustomersApi.ts
│   └── ...
├── models/                 # Data models
│   ├── ProductResponse.ts
│   ├── OrderResponse.ts
│   └── ...
├── package.json
├── tsconfig.json
└── README.md
```

### 2. API Classes
```typescript
// Example usage
import { ProductsApi, Configuration } from './dist';

const api = new ProductsApi(new Configuration({
  basePath: 'http://localhost:8080'
}));

// List products
const products = await api.list3(0, 10, 'id,desc');

// Create product
const newProduct = await api.create3({
  name: 'Test Product',
  description: 'Test Description',
  price: 99.99,
  quantityInStock: 10
});
```

## Cách sử dụng

### 1. Generate Client
```bash
# Đảm bảo backend đang chạy
make dev-backend

# Sinh client
make client-gen
```

### 2. Test Client
```bash
# Test client call
make client-test
```

### 3. Use Client
```bash
# Vào thư mục client
cd clients/typescript-axios

# Install dependencies (nếu cần)
npm install

# Build
npm run build

# Sử dụng trong code
node -e "const {ProductsApi, Configuration} = require('./dist'); ..."
```

## Generated Files

### 1. API Classes
- **ProductsApi**: CRUD operations cho products
- **OrdersApi**: CRUD operations cho orders
- **CustomersApi**: CRUD operations cho customers
- **SuppliersApi**: CRUD operations cho suppliers
- **UsersApi**: CRUD operations cho users
- **StockEntriesApi**: CRUD operations cho stock entries
- **AuthenticationApi**: Auth operations

### 2. Model Classes
- **Request DTOs**: ProductCreateRequest, OrderCreateRequest, etc.
- **Response DTOs**: ProductResponse, OrderResponse, etc.
- **Common DTOs**: PageResponse, ApiError, etc.

### 3. Configuration
- **Configuration**: Base configuration class
- **Base**: Base API class
- **Common**: Common utilities

## Client Features

### 1. TypeScript Support
- **Type Safety**: Full TypeScript support
- **IntelliSense**: IDE autocomplete
- **Error Handling**: Typed error responses

### 2. Axios Integration
- **HTTP Client**: Axios-based HTTP client
- **Interceptors**: Request/response interceptors
- **Error Handling**: Axios error handling

### 3. Configuration
- **Base URL**: Configurable base URL
- **Headers**: Custom headers support
- **Timeout**: Request timeout configuration

## Lợi ích

### 1. Developer Experience
- **Type Safety**: Compile-time type checking
- **Auto-completion**: IDE support
- **Documentation**: Generated from OpenAPI spec

### 2. Consistency
- **Docker-based**: Môi trường giống nhau
- **No local installs**: Không cần cài đặt local
- **Version control**: Client code được version control

### 3. Maintenance
- **Auto-generated**: Tự động update khi API thay đổi
- **Sync**: Luôn sync với API spec
- **Testing**: Dễ dàng test integration

## Lưu ý cho dev

### 1. Client Generation
- **Backend running**: Cần backend chạy để fetch spec
- **Docker**: Sử dụng Docker để đảm bảo consistency
- **Version**: Sử dụng specific version của generator

### 2. Client Usage
- **Base URL**: Cấu hình đúng base URL
- **Error Handling**: Handle errors properly
- **Type Safety**: Sử dụng TypeScript types

### 3. Maintenance
- **Regenerate**: Regenerate khi API thay đổi
- **Version Control**: Commit client code
- **Testing**: Test client integration
