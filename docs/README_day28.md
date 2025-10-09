# Day 28 — Slim Image + SBOM

## Mục tiêu
Tối ưu hóa Docker image bằng cách sử dụng Alpine JRE và tạo SBOM (Software Bill of Materials) để đảm bảo security và compliance.

## Thay đổi chính

### 1. Dockerfile Optimization
- **File**: `backend/Dockerfile`
- **Thay đổi**: Chuyển từ `eclipse-temurin:17-jre` sang `eclipse-temurin:17-jre-alpine`

### 2. Makefile Commands
- **File**: `makefile`
- **Commands**:
  - `make backend-slim-build` - Build slim image
  - `make backend-sbom` - Generate SBOM với Syft

### 3. CI Integration
- **File**: `.github/workflows/ci.yml`
- **Steps**: Build slim image và generate SBOM

## Docker Image Optimization

### 1. Base Image Change
```dockerfile
# Before
FROM eclipse-temurin:17-jre

# After
FROM eclipse-temurin:17-jre-alpine
```

### 2. Size Comparison
- **Standard JRE**: ~400MB
- **Alpine JRE**: ~200MB (50% reduction)

### 3. Benefits
- **Smaller Size**: Faster pull/push
- **Security**: Smaller attack surface
- **Performance**: Faster container startup

## SBOM Generation

### 1. Syft Integration
```bash
# Generate SBOM
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  -v $(PWD):/work anchore/syft:latest packages \
  docker:springboot-mysql-docker-backend:latest \
  -o spdx-json=/work/backend-sbom.spdx.json
```

### 2. SBOM Format
- **Format**: SPDX JSON
- **Content**: Dependencies, licenses, vulnerabilities
- **Output**: `backend-sbom.spdx.json`

### 3. SBOM Benefits
- **Compliance**: Software compliance
- **Security**: Vulnerability tracking
- **Audit**: Software audit trail

## Cách sử dụng

### 1. Build Slim Image
```bash
# Build slim image
make backend-slim-build

# Check image size
docker images | grep backend
```

### 2. Generate SBOM
```bash
# Generate SBOM
make backend-sbom

# Check SBOM file
ls -la backend-sbom.spdx.json
```

### 3. CI Integration
```bash
# CI sẽ tự động build slim image và generate SBOM
git push origin master
```

## SBOM Content

### 1. Package Information
```json
{
  "packages": [
    {
      "name": "spring-boot-starter-web",
      "version": "3.5.4",
      "license": "Apache-2.0",
      "purl": "pkg:maven/org.springframework.boot/spring-boot-starter-web@3.5.4"
    }
  ]
}
```

### 2. Vulnerability Information
```json
{
  "vulnerabilities": [
    {
      "id": "CVE-2023-1234",
      "severity": "HIGH",
      "description": "Vulnerability description"
    }
  ]
}
```

## Makefile Commands

### 1. Backend Slim Build
```makefile
.PHONY: backend-slim-build
backend-slim-build: ## 🪶 Build slim backend image (alpine JRE)
	@echo "🪶 Building slim backend image (alpine JRE)..."
	$(DC) build $(SERVICE_APP)
	@echo "✅ Slim backend image built!"
```

### 2. Backend SBOM
```makefile
.PHONY: backend-sbom
backend-sbom: ## 📦 Generate SBOM for backend image with Syft
	@echo "📦 Generating SBOM (Syft) for backend image..."
	@docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
	  -v $(PWD):/work anchore/syft:latest packages \
	  docker:$(shell basename $(PWD))-backend:latest \
	  -o spdx-json=/work/backend-sbom.spdx.json
	@echo "✅ SBOM generated at backend-sbom.spdx.json"
```

## CI Integration

### 1. Build Slim Image
```yaml
- name: Build slim backend image & generate SBOM
  env:
    BACKEND_PORT: 8080
  run: |
    make backend-slim-build
    make backend-sbom
```

### 2. Upload SBOM
```yaml
- name: Upload SBOM artifact
  uses: actions/upload-artifact@v4
  with:
    name: backend-sbom
    path: backend-sbom.spdx.json
```

## Security Benefits

### 1. Vulnerability Tracking
- **Dependencies**: Track all dependencies
- **Vulnerabilities**: Identify known vulnerabilities
- **Updates**: Plan security updates

### 2. Compliance
- **License Compliance**: Track license usage
- **Regulatory**: Meet regulatory requirements
- **Audit**: Software audit trail

### 3. Supply Chain Security
- **Transparency**: Full software transparency
- **Trust**: Build trust with stakeholders
- **Risk Management**: Manage supply chain risks

## Performance Benefits

### 1. Image Size
- **50% Reduction**: Smaller image size
- **Faster Pull**: Faster image pull
- **Storage**: Less storage usage

### 2. Container Startup
- **Faster Startup**: Faster container startup
- **Memory Usage**: Lower memory usage
- **Resource Efficiency**: Better resource utilization

## Lợi ích

### 1. Security
- **Vulnerability Tracking**: Track vulnerabilities
- **Compliance**: Meet compliance requirements
- **Audit**: Software audit trail

### 2. Performance
- **Smaller Images**: Faster deployment
- **Resource Efficiency**: Better resource usage
- **Scalability**: Better scalability

### 3. Operations
- **Transparency**: Full software transparency
- **Maintenance**: Easier maintenance
- **Updates**: Planned updates

## Lưu ý cho dev

### 1. Alpine Considerations
- **Compatibility**: Test compatibility với Alpine
- **Dependencies**: Kiểm tra dependencies
- **Performance**: Test performance

### 2. SBOM Maintenance
- **Regular Updates**: Update SBOM regularly
- **Vulnerability Scanning**: Scan for vulnerabilities
- **License Compliance**: Check license compliance

### 3. CI/CD Integration
- **Automation**: Automate SBOM generation
- **Artifacts**: Store SBOM artifacts
- **Reporting**: Generate security reports
