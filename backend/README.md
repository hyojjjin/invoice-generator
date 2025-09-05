# Invoice Backend API

Spring Boot 기반 인보이스 관리 백엔드 API

## 요구사항

- Java 17 이상
- Maven 3.6 이상

## 설치 및 실행

### 1. Java 및 Maven 설치 (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven
```

### 2. 프로젝트 빌드 및 실행
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### 3. 다른 데이터베이스 사용 (선택사항)

#### MySQL 사용
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

#### PostgreSQL 사용
```bash
mvn spring-boot.run -Dspring-boot.run.profiles=postgresql
```

## API 엔드포인트

### 기본 URL: `http://localhost:8080/api`

### 인보이스 관리
- `GET /invoices` - 모든 인보이스 조회 (페이징 지원)
- `GET /invoices/all` - 모든 인보이스 조회 (페이징 없음)
- `GET /invoices/{id}` - ID로 인보이스 조회
- `GET /invoices/number/{invoiceNumber}` - 인보이스 번호로 조회
- `POST /invoices` - 새 인보이스 생성
- `PUT /invoices/{id}` - 인보이스 수정
- `DELETE /invoices/{id}` - 인보이스 삭제

### 검색
- `GET /invoices/search/company?name={companyName}` - 회사명으로 검색
- `GET /invoices/search/client?name={clientName}` - 고객명으로 검색
- `GET /invoices/search/date-range?startDate={date}&endDate={date}` - 날짜 범위 검색
- `GET /invoices/recent` - 최근 인보이스 10개 조회

### 헬스체크
- `GET /invoices/health` - API 상태 확인

## 데이터베이스

### H2 Database (기본 - 개발용)
- 메모리 데이터베이스로 애플리케이션 재시작 시 데이터 초기화
- H2 콘솔: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (비워두기)

### MySQL (운영용)
```sql
CREATE DATABASE invoice_db;
CREATE USER 'invoice_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON invoice_db.* TO 'invoice_user'@'localhost';
FLUSH PRIVILEGES;
```

### PostgreSQL (운영용)
```sql
CREATE DATABASE invoice_db;
CREATE USER invoice_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE invoice_db TO invoice_user;
```

## 프론트엔드 연동

React 프론트엔드에서 다음과 같이 API를 호출할 수 있습니다:

```javascript
// 인보이스 저장
const saveInvoice = async (invoiceData) => {
  const response = await fetch('http://localhost:8080/api/invoices', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(invoiceData)
  });
  return response.json();
};

// 인보이스 목록 조회
const getInvoices = async () => {
  const response = await fetch('http://localhost:8080/api/invoices/all');
  return response.json();
};
```

## 주요 기능

1. **완전한 CRUD 기능**: 인보이스 생성, 조회, 수정, 삭제
2. **데이터 검증**: Jakarta Validation을 사용한 입력 데이터 검증
3. **다중 데이터베이스 지원**: H2, MySQL, PostgreSQL
4. **CORS 설정**: React 프론트엔드와의 연동을 위한 CORS 설정
5. **페이징 및 정렬**: 대량 데이터 처리를 위한 페이징 지원
6. **검색 기능**: 회사명, 고객명, 날짜 범위별 검색
7. **자동 계산**: 세금 및 총액 자동 계산