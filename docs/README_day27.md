# Day 27 — CI Workflow

## Mục tiêu
Thiết lập GitHub Actions workflow để tự động build, test và deploy.

## Thay đổi chính

### 1. GitHub Actions Workflow
- **File**: `.github/workflows/ci.yml`
- **Triggers**: Push to master, Pull requests
- **Jobs**: test-and-build

### 2. Workflow Steps
- **Checkout**: Checkout repository
- **Java Setup**: Setup JDK 17
- **Gradle Cache**: Cache Gradle dependencies
- **Tests**: Run unit tests
- **Docker Build**: Build backend image
- **Artifacts**: Upload build artifacts

## Workflow Configuration

### 1. Basic Setup
```yaml
name: CI

on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

jobs:
  test-and-build:
    runs-on: ubuntu-latest
```

### 2. Java Setup
```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: '17'
```

### 3. Gradle Cache
```yaml
- name: Cache Gradle
  uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
    restore-keys: |
      ${{ runner.os }}-gradle-
```

### 4. Test Execution
```yaml
- name: Run unit tests (Gradle)
  run: |
    cd backend
    ./gradlew clean test --no-daemon --stacktrace
```

### 5. Docker Build
```yaml
- name: Build backend image via Make (Docker)
  env:
    BACKEND_PORT: 8080
    MYSQL_PORT: 3306
  run: |
    make backend-quick-build
```

## Workflow Features

### 1. Automated Testing
- **Unit Tests**: Chạy tất cả unit tests
- **Integration Tests**: Chạy Testcontainers tests
- **Code Coverage**: Có thể thêm coverage reporting

### 2. Docker Build
- **Multi-stage Build**: Sử dụng Dockerfile multi-stage
- **Caching**: Docker layer caching
- **Optimization**: Build optimization

### 3. Artifact Management
- **Build Artifacts**: Upload JAR files
- **Docker Images**: Build và push images
- **Reports**: Upload test reports

## Cách sử dụng

### 1. Trigger Workflow
```bash
# Push to master
git push origin master

# Create pull request
git push origin feature-branch
```

### 2. Check Workflow Status
- **GitHub UI**: Vào Actions tab
- **Status**: Xem workflow status
- **Logs**: Xem detailed logs

### 3. Local Testing
```bash
# Test locally
make backend-quick-build

# Run tests
cd backend && ./gradlew test
```

## Workflow Benefits

### 1. Automation
- **Automatic**: Tự động chạy khi có changes
- **Consistent**: Môi trường giống nhau
- **Reliable**: Không phụ thuộc vào local setup

### 2. Quality Assurance
- **Tests**: Đảm bảo code quality
- **Build**: Đảm bảo code build được
- **Integration**: Test integration với dependencies

### 3. Deployment Ready
- **Docker Images**: Sẵn sàng deploy
- **Artifacts**: Có artifacts để deploy
- **Environment**: Test trong production-like environment

## Workflow Steps Detail

### 1. Checkout Repository
```yaml
- name: Checkout repository
  uses: actions/checkout@v4
```

### 2. Setup Java
```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: '17'
```

### 3. Cache Dependencies
```yaml
- name: Cache Gradle
  uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
```

### 4. Run Tests
```yaml
- name: Run unit tests (Gradle)
  run: |
    cd backend
    ./gradlew clean test --no-daemon --stacktrace
```

### 5. Build Docker Image
```yaml
- name: Build backend image via Make (Docker)
  env:
    BACKEND_PORT: 8080
    MYSQL_PORT: 3306
  run: |
    make backend-quick-build
```

## Error Handling

### 1. Test Failures
- **Fail Fast**: Workflow dừng khi test fail
- **Detailed Logs**: Xem chi tiết lỗi
- **Retry**: Có thể retry failed tests

### 2. Build Failures
- **Docker Build**: Xem Docker build logs
- **Dependencies**: Kiểm tra dependencies
- **Configuration**: Kiểm tra configuration

### 3. Environment Issues
- **Runner Issues**: GitHub runner problems
- **Network Issues**: Network connectivity
- **Resource Issues**: Memory/CPU limits

## Lợi ích

### 1. Development
- **Feedback**: Immediate feedback on changes
- **Quality**: Đảm bảo code quality
- **Confidence**: Deploy với confidence

### 2. Team Collaboration
- **Consistent**: Môi trường giống nhau
- **Transparent**: Mọi người thấy được status
- **Automated**: Không cần manual intervention

### 3. Production Readiness
- **Tested**: Code đã được test
- **Built**: Build artifacts sẵn sàng
- **Deployed**: Có thể deploy ngay

## Lưu ý cho dev

### 1. Workflow Maintenance
- **Update Dependencies**: Cập nhật actions versions
- **Add Steps**: Thêm steps mới khi cần
- **Optimize**: Tối ưu hóa performance

### 2. Local Development
- **Test Locally**: Test trước khi push
- **Docker**: Sử dụng Docker để test
- **Makefile**: Sử dụng makefile commands

### 3. Troubleshooting
- **Logs**: Xem detailed logs
- **Environment**: Kiểm tra environment variables
- **Dependencies**: Kiểm tra dependencies
