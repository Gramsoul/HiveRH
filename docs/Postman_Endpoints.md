# HiveRH - Endpoints para Postman

Documento armado desde los controllers, DTOs, enums y reglas de seguridad actuales del proyecto.

Base URL esperada en el environment de Postman:

```text
{{baseUrl}} = http://localhost:8080
```

Variables de token que ya tenes:

```text
{{adminToken}}
{{staffToken}}
{{employeeToken}}
```

Variables opcionales recomendadas para copiar los ejemplos tal cual:

```text
{{accountIdentifier}}
{{branchId}}
{{departmentId}}
{{positionId}}
{{employeeDni}}
{{workScheduleId}}
{{workRequestId}}
{{payrollPeriodId}}
{{payrollConceptId}}
{{additionConceptId}}
{{deductionConceptId}}
{{payrollId}}
{{vacationId}}
{{licenseId}}
{{certificateId}}
```

## Convenciones para Postman

En endpoints protegidos usar Authorization tipo Bearer Token:

```text
Bearer {{adminToken}}
Bearer {{staffToken}}
Bearer {{employeeToken}}
```

Para JSON usar:

```text
Content-Type: application/json
```

Para certificados usar `form-data`; Postman setea el `Content-Type` con boundary automaticamente.

Los endpoints paginados aceptan estos query params opcionales:

```text
page=0
size=20
sort=campo,asc
sort=campo,desc
```

Endpoints paginados: `GET /api/employees`, `GET /api/work-schedules`, `GET /api/work-requests`, `GET /api/payrolls`, `GET /api/vacations`, `GET /api/licenses`.

Enums usados en requests y filtros:

```text
RolEnum: ADMIN, STAFF, EMPLOYEE
GenreEnum: MALE, FEMALE, OTHER, NON_BINARY
EmployeeStatus: ACTIVE, TERMINATED
AbsenceStatus: PENDING, APPROVED, REJECTED, CANCELLED
RequestStatus: PENDING, APPROVED, REJECTED, CANCELLED
WorkRequestType: DAY_OFF, SHIFT_CHANGE, EXTRA_HOURS, COMPENSATORY_DAY, LATE_ARRIVAL, EARLY_LEAVE
WorkScheduleType: WORKDAY, DAY_OFF, HOLIDAY, EXTRA_HOURS
WorkScheduleStatus: ACTIVE, CANCELLED
PayrollPeriodStatus: OPEN, CLOSED
PayrollConceptType: ADDITION, DEDUCTION
PayrollStatus: DRAFT, CONFIRMED, CANCELLED
```

Nota: tambien existen endpoints tecnicos de Swagger/OpenAPI (`/v3/api-docs`, `/swagger-ui/**`, `/swagger-ui.html`) expuestos por configuracion, pero abajo estan solo los endpoints funcionales de la API.

## 01 Auth

### 1. Login

```http
POST {{baseUrl}}/api/auth/login
```

Auth: sin token.

Body raw JSON:

```json
{
  "identifier": "admin",
  "password": "123"
}
```

Campos:

- `identifier`: requerido. Usuario o email. Max 100.
- `password`: requerido. Max 72.

### 2. Registrar cuenta

```http
POST {{baseUrl}}/api/auth/register
```

Auth sugerida: `Bearer {{adminToken}}`.

Body raw JSON:

```json
{
  "user": "staff.demo",
  "email": "staff.demo@hiverh.local",
  "password": "Staff123",
  "rol": "STAFF"
}
```

Campos:

- `user`: requerido. 4 a 30 caracteres. Letras, numeros, punto, guion y guion bajo.
- `email`: requerido. Email valido. Max 100.
- `password`: requerido. 3 a 72 caracteres.
- `rol`: requerido. `ADMIN`, `STAFF` o `EMPLOYEE`.

## 02 Accounts

### 3. Listar cuentas

```http
GET {{baseUrl}}/api/accounts
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Params: no lleva.

Respuesta: lista cuentas sin exponer password. Cada item devuelve `email`, `user` y `rol`.

### 4. Cambiar rol de cuenta

```http
PATCH {{baseUrl}}/api/accounts/{{accountIdentifier}}/rol
```

Auth sugerida: `Bearer {{adminToken}}`. STAFF puede asignar roles que no sean `ADMIN`.

Path params:

- `accountIdentifier`: requerido. Usuario o email. Si la cuenta fue creada automaticamente para un empleado, suele ser el DNI.

Body raw JSON:

```json
{
  "rol": "EMPLOYEE"
}
```

Campos:

- `rol`: requerido. `ADMIN`, `STAFF` o `EMPLOYEE`.

### 5. Cambiar mi email

```http
PATCH {{baseUrl}}/api/accounts/me/email
```

Auth: cualquier token valido.

Body raw JSON:

```json
{
  "email": "nuevo.email@hiverh.local"
}
```

Campos:

- `email`: requerido. Max 100 y formato de email valido.

### 6. Cambiar mi password

```http
PATCH {{baseUrl}}/api/accounts/me/password
```

Auth: cualquier token valido.

Body raw JSON:

```json
{
  "currentPassword": "123",
  "newPassword": "Nueva123"
}
```

Campos:

- `currentPassword`: requerido.
- `newPassword`: requerido. 6 a 72 caracteres, con al menos una mayuscula, una minuscula y un numero. No puede ser igual al DNI/username ni igual a la actual.

## 03 Branches

### 7. Listar sucursales activas

```http
GET {{baseUrl}}/api/branches
```

Auth: cualquier token valido.

Params: no recibe filtros. Devuelve solo sucursales activas.

### 8. Crear sucursal

```http
POST {{baseUrl}}/api/branches
```

Auth sugerida: `Bearer {{adminToken}}`.

Body raw JSON:

```json
{
  "name": "Casa Central",
  "city": "Cordoba",
  "address": "Av. Colon 123"
}
```

Campos:

- `name`: requerido. Max 100. Tambien acepta alias `branchName`.
- `city`: requerido. Max 100.
- `address`: requerido. 3 a 100 caracteres.

### 9. Actualizar sucursal

```http
PUT {{baseUrl}}/api/branches/{{branchId}}
```

Auth sugerida: `Bearer {{adminToken}}`.

Path params:

- `branchId`: requerido, positivo.

Body raw JSON:

```json
{
  "name": "Casa Central",
  "city": "Cordoba Capital",
  "address": "Av. Colon 123",
  "active": true
}
```

Campos:

- `name`, `city`, `address`: recomendados. El PUT reemplaza valores.
- `active`: opcional. Si no se envia conserva el estado actual.
- `name` tambien acepta alias `branchName`.

### 10. Desactivar sucursal

```http
DELETE {{baseUrl}}/api/branches/{{branchId}}
```

Auth sugerida: `Bearer {{adminToken}}`.

Path params:

- `branchId`: requerido, positivo.

Body: no lleva.

## 04 Departments

### 11. Listar departamentos

```http
GET {{baseUrl}}/api/departments?id_department={{departmentId}}&name=Recursos&active=true
```

Auth: cualquier token valido.

Query params opcionales:

- `id_department`: positivo.
- `name`: max 100, busqueda parcial.
- `active`: boolean.

### 12. Crear departamento

```http
POST {{baseUrl}}/api/departments
```

Auth sugerida: `Bearer {{adminToken}}`.

Body raw JSON:

```json
{
  "name": "Recursos Humanos"
}
```

Campos:

- `name`: requerido. Max 100. Tambien acepta alias `departmentName`.

### 13. Actualizar departamento

```http
PUT {{baseUrl}}/api/departments/{{departmentId}}
```

Auth sugerida: `Bearer {{adminToken}}`.

Path params:

- `departmentId`: requerido, positivo.

Body raw JSON:

```json
{
  "name": "Talento Humano"
}
```

Campos:

- `name`: requerido. Max 100. Tambien acepta alias `departmentName`.

### 14. Cambiar estado de departamento

```http
PATCH {{baseUrl}}/api/departments/{{departmentId}}/status
```

Auth sugerida: `Bearer {{adminToken}}`.

Body raw JSON:

```json
{
  "active": false
}
```

Campos:

- `active`: requerido. Boolean.

## 05 Positions

### 15. Listar puestos

```http
GET {{baseUrl}}/api/positions?id_department={{departmentId}}&name=Analista&active=true
```

Auth: cualquier token valido.

Query params opcionales:

- `id_department`: positivo. Filtra puestos usados por asignaciones de ese departamento.
- `name`: max 100, busqueda parcial.
- `active`: boolean.

### 16. Crear puesto

```http
POST {{baseUrl}}/api/positions
```

Auth sugerida: `Bearer {{adminToken}}`.

Body raw JSON:

```json
{
  "name": "Analista de RRHH"
}
```

Campos:

- `name`: requerido. Max 100. Tambien acepta alias `positionName`.

### 17. Actualizar puesto

```http
PUT {{baseUrl}}/api/positions/{{positionId}}
```

Auth sugerida: `Bearer {{adminToken}}`.

Path params:

- `positionId`: requerido, positivo.

Body raw JSON:

```json
{
  "name": "Analista Senior de RRHH"
}
```

Campos:

- `name`: requerido. Max 100. Tambien acepta alias `positionName`.

### 18. Cambiar estado de puesto

```http
PATCH {{baseUrl}}/api/positions/{{positionId}}/status
```

Auth sugerida: `Bearer {{adminToken}}`.

Body raw JSON:

```json
{
  "active": true
}
```

Campos:

- `active`: requerido. Boolean.

## 06 Employees

### 19. Obtener mi perfil de empleado

```http
GET {{baseUrl}}/api/employees/me
```

Auth sugerida: `Bearer {{employeeToken}}`.

Params: no lleva.

Nota: la cuenta autenticada debe tener un empleado vinculado.

### 20. Obtener empleado por DNI

```http
GET {{baseUrl}}/api/employees/{{employeeDni}}
```

Auth: ADMIN/STAFF o el propio EMPLOYEE del DNI.

Path params:

- `employeeDni`: requerido. 7 u 8 numeros.

### 21. Listar empleados

```http
GET {{baseUrl}}/api/employees?fullName=Ada&dni={{employeeDni}}&id_branch={{branchId}}&hire_date=2026-01-10&termination_date=2026-12-31&status=ACTIVE&position=Analista&department=Recursos%20Humanos&min_salary=800000&max_salary=2000000&page=0&size=20&sort=hireDate,desc
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Query params opcionales:

- `fullName`: busqueda por nombre completo.
- `dni`: 7 u 8 numeros.
- `id_branch`: ID de sucursal.
- `hire_date`: fecha exacta `YYYY-MM-DD`.
- `termination_date`: fecha exacta `YYYY-MM-DD`.
- `status`: `ACTIVE` o `TERMINATED`.
- `position`: nombre exacto del puesto activo, case-insensitive.
- `department`: nombre exacto del departamento activo, case-insensitive.
- `min_salary`: numero.
- `max_salary`: numero.
- `page`, `size`, `sort`: paginacion opcional.

### 22. Crear empleado

```http
POST {{baseUrl}}/api/employees
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "name": "Ada",
  "lastName": "Lovelace",
  "phoneNumber": "3515550101",
  "genre": "FEMALE",
  "dni": "40111222",
  "city": "Cordoba",
  "address": "Bv. Testing 456",
  "birth_date": "1995-04-12",
  "hire_date": "2026-01-10",
  "base_salary": 1500000,
  "id_branch": 1,
  "id_position": 1,
  "id_department": 1
}
```

Campos:

- Todos los campos son requeridos.
- `dni`: 7 u 8 numeros y unico.
- `birth_date`: pasado; el empleado debe tener al menos 18 anos.
- `hire_date`: pasado o presente.
- `base_salary`, `id_branch`, `id_position`, `id_department`: positivos.
- Al crear el empleado se crea una cuenta EMPLOYEE con usuario/password inicial igual al DNI.

### 23. Actualizar parcialmente empleado

```http
PATCH {{baseUrl}}/api/employees/{{employeeDni}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Path params:

- `employeeDni`: requerido.

Body raw JSON:

```json
{
  "name": "Ada",
  "lastName": "Lovelace",
  "phoneNumber": "3515550199",
  "genre": "FEMALE",
  "dni": "40111222",
  "city": "Cordoba",
  "address": "Nueva direccion 789",
  "birth_date": "1995-04-12",
  "hire_date": "2026-01-10",
  "termination_date": null,
  "status": "ACTIVE",
  "base_salary": 1650000,
  "id_branch": 1,
  "id_position": 1,
  "id_department": 1
}
```

Campos:

- Todos los campos del body son opcionales, pero debe enviarse al menos uno.
- Si se envia `id_branch`, `id_position` o `id_department`, actualiza la asignacion laboral activa.
- Si `status` pasa a `TERMINATED`, cierra asignaciones activas y puede setear `termination_date`.

### 24. Actualizar empleado completo

```http
PUT {{baseUrl}}/api/employees/{{employeeDni}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "name": "Ada",
  "lastName": "Lovelace",
  "phoneNumber": "3515550101",
  "genre": "FEMALE",
  "dni": "40111222",
  "city": "Cordoba",
  "address": "Bv. Testing 456",
  "birth_date": "1995-04-12",
  "hire_date": "2026-01-10",
  "termination_date": null,
  "status": "ACTIVE",
  "base_salary": 1500000,
  "id_branch": 1,
  "id_position": 1,
  "id_department": 1
}
```

Campos:

- Requeridos: todos excepto `termination_date`.
- `termination_date`: opcional, pasado o presente.

### 25. Dar de baja empleado

```http
DELETE {{baseUrl}}/api/employees/{{employeeDni}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Path params:

- `employeeDni`: requerido.

Body: no lleva.

## 07 Work Schedules

### 26. Listar mis cronogramas

```http
GET {{baseUrl}}/api/work-schedules/me?from=2026-08-01&to=2026-08-31
```

Auth sugerida: `Bearer {{employeeToken}}`.

Query params opcionales:

- `from`: fecha desde `YYYY-MM-DD`.
- `to`: fecha hasta `YYYY-MM-DD`.

Nota: devuelve solo cronogramas activos del empleado vinculado al token.

### 27. Listar cronogramas

```http
GET {{baseUrl}}/api/work-schedules?dniEmployee={{employeeDni}}&departmentId={{departmentId}}&branchId={{branchId}}&from=2026-08-01&to=2026-08-31&type=WORKDAY&status=ACTIVE&page=0&size=20&sort=workDate,asc
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Query params opcionales:

- `dniEmployee`: DNI del empleado.
- `departmentId`: ID de departamento, positivo.
- `branchId`: ID de sucursal, positivo.
- `from`: fecha desde.
- `to`: fecha hasta.
- `type`: `WORKDAY`, `DAY_OFF`, `HOLIDAY`, `EXTRA_HOURS`.
- `status`: `ACTIVE`, `CANCELLED`.
- `page`, `size`, `sort`: paginacion opcional.

### 28. Obtener cronograma por ID

```http
GET {{baseUrl}}/api/work-schedules/{{workScheduleId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Path params:

- `workScheduleId`: requerido.

### 29. Crear cronograma

```http
POST {{baseUrl}}/api/work-schedules
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "dniEmployee": "40111222",
  "workDate": "2026-08-17",
  "startTime": "08:00:00",
  "endTime": "14:00:00",
  "type": "WORKDAY",
  "note": "Turno manana"
}
```

Campos:

- `dniEmployee`: requerido. 7 u 8 numeros.
- `workDate`: requerido.
- `type`: requerido.
- `startTime`, `endTime`: requeridos para `WORKDAY` y `EXTRA_HOURS`; opcionales/no aplican para `DAY_OFF` y `HOLIDAY`.
- `note`: opcional, max 500.
- `endTime` debe ser posterior a `startTime`.

### 30. Actualizar cronograma

```http
PATCH {{baseUrl}}/api/work-schedules/{{workScheduleId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "dniEmployee": "40111222",
  "workDate": "2026-08-18",
  "startTime": "09:00:00",
  "endTime": "15:00:00",
  "type": "WORKDAY",
  "note": "Turno actualizado"
}
```

Campos:

- Todos los campos son opcionales, pero debe enviarse al menos uno.
- Solo se pueden modificar cronogramas `ACTIVE`.
- Para `DAY_OFF` o `HOLIDAY`, el servicio deja horarios en `null`.

### 31. Cancelar cronograma

```http
PATCH {{baseUrl}}/api/work-schedules/{{workScheduleId}}/cancel
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body: no lleva.

## 08 Work Requests

### 32. Crear mi solicitud de jornada

```http
POST {{baseUrl}}/api/work-requests/me
```

Auth sugerida: `Bearer {{employeeToken}}`.

Body raw JSON:

```json
{
  "requestType": "SHIFT_CHANGE",
  "targetDate": "2026-08-17",
  "startTime": "14:00:00",
  "endTime": "20:00:00",
  "reason": "Cambio de turno por tramite personal",
  "compensationDescription": "Compensa trabajando por la tarde"
}
```

Campos:

- `requestType`: requerido.
- `targetDate`: requerido.
- `startTime`, `endTime`: requeridos para `SHIFT_CHANGE`, `EXTRA_HOURS`, `LATE_ARRIVAL`, `EARLY_LEAVE`; opcionales para `DAY_OFF` y `COMPENSATORY_DAY`.
- `reason`: opcional, max 500.
- `compensationDescription`: opcional, max 500.
- No se envia DNI; se usa el empleado vinculado al token.

### 33. Listar mis solicitudes de jornada

```http
GET {{baseUrl}}/api/work-requests/me?from=2026-08-01&to=2026-08-31&requestType=SHIFT_CHANGE&status=PENDING
```

Auth sugerida: `Bearer {{employeeToken}}`.

Query params opcionales efectivos:

- `from`: fecha desde.
- `to`: fecha hasta.
- `requestType`: `DAY_OFF`, `SHIFT_CHANGE`, `EXTRA_HOURS`, `COMPENSATORY_DAY`, `LATE_ARRIVAL`, `EARLY_LEAVE`.
- `status`: `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`.

Nota: el DTO tambien puede recibir `dniEmployee`, `departmentId` y `branchId`, pero en esta ruta `/me` no se usan para filtrar.

### 34. Obtener mi solicitud por ID

```http
GET {{baseUrl}}/api/work-requests/me/{{workRequestId}}
```

Auth sugerida: `Bearer {{employeeToken}}`.

Path params:

- `workRequestId`: requerido.

### 35. Cancelar mi solicitud

```http
PATCH {{baseUrl}}/api/work-requests/me/{{workRequestId}}/cancel
```

Auth sugerida: `Bearer {{employeeToken}}`.

Body: no lleva.

Nota: solo se pueden cancelar solicitudes propias en estado `PENDING`.

### 36. Listar solicitudes de jornada

```http
GET {{baseUrl}}/api/work-requests?dniEmployee={{employeeDni}}&departmentId={{departmentId}}&branchId={{branchId}}&from=2026-08-01&to=2026-08-31&requestType=SHIFT_CHANGE&status=PENDING&page=0&size=20&sort=targetDate,desc
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Query params opcionales:

- `dniEmployee`: DNI del empleado.
- `departmentId`: ID de departamento, positivo.
- `branchId`: ID de sucursal, positivo.
- `from`: fecha desde.
- `to`: fecha hasta.
- `requestType`: enum de WorkRequestType.
- `status`: enum de RequestStatus.
- `page`, `size`, `sort`: paginacion opcional.

### 37. Obtener solicitud de jornada por ID

```http
GET {{baseUrl}}/api/work-requests/{{workRequestId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Path params:

- `workRequestId`: requerido.

### 38. Aprobar solicitud de jornada

```http
PATCH {{baseUrl}}/api/work-requests/{{workRequestId}}/approve
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON opcional:

```json
{
  "reviewComment": "Cambio de turno aprobado"
}
```

Campos:

- `reviewComment`: opcional, max 500.
- El body completo es opcional; si lo omitis, no enviar `Content-Type`.

### 39. Rechazar solicitud de jornada

```http
PATCH {{baseUrl}}/api/work-requests/{{workRequestId}}/reject
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON opcional:

```json
{
  "reviewComment": "No se puede cubrir el turno solicitado"
}
```

Campos:

- `reviewComment`: opcional, max 500.
- El body completo es opcional.

## 09 Payroll Periods

### 40. Listar periodos de liquidacion

```http
GET {{baseUrl}}/api/payroll-periods?month=8&year=2026&status=OPEN
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Query params opcionales:

- `month`: 1 a 12.
- `year`: anio.
- `status`: `OPEN` o `CLOSED`.

### 41. Obtener periodo por ID

```http
GET {{baseUrl}}/api/payroll-periods/{{payrollPeriodId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Path params:

- `payrollPeriodId`: requerido.

### 42. Crear periodo de liquidacion

```http
POST {{baseUrl}}/api/payroll-periods
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "month": 8,
  "year": 2026
}
```

Campos:

- `month`: requerido, 1 a 12.
- `year`: requerido, mayor o igual a 2000.

### 43. Cerrar periodo de liquidacion

```http
PATCH {{baseUrl}}/api/payroll-periods/{{payrollPeriodId}}/close
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body: no lleva.

Nota: no se puede cerrar si tiene liquidaciones `DRAFT`.

## 10 Payroll Concepts

### 44. Listar conceptos de liquidacion

```http
GET {{baseUrl}}/api/payroll-concepts?name=Bono&type=ADDITION&active=true
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Query params opcionales:

- `name`: busqueda parcial.
- `type`: `ADDITION` o `DEDUCTION`.
- `active`: boolean.

### 45. Obtener concepto por ID

```http
GET {{baseUrl}}/api/payroll-concepts/{{payrollConceptId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Path params:

- `payrollConceptId`: requerido.

### 46. Crear concepto

```http
POST {{baseUrl}}/api/payroll-concepts
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "name": "Bono productividad",
  "description": "Bono mensual por objetivos",
  "type": "ADDITION",
  "active": true
}
```

Campos:

- `name`: requerido, max 100.
- `description`: opcional, max 255.
- `type`: requerido.
- `active`: opcional. Si no se envia, el concepto se crea activo.

### 47. Actualizar concepto

```http
PATCH {{baseUrl}}/api/payroll-concepts/{{payrollConceptId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "name": "Bono productividad actualizado",
  "description": "Bono mensual revisado",
  "type": "ADDITION",
  "active": true
}
```

Campos:

- Todos los campos son opcionales, pero debe enviarse al menos uno.

### 48. Desactivar concepto

```http
DELETE {{baseUrl}}/api/payroll-concepts/{{payrollConceptId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body: no lleva.

## 11 Payrolls

### 49. Listar mis liquidaciones

```http
GET {{baseUrl}}/api/payrolls/me?year=2026
```

Auth sugerida: `Bearer {{employeeToken}}`.

Query params opcionales:

- `year`: mayor o igual a 2000.

Nota: solo devuelve liquidaciones `CONFIRMED` del empleado autenticado.

### 50. Obtener mi liquidacion por ID

```http
GET {{baseUrl}}/api/payrolls/me/{{payrollId}}
```

Auth sugerida: `Bearer {{employeeToken}}`.

Path params:

- `payrollId`: requerido.

Nota: solo permite liquidaciones propias en estado `CONFIRMED`.

### 51. Listar liquidaciones

```http
GET {{baseUrl}}/api/payrolls?periodId={{payrollPeriodId}}&month=8&year=2026&status=DRAFT&dniEmployee={{employeeDni}}&page=0&size=20&sort=idPayroll,desc
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Query params opcionales:

- `periodId`: ID de periodo, positivo.
- `month`: 1 a 12.
- `year`: mayor o igual a 2000.
- `status`: `DRAFT`, `CONFIRMED`, `CANCELLED`.
- `dniEmployee`: DNI del empleado.
- `page`, `size`, `sort`: paginacion opcional.

### 52. Obtener liquidacion por ID

```http
GET {{baseUrl}}/api/payrolls/{{payrollId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Path params:

- `payrollId`: requerido.

### 53. Crear liquidacion borrador

```http
POST {{baseUrl}}/api/payrolls
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "dniEmployee": "40111222",
  "periodId": 1,
  "details": [
    {
      "payrollConceptId": 1,
      "amount": 250000,
      "description": "Bono mensual"
    },
    {
      "payrollConceptId": 2,
      "amount": 50000,
      "description": "Descuento por adelanto"
    }
  ]
}
```

Campos:

- `dniEmployee`: requerido.
- `periodId`: requerido, positivo.
- `details`: opcional. Si se omite o va vacio, crea liquidacion solo con sueldo base.
- En cada detalle: `payrollConceptId` requerido, `amount` requerido positivo, `description` opcional max 255.
- No se puede repetir concepto en la misma liquidacion.

### 54. Actualizar liquidacion borrador

```http
PATCH {{baseUrl}}/api/payrolls/{{payrollId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "dniEmployee": "40111222",
  "periodId": 1,
  "details": [
    {
      "payrollConceptId": 1,
      "amount": 300000,
      "description": "Bono actualizado"
    }
  ]
}
```

Campos:

- Todos los campos son opcionales, pero debe enviarse al menos uno.
- Si se envia `details`, reemplaza el detalle completo de la liquidacion.
- Solo se pueden modificar liquidaciones `DRAFT`.

### 55. Confirmar liquidacion

```http
PATCH {{baseUrl}}/api/payrolls/{{payrollId}}/confirm
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body: no lleva.

### 56. Cancelar liquidacion

```http
PATCH {{baseUrl}}/api/payrolls/{{payrollId}}/cancel
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body: no lleva.

## 12 Vacations

### 57. Listar vacaciones

```http
GET {{baseUrl}}/api/vacations?status=PENDING&startDate=2026-07-01&endDate=2026-07-31&dniEmployee={{employeeDni}}&fullName=Ada%20Lovelace&page=0&size=20&sort=startDate,asc
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Query params opcionales:

- `status`: `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`.
- `startDate`: inicio del rango.
- `endDate`: fin del rango.
- `dniEmployee`: DNI del empleado.
- `fullName`: busqueda por nombre completo.
- `page`, `size`, `sort`: paginacion opcional.

### 58. Crear vacaciones

```http
POST {{baseUrl}}/api/vacations
```

Auth: ADMIN/STAFF o el propio EMPLOYEE del DNI.

Body raw JSON:

```json
{
  "requestDate": "2026-06-11",
  "status": "PENDING",
  "startDate": "2026-07-01",
  "endDate": "2026-07-10",
  "reviewComment": null,
  "dniEmployee": "40111222"
}
```

Campos:

- `requestDate`: opcional, pasado o presente. Si no se envia, se usa la fecha actual.
- `status`: opcional en DTO, pero en creacion el servicio fuerza `PENDING`.
- `startDate`: requerido, presente o futuro.
- `endDate`: requerido, presente o futuro y no anterior a `startDate`.
- `reviewComment`: opcional, max 500; en creacion el servicio lo deja en `null`.
- `dniEmployee`: requerido.
- Debe haber al menos 5 dias habiles entre `requestDate` y `startDate`.

### 59. Actualizar vacaciones

```http
PUT {{baseUrl}}/api/vacations/{{vacationId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "requestDate": "2026-06-11",
  "status": "APPROVED",
  "startDate": "2026-07-05",
  "endDate": "2026-07-15",
  "reviewComment": "Vacaciones aprobadas",
  "dniEmployee": "40111222"
}
```

Campos:

- `startDate`, `endDate`, `dniEmployee`: requeridos.
- `requestDate`, `status`, `reviewComment`: opcionales, pero incluidos aca para Postman.
- Si `status` no es `PENDING`, registra el revisor autenticado.

### 60. Eliminar vacaciones

```http
DELETE {{baseUrl}}/api/vacations/{{vacationId}}
```

Auth: ADMIN o el propio EMPLOYEE si las vacaciones estan `PENDING`.

Path params:

- `vacationId`: requerido.

Body: no lleva.

## 13 Licenses

### 61. Listar licencias

```http
GET {{baseUrl}}/api/licenses?status=PENDING&dniEmployee={{employeeDni}}&startDate=2026-06-01&endDate=2026-06-30&isPaid=true&page=0&size=20&sort=startDate,desc
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Query params opcionales:

- `status`: `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`.
- `dniEmployee`: DNI del empleado.
- `startDate`: inicio del rango.
- `endDate`: fin del rango.
- `isPaid`: boolean.
- `page`, `size`, `sort`: paginacion opcional.

### 62. Obtener licencia por ID

```http
GET {{baseUrl}}/api/licenses/{{licenseId}}
```

Auth: ADMIN/STAFF o el propio EMPLOYEE de la licencia.

Path params:

- `licenseId`: requerido, positivo.

### 63. Crear licencia

```http
POST {{baseUrl}}/api/licenses
```

Auth sugerida: `Bearer {{employeeToken}}`.

Body raw JSON:

```json
{
  "startDate": "2026-06-20",
  "endDate": "2026-06-22",
  "motive": "Control medico",
  "idCertificates": []
}
```

Campos:

- `startDate`: requerido.
- `endDate`: requerido, no anterior a `startDate`.
- `motive`: requerido, max 100.
- `idCertificates`: opcional, max 10 IDs positivos. Normalmente se deja `[]` y luego se suben certificados por form-data.
- La licencia se asocia al empleado vinculado al token; no se envia DNI.

### 64. Revisar licencia

```http
PATCH {{baseUrl}}/api/licenses/{{licenseId}}
```

Auth sugerida: `Bearer {{adminToken}}` o `Bearer {{staffToken}}`.

Body raw JSON:

```json
{
  "status": "APPROVED",
  "isPaid": true,
  "reviewComment": "Licencia aprobada con certificado"
}
```

Campos:

- `status`: requerido.
- `isPaid`: requerido.
- `reviewComment`: opcional, max 500.

### 65. Eliminar licencia

```http
DELETE {{baseUrl}}/api/licenses/{{licenseId}}
```

Auth: ADMIN o el propio EMPLOYEE si la licencia esta `PENDING`.

Path params:

- `licenseId`: requerido, positivo.

Body: no lleva.

## 14 Certificates

### 66. Cargar certificado PDF

```http
POST {{baseUrl}}/api/certificates
```

Auth: ADMIN/STAFF o el propio EMPLOYEE con acceso a la licencia.

Body form-data:

```text
idLicense = {{licenseId}}        type: Text, requerido, positivo
description = Certificado medico type: Text, opcional, max 255
file = certificate.pdf           type: File, requerido
```

Notas:

- Usar `form-data`, no `raw JSON`.
- El archivo debe ser PDF segun el servicio de lectura/guardado de PDFs.

### 67. Descargar certificado PDF

```http
GET {{baseUrl}}/api/certificate/{{certificateId}}
```

Auth: ADMIN/STAFF o el propio EMPLOYEE con acceso al certificado.

Path params:

- `certificateId`: requerido, positivo.

Body: no lleva. Devuelve `application/pdf`.

### 68. Obtener info del certificado

```http
GET {{baseUrl}}/api/certificate-info?id={{certificateId}}
```

Auth: ADMIN/STAFF o el propio EMPLOYEE con acceso al certificado.

Query params:

- `id`: requerido, positivo.

Body: no lleva.

### 69. Eliminar certificado

```http
DELETE {{baseUrl}}/api/certificate/{{certificateId}}
```

Auth: ADMIN/STAFF o el propio EMPLOYEE con acceso al certificado.

Path params:

- `certificateId`: requerido, positivo.

Body: no lleva.
