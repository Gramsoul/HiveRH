# HiveRH

[Version en espanol](README.md)

HiveRH is a REST API for Human Resources management. It provides endpoints to manage employees, user accounts, roles, organizational structure, work schedules, daily work requests, payroll records, leaves, vacations, and certificates.

The project is designed as an academic MVP. The main goal is to provide clear business rules, JWT authentication, role-based permissions, and endpoints that are easy to test from Postman or Swagger.

## Documentation

Detailed documentation is available in the `docs` folder:

- `docs/Requerimiento.md`: functional scope and general system rules.
- `docs/Informe_Entidades_Endpoints.md`: full walkthrough of entities, endpoints, and the recommended testing flow for Postman or project presentation.
- `docs/Conceptual.md`: conceptual domain model.
- `docs/DER.pdf`: entity-relationship diagram.

This README is a quick guide to run and understand the project. For the complete endpoint details, check the full report.

## Requirements

- JDK 17 or newer.
- MySQL running locally or on an accessible server.
- Maven Wrapper included in the repository (`mvnw.cmd` / `mvnw`), or Maven installed.
- Environment variables configured in the environment used to run the application.

## Configuration

The application reads its configuration from `src/main/resources/application.yaml`. The required variables are:

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/hiverh` |
| `DB_USER` | MySQL user | `root` |
| `DB_PASSWORD` | MySQL password | `admin` |
| `EMAIL_ADDRESS` | SMTP sender email address | `hiverh.notifications@gmail.com` |
| `EMAIL_PASSWORD` | SMTP application password | `abcd efgh ijkl mnop` |
| `SECRET` | Secret key used to sign JWT tokens | `super-secret-key-at-least-32-bytes` |
| `EXPIRATION` | Token duration in milliseconds | `86400000` |
| `DEMO_CLEANUP_ENABLED` | Enables automatic demo data cleanup | `false` |
| `DEMO_CLEANUP_DAILY_CRON` | Daily cleanup cron expression | `0 0 4 * * *` |
| `DEMO_CLEANUP_ZONE` | Cron time zone | `UTC` |
| `DEMO_CLEANUP_MAX_RECORDS` | Maximum record count before cleanup runs | `5000` |
| `DEMO_CLEANUP_INCLUDE_CATALOG_DATA` | Also deletes branches, departments, positions, and payroll concepts | `true` |
| `DEMO_CLEANUP_PRESERVED_ACCOUNT_USERS` | Comma-separated users that are never deleted | `admin` |

Example:

```properties
DB_URL=jdbc:mysql://localhost:3306/hiverh
DB_USER=root
DB_PASSWORD=admin
EMAIL_ADDRESS=hiverh.notifications@gmail.com
EMAIL_PASSWORD=abcd efgh ijkl mnop
SECRET=super-secret-key-at-least-32-bytes
EXPIRATION=86400000
DEMO_CLEANUP_ENABLED=false
DEMO_CLEANUP_DAILY_CRON=0 0 4 * * *
DEMO_CLEANUP_ZONE=UTC
DEMO_CLEANUP_MAX_RECORDS=5000
DEMO_CLEANUP_INCLUDE_CATALOG_DATA=true
DEMO_CLEANUP_PRESERVED_ACCOUNT_USERS=admin
```

For Gmail, use an application password instead of the personal account password.

Using a `.env` file is optional. Each developer can configure these variables from IntelliJ IDEA, the terminal, system environment variables, or any execution environment they prefer.

The repository includes `.env.sample` as a template. It can be copied to `.env` and adjusted locally without committing secrets.

In IntelliJ IDEA:

```text
Run/Debug Configurations > Environment variables
```

## Database

HiveRH uses MySQL. Before starting the application, the database must exist:

```sql
CREATE DATABASE IF NOT EXISTS hiverh;
```

Hibernate is configured with `ddl-auto: update`, so it can create or update tables inside that database, but it does not create the MySQL database itself.

The application expects at least one admin account to already exist in the database used for testing.

## Demo Cleanup

Public demo environments can enable automatic data cleanup. The job deletes operational records and, when `DEMO_CLEANUP_INCLUDE_CATALOG_DATA=true`, also deletes catalog data created from Swagger. Users listed in `DEMO_CLEANUP_PRESERVED_ACCOUNT_USERS` are never deleted.

For Railway, configure it with environment variables, for example:

```properties
DEMO_CLEANUP_ENABLED=true
DEMO_CLEANUP_DAILY_CRON=0 0 4 * * *
DEMO_CLEANUP_ZONE=UTC
DEMO_CLEANUP_MAX_RECORDS=1000
DEMO_CLEANUP_INCLUDE_CATALOG_DATA=true
DEMO_CLEANUP_PRESERVED_ACCOUNT_USERS=admin
```

## Local Run

By default, the API is available at:

```text
http://localhost:8080
```

With Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Authentication

The API uses JWT. To call protected endpoints:

1. Execute `POST /api/auth/login`.
2. Copy the returned token.
3. Send the token in each protected request:

```http
Authorization: Bearer <token>
```

Main roles:

- `ADMIN`: manages the whole system.
- `STAFF`: manages employees, work schedules, daily work requests, leaves, vacations, and payroll records.
- `EMPLOYEE`: can view and operate on their own resources when the business rule allows it.

## Swagger

With the application running:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Swagger is exposed to make testing and contract review easier. Swagger does not store data by itself: it sends real requests to the API. Any data created from Swagger is persisted in the MySQL database configured through `DB_URL`.

Recommended Swagger testing flow:

1. Start MySQL and create the `hiverh` database.
2. Configure the environment variables.
3. Run the application.
4. Open `http://localhost:8080/swagger-ui.html`.
5. Execute `POST /api/auth/login` with an existing account.
6. Copy the token from the response.
7. Press `Authorize` and paste only the JWT token.

Once authorized, Swagger sends the JWT when calling protected endpoints.

## Base Endpoints

The complete endpoint details are available in `docs/Informe_Entidades_Endpoints.md`. As a quick reference, the main modules are:

| Module | Base path |
|---|---|
| Auth | `/api/auth` |
| Accounts | `/api/accounts` |
| Employees | `/api/employees` |
| Branches | `/api/branches` |
| Departments | `/api/departments` |
| Positions | `/api/positions` |
| Work Schedules | `/api/work-schedules` |
| Work Requests | `/api/work-requests` |
| Payroll Periods | `/api/payroll-periods` |
| Payroll Concepts | `/api/payroll-concepts` |
| Payrolls | `/api/payrolls` |
| Licenses | `/api/licenses` |
| Certificates | `/api/certificates` |
| Vacations | `/api/vacations` |

Filters in `GET` endpoints are sent as query params. It is not necessary to send every filter: one, several, or none can be provided.

Examples:

```http
GET /api/employees?dni=43917621&page=0&size=10
GET /api/work-schedules/me?from=2026-08-01&to=2026-08-31
GET /api/work-requests?departmentId=2&status=PENDING&page=0&size=10
GET /api/vacations?status=PENDING&fullName=Juan Perez&page=0&size=10
GET /api/payrolls?periodId=1&page=0&size=10
GET /api/payrolls/me?year=2026
```

## Pagination

Paginated endpoints use Spring `Pageable` parameters:

```http
page=0
size=10
sort=startDate,desc
```

`page` starts at 0. `sort` is optional and orders the result without changing the applied filters.

Paginated endpoints:

| Module | Endpoint |
|---|---|
| Employees | `GET /api/employees` |
| Work Schedules | `GET /api/work-schedules` |
| Work Requests | `GET /api/work-requests` |
| Licenses | `GET /api/licenses` |
| Payrolls | `GET /api/payrolls` |
| Vacations | `GET /api/vacations` |

Examples:

```http
GET /api/employees?page=0&size=10
GET /api/work-schedules?branchId=1&from=2026-08-01&to=2026-08-31&page=0&size=10
GET /api/work-requests?status=PENDING&page=0&size=10
GET /api/licenses?status=PENDING&page=0&size=10&sort=requestDate,desc
GET /api/payrolls?page=0&size=10
GET /api/vacations?dniEmployee=43917621&page=0&size=10
```

## Important Rules

- An employee cannot view another employee's payroll records.
- `STAFF` and `ADMIN` can view any employee payroll records.
- Only `STAFF` and `ADMIN` can create, update, confirm, or cancel payroll records.
- Employees can only view their own confirmed payroll records.
- Two active payroll records cannot be created for the same employee in the same period.
- Employees can only view their own active work schedules.
- Employees can create and view their own daily work requests, and can cancel them only while they are PENDING.
- `STAFF` and `ADMIN` can create, update, or cancel work schedules without physically deleting records.
- `STAFF` and `ADMIN` can approve or reject daily work requests; approval records the reviewer and updates the work schedule.
- Two active work schedules cannot overlap for the same employee, date, and time range.
- Two PENDING work requests of the same type cannot exist for the same employee and target date.
- Employees can delete their own leave or vacation requests only if they are still PENDING.
- `STAFF` does not delete leave or vacation requests; it manages, approves, or rejects them.
- `ADMIN` can manage all resources.

## Common Errors

- `401 Unauthorized`: the token is missing or invalid.
- `403 Forbidden`: the user is authenticated but does not have permission for that action.
- `404 Not Found`: the requested resource does not exist.
- `415 Unsupported Media Type`: the `Content-Type` does not match what the endpoint expects. For example, sending JSON to an endpoint that expects `multipart/form-data`.

## Tech Stack

- Java
- Spring Boot
- Spring Web MVC
- Spring Security
- JWT with `jjwt`
- Spring Data JPA
- Hibernate
- MySQL
- Bean Validation / Jakarta Validation
- Lombok
- MapStruct
- Springdoc OpenAPI / Swagger UI
- Maven

## Authors

- Gallego Romero Gonzalo N.
- Herrera Victor M.
- Molina Cristian N.
- Romero Rajoy Jose L.
