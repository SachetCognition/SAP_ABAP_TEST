# SAP ABAP to Java + Angular Migration

This project is a migrated SAP ABAP application now running on a modern **Java (Spring Boot)** backend with an **Angular** frontend. The original ABAP programs handled material stock reporting and sales order retrieval from SAP tables (MARA, MAKT, MARD, VBAK, VBAP). This migration preserves all business logic while moving to a cloud-ready, open-source technology stack.

## Architecture Overview

```
┌─────────────────┐       ┌──────────────────────┐       ┌────────────┐
│  Angular 17 SPA │──────>│  Spring Boot 3.2 API │──────>│ PostgreSQL │
│  (Port 4200)    │  HTTP │  (Port 8080)         │  JDBC │ (Port 5432)│
│  AG Grid (ALV)  │       │  Flyway migrations   │       │            │
└─────────────────┘       └──────────────────────┘       └────────────┘
```

### Key Mappings

| SAP ABAP Component | Java / Angular Equivalent |
|---|---|
| `ZMAT_REPORT.PROG` (Material Stock Report) | `MaterialStockController` + `MaterialStockComponent` |
| `ZFM_GET_MAT_SO_DETAILS` (Sales Order FM) | `SalesOrderController` + `SalesOrderComponent` |
| `LZFG_MAT_SOTOP` (Type Definitions) | Entity & DTO classes |
| `CL_SALV_TABLE` (ALV Grid) | AG Grid (Angular) |
| SAP Tables (MARA, MAKT, MARD, VBAK, VBAP) | PostgreSQL tables via Flyway |

## Prerequisites

- **Java 17+** (JDK)
- **Node 18+** (with npm)
- **PostgreSQL 15+**
- **Maven 3.8+**

## How to Run Backend

```bash
# 1. Create the PostgreSQL database
createdb sap_migration

# 2. Navigate to backend directory
cd backend

# 3. Run with Maven (Flyway will auto-create tables)
./mvnw spring-boot:run

# The API will be available at http://localhost:8080/api
```

### API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/materials/stock` | Material stock report (replaces ZMAT_REPORT) |
| `GET` | `/api/sales-orders` | Sales order details (replaces ZFM_GET_MAT_SO_DETAILS) |

## How to Run Frontend

```bash
# 1. Navigate to frontend directory
cd frontend

# 2. Install dependencies
npm install

# 3. Start development server
ng serve

# The app will be available at http://localhost:4200
```

## How to Run Tests

### Backend Tests

```bash
cd backend

# Run all tests (unit + integration)
./mvnw test

# Run only unit tests
./mvnw test -Dtest="*ServiceTest"

# Run only integration tests
./mvnw test -Dtest="*IT"
```

### Frontend Tests

```bash
cd frontend

# Unit tests
ng test

# E2E tests (Cypress)
npx cypress run
```

## Project Structure

```
.
├── backend/                    # Spring Boot application
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/migration/sap/
│       │   │   ├── controller/     # REST controllers
│       │   │   ├── service/        # Business logic
│       │   │   ├── repository/     # Data access
│       │   │   ├── entity/         # JPA entities
│       │   │   ├── dto/            # Data transfer objects
│       │   │   ├── exception/      # Error handling
│       │   │   └── config/         # CORS, etc.
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/   # Flyway SQL scripts
│       └── test/
├── frontend/                   # Angular application
│   ├── src/app/
│   │   ├── material-stock/     # Material stock feature
│   │   ├── sales-order/        # Sales order feature
│   │   └── shared/             # Shared models & services
│   └── cypress/                # E2E tests
├── docs/                       # Migration documentation
├── scripts/                    # Cross-validation scripts
└── test-artifacts/             # Test evidence directories
```
