# AGENTS.md - HiveRH

Guia operativa para agentes que retomen el proyecto sin depender del historial de chats.

## Como entender el proyecto

- Empezar por `README.md`, luego `docs/ai-context.md`, `docs/current-state.md`, `docs/decisions.md`, `docs/Requerimiento.md` y, para trabajo de frontend/MVP, `docs/frontend-mvp-v1.md`.
- Para endpoints concretos, contrastar siempre `docs/Postman_Endpoints.md` con los controllers actuales. Hay documentacion auxiliar que puede estar desactualizada.
- La fuente de verdad tecnica esta en `src/main/java/com/HiveGroup/HiveRH` y `src/main/resources/application.yaml`.
- El DER vive como `docs/DER.pdf`. Si una regla del DER contradice el codigo actual, marcarlo como pendiente de confirmar antes de modificar entidades.

## Stack principal

- Java 17.
- Spring Boot 4.0.6.
- Spring Web MVC.
- Spring Security con access JWT stateless y refresh token rotativo en cookie `HttpOnly`.
- Spring Data JPA / Hibernate.
- MySQL.
- Jakarta Bean Validation.
- Lombok.
- MapStruct.
- Springdoc OpenAPI / Swagger UI.
- Maven Wrapper.

## Estructura

- `src/main/java/com/HiveGroup/HiveRH/Common`: seguridad, excepciones, DTOs comunes, enums y utilidades.
- `src/main/java/com/HiveGroup/HiveRH/Features`: modulos por feature. Cada modulo suele tener `Controller`, `Service`, `Repository`, `Entity`, `DTO` y mapper si corresponde.
- `src/main/resources/application.yaml`: configuracion principal por variables de entorno.
- `src/test/java`: tests unitarios y de contexto.
- `docs`: documentacion funcional, endpoints, DER y memoria para agentes.
- `testdata`: archivos de prueba, actualmente un PDF de certificado.
- `HiveRH.http`: smoke test para IntelliJ HTTP Client, pero revisar antes de usar porque algunas rutas pueden estar viejas.

## Comandos

Windows:

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd clean package
```

Linux/macOS:

```bash
./mvnw spring-boot:run
./mvnw test
./mvnw clean package
```

Tests puntuales utiles:

```powershell
.\mvnw.cmd "-Dtest=AccountServiceTest,EmployeeServiceTest,SecurityAuthorizationServiceTest,WorkScheduleControllerTest,WorkScheduleServiceTest,WorkRequestServiceTest,PayrollServiceTest" test
```

Swagger local:

```text
http://localhost:8080/swagger-ui.html
```

Base local esperada:

```sql
CREATE DATABASE IF NOT EXISTS hiverh;
```

Variables principales: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `EMAIL_ADDRESS`, `EMAIL_PASSWORD`, `SECRET`, `EXPIRATION`. Usar `.env.sample` como plantilla y no commitear secretos.

## Convenciones de codigo

- Idioma del codigo: ingles.
- Java: `camelCase`.
- Base de datos: `snake_case`.
- Paquetes: package by feature bajo `Features/<Modulo>`.
- Roles validos: `ADMIN`, `STAFF`, `EMPLOYEE`. No volver a usar `RRHH` como rol.
- Estados actuales:
  - `AccountStatus`: estado de cuenta.
  - `EmployeeStatus`: estado laboral.
  - `AbsenceStatus`: vacaciones/licencias.
  - `RequestStatus`: solicitudes de jornada.
  - `PayrollStatus`, `PayrollPeriodStatus`.
- Para empleados, preferir DNI en endpoints o payloads cuando el patron existente lo usa.
- Evitar exponer entidades directamente si el modulo ya trabaja con DTOs.

## Reglas de seguridad y flujo

- `POST /api/auth/login` es publico.
- `GET /api/auth/csrf`, `POST /api/auth/refresh` y `POST /api/auth/logout` son publicos; login, refresh y logout requieren `X-XSRF-TOKEN`.
- El access JWT se devuelve al frontend y se envia por `Authorization: Bearer`; el refresh token nunca se devuelve en JSON, se guarda como hash y viaja en cookie `HttpOnly`.
- El frontend debe conservar el access token solo en memoria, usar `withCredentials` en auth y no guardar tokens en `localStorage` ni `sessionStorage`.
- CORS debe usar una allowlist exacta. En produccion, las cookies requieren HTTPS; si frontend y API estan en sitios distintos usar `SameSite=None` y `Secure=true`.
- `POST /api/auth/register` es solo `ADMIN` y se conserva para crear cuentas sin empleado asociado.
- El flujo principal es crear empleado desde `POST /api/employees`; eso crea una cuenta `EMPLOYEE` vinculada con usuario/password inicial igual al DNI.
- Para convertir una cuenta vinculada a empleado en `STAFF` o `ADMIN`, usar `PATCH /api/accounts/{identifier}/rol`.
- `STAFF` puede asignar roles que no sean `ADMIN`; solo `ADMIN` puede asignar `ADMIN`.
- La cuenta con username literal `admin` es la cuenta principal protegida y no debe poder darse de baja por el flujo de empleado.
- Los endpoints `/me` deben depender de que la cuenta tenga empleado vinculado, no solo del rol. Un `STAFF` o `ADMIN` con empleado asociado tambien sigue siendo empleado para sus recursos propios.
- Cambios de seguridad deben revisarse tanto en `Common/Security/Config/Config.java` como en annotations `@PreAuthorize` y validaciones del service.

## Reglas de edicion

- No reintroducir modulos de suspensiones ni denuncias sin confirmacion explicita.
- No cambiar el modelo nuevo de payroll sin confirmacion: `PayrollPeriod`, `PayrollConcept`, `PayrollDetail`, `Payroll` con `baseSalarySnapshot`.
- No volver a crear `Variation`; fue reemplazado por conceptos y detalles de payroll.
- No renombrar `STAFF` a `RRHH`.
- No tocar datos reales ni activar schedulers de limpieza sin revisar variables de entorno.
- Antes de correr la app contra una base con datos, revisar `spring.jpa.hibernate.ddl-auto` en `application.yaml`; actualmente esta en `update`, pero no reemplaza migraciones formales y puede alterar el esquema.
- Mantener cambios acotados al pedido. No refactorizar controllers/services ajenos si no es necesario.
- Tests y documentacion deben versionarse. `target/`, `.env` e IDE metadata no.

## Verificacion

- Para reglas de negocio, agregar o ajustar tests unitarios en `src/test/java`.
- Para seguridad, cubrir endpoint/filter cuando corresponda y tambien service-level authorization.
- Para cambios de API, actualizar Swagger annotations y docs relevantes.
- Para cambios de endpoints, contrastar controllers con `docs/Postman_Endpoints.md` y `HiveRH.http`.
- Si Git marca `dubious ownership`, usar un comando puntual con `git -c safe.directory=C:/Users/herre/Documents/Github/HiveRH ...` en vez de cambiar configuracion global sin permiso.
