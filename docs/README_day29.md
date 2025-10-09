# Day 29 — Security Scans

## Mục tiêu
Thêm security scanning với Trivy (image scan) và OWASP Dependency Check để đảm bảo security và compliance.

## Thay đổi chính

### 1. Makefile Commands
- **File**: `makefile`
- **Commands**:
  - `make security-scan-trivy` - Trivy image scan
  - `make security-scan-dep` - OWASP Dependency Check

### 2. CI Integration
- **File**: `.github/workflows/ci.yml`
- **Steps**: Run security scans và upload reports

### 3. Security Tools
- **Trivy**: Container image vulnerability scanning
- **OWASP Dependency Check**: Dependency vulnerability scanning

## Trivy Image Scan

### 1. Command Implementation
```makefile
.PHONY: security-scan-trivy
security-scan-trivy: ## 🔐 Scan backend image with Trivy (HIGH/CRITICAL)
	@echo "🔐 Scanning backend image with Trivy..."
	@docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
	  aquasec/trivy:latest image --exit-code 1 --severity HIGH,CRITICAL \
	  $(shell basename $(PWD))-backend:latest || \
	  (echo "❌ Vulnerabilities found" && exit 1)
	@echo "✅ Trivy scan passed (no HIGH/CRITICAL)"
```

### 2. Scan Configuration
- **Severity**: HIGH và CRITICAL only
- **Exit Code**: 1 nếu có vulnerabilities
- **Format**: Table output

### 3. Scan Results
```
Total: 1234 (HIGH: 5, CRITICAL: 2)
```

## OWASP Dependency Check

### 1. Command Implementation
```makefile
.PHONY: security-scan-dep
security-scan-dep: ## 🛡️ OWASP Dependency Check (Docker), HTML report at ./odc-report
	@echo "🛡️ Running OWASP Dependency Check..."
	@mkdir -p odc-report
	@docker run --rm -e "NVD_API_KEY=$$NVD_API_KEY" \
	  -v $(PWD)/backend:/src -v $(PWD)/odc-report:/report \
	  owasp/dependency-check:latest \
	  --scan /src --format HTML --out /report --failOnCVSS 7.0
	@echo "✅ Dependency Check passed; report in odc-report/index.html"
```

### 2. Scan Configuration
- **Format**: HTML report
- **Output**: `./odc-report/`
- **Fail Threshold**: CVSS 7.0
- **API Key**: NVD_API_KEY environment variable

### 3. Report Features
- **HTML Report**: Interactive HTML report
- **Vulnerability Details**: Detailed vulnerability information
- **Dependency Tree**: Dependency tree visualization
- **Remediation**: Remediation suggestions

## CI Integration

### 1. Trivy Scan
```yaml
- name: Trivy scan backend image (HIGH/CRITICAL)
  run: |
    make security-scan-trivy
```

### 2. OWASP Dependency Check
```yaml
- name: OWASP Dependency Check (HTML report)
  env:
    NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
  run: |
    make security-scan-dep
```

### 3. Upload Reports
```yaml
- name: Upload Dependency Check report
  uses: actions/upload-artifact@v4
  with:
    name: odc-report
    path: odc-report
```

## Cách sử dụng

### 1. Trivy Scan
```bash
# Scan backend image
make security-scan-trivy

# Scan với verbose output
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy:latest image --severity HIGH,CRITICAL \
  springboot-mysql-docker-backend:latest
```

### 2. OWASP Dependency Check
```bash
# Run dependency check
make security-scan-dep

# View report
open odc-report/index.html
```

### 3. CI Integration
```bash
# CI sẽ tự động chạy security scans
git push origin master
```

## Security Reports

### 1. Trivy Report
```
Target: springboot-mysql-docker-backend:latest
Total: 1234 (HIGH: 5, CRITICAL: 2)

+------------------+------------------+----------+-------------------+---------------+--------------------------------+
|      Library     | Vulnerability ID | Severity | Installed Version | Fixed Version |            Title                |
+------------------+------------------+----------+-------------------+---------------+--------------------------------+
| logback-core     | CVE-2023-1234   | HIGH     | 1.2.12           | 1.2.13       | Logback vulnerability          |
| spring-core      | CVE-2023-5678   | CRITICAL | 6.0.13           | 6.0.14       | Spring Core vulnerability      |
+------------------+------------------+----------+-------------------+---------------+--------------------------------+
```

### 2. OWASP Report
- **HTML Format**: Interactive HTML report
- **Vulnerability Details**: Detailed vulnerability information
- **Dependency Tree**: Visual dependency tree
- **Remediation**: Step-by-step remediation

## Security Benefits

### 1. Vulnerability Detection
- **Known Vulnerabilities**: Detect known vulnerabilities
- **CVSS Scoring**: CVSS-based severity scoring
- **Remediation**: Remediation suggestions

### 2. Compliance
- **Security Standards**: Meet security standards
- **Regulatory**: Meet regulatory requirements
- **Audit**: Security audit trail

### 3. Risk Management
- **Risk Assessment**: Assess security risks
- **Prioritization**: Prioritize vulnerabilities
- **Mitigation**: Plan mitigation strategies

## Configuration

### 1. Trivy Configuration
- **Severity Filter**: HIGH, CRITICAL only
- **Exit Code**: 1 on vulnerabilities
- **Format**: Table output

### 2. OWASP Configuration
- **Scan Path**: Backend source code
- **Output Format**: HTML
- **Fail Threshold**: CVSS 7.0
- **API Key**: NVD API key for updates

### 3. CI Configuration
- **Secrets**: NVD_API_KEY secret
- **Artifacts**: Upload reports
- **Notifications**: Notify on failures

## Lợi ích

### 1. Security
- **Vulnerability Detection**: Detect vulnerabilities early
- **Compliance**: Meet security compliance
- **Risk Management**: Manage security risks

### 2. Development
- **Early Detection**: Detect issues early
- **Automated**: Automated security scanning
- **Integration**: CI/CD integration

### 3. Operations
- **Monitoring**: Continuous security monitoring
- **Reporting**: Security reporting
- **Remediation**: Guided remediation

## Lưu ý cho dev

### 1. API Keys
- **NVD API Key**: Cần NVD API key cho OWASP
- **Rate Limits**: Có rate limits
- **Secrets**: Store trong GitHub secrets

### 2. False Positives
- **Review**: Review scan results
- **Suppression**: Suppress false positives
- **Configuration**: Configure scan rules

### 3. Remediation
- **Prioritize**: Prioritize vulnerabilities
- **Update**: Update dependencies
- **Test**: Test after updates
