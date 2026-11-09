# HiveRH

[English version](README.en.md)

HiveRH es una API REST para la gestion de Recursos Humanos. Permite administrar empleados, cuentas de usuario, roles, estructura organizacional, cronogramas laborales, solicitudes de jornada, liquidaciones de sueldo, licencias, vacaciones y certificados.

El proyecto esta planteado como un MVP academico: el foco esta en tener reglas de negocio claras, autenticacion con JWT, permisos por rol y endpoints faciles de probar desde Postman o Swagger.

## Documentacion

La documentacion detallada esta en la carpeta `docs`:

- `docs/Requerimiento.md`: alcance funcional y reglas generales del sistema.
- `docs/frontend-mvp-v1.md`: alcance, brechas y backlog sugerido para el desarrollo del frontend y la presentacion del MVP 1.0.
- `docs/Informe_Entidades_Endpoints.md`: recorrido completo del sistema, entidades, endpoints y flujo recomendado para Postman/defensa.
- `docs/DER.pdf`: diagrama entidad-relacion.

Este README queda como guia rapida para levantar y entender el proyecto. Para el detalle completo de endpoints conviene ir al informe.

## Requisitos

- JDK 17 o superior.
- MySQL corriendo localmente o en un servidor accesible.
- Maven Wrapper incluido en el repositorio (`mvnw.cmd` / `mvnw`), o Maven instalado.
- Variables de entorno configuradas en el entorno de ejecucion elegido.

## Configuracion

La aplicacion toma su configuracion desde `src/main/resources/application.yaml`. Las variables necesarias son:

| Variable | Descripcion | Ejemplo |
|---|---|---|
| `DB_URL` | URL JDBC de la base MySQL | `jdbc:mysql://localhost:3306/hiverh` |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Password de MySQL | `admin` |
| `EMAIL_ADDRESS` | Email usado como remitente SMTP | `hiverh.notificaciones@gmail.com` |
| `EMAIL_PASSWORD` | Password de aplicacion del email SMTP | `abcd efgh ijkl mnop` |
| `SECRET` | Clave para firmar JWT | `clave-super-secreta-de-32-bytes-minimo` |
| `EXPIRATION` | Duracion del access token JWT en milisegundos | `600000` |
| `REFRESH_EXPIRATION` | Duracion maxima de una familia de refresh tokens en milisegundos | `28800000` |
| `REFRESH_COOKIE_NAME` | Nombre de la cookie HttpOnly de refresh | `hiverh_refresh` |
| `REFRESH_COOKIE_SECURE` | Envia la cookie solo por HTTPS; usar `false` unicamente en local HTTP | `true` |
| `REFRESH_COOKIE_SAME_SITE` | Politica SameSite de las cookies de auth | `Strict` |
| `FRONTEND_ORIGINS` | Allowlist CORS exacta, separada por comas | `http://localhost:4200` |
| `DEMO_CLEANUP_ENABLED` | Activa la limpieza automatica de datos demo | `false` |
| `DEMO_CLEANUP_DAILY_CRON` | Cron diario de limpieza | `0 0 4 * * *` |
| `DEMO_CLEANUP_ZONE` | Zona horaria del cron | `UTC` |
| `DEMO_CLEANUP_MAX_RECORDS` | Cantidad maxima de registros antes de limpiar | `5000` |
| `DEMO_CLEANUP_INCLUDE_CATALOG_DATA` | Tambien borra sucursales, departamentos, puestos y conceptos de liquidacion | `true` |
| `DEMO_CLEANUP_PRESERVED_ACCOUNT_USERS` | Usuarios que nunca se borran, separados por coma | `admin` |

Ejemplo:

```properties
DB_URL=jdbc:mysql://localhost:3306/hiverh
DB_USER=root
DB_PASSWORD=admin
EMAIL_ADDRESS=hiverh.notificaciones@gmail.com
EMAIL_PASSWORD=abcd efgh ijkl mnop
SECRET=clave-super-secreta-de-32-bytes-minimo
EXPIRATION=600000
REFRESH_EXPIRATION=28800000
REFRESH_COOKIE_NAME=hiverh_refresh
REFRESH_COOKIE_SECURE=false
REFRESH_COOKIE_SAME_SITE=Strict
FRONTEND_ORIGINS=http://localhost:4200
DEMO_CLEANUP_ENABLED=false
DEMO_CLEANUP_DAILY_CRON=0 0 4 * * *
DEMO_CLEANUP_ZONE=UTC
DEMO_CLEANUP_MAX_RECORDS=5000
DEMO_CLEANUP_INCLUDE_CATALOG_DATA=true
DEMO_CLEANUP_PRESERVED_ACCOUNT_USERS=admin
```

Para Gmail se recomienda usar una password de aplicacion, no la password personal de la cuenta.

No es obligatorio usar un archivo `.env`. Cada integrante puede configurar estas variables como prefiera: desde IntelliJ IDEA, desde la terminal, desde variables del sistema operativo o desde el entorno que use para ejecutar la aplicacion.

El repositorio incluye `.env.sample` como plantilla. Se puede copiar a `.env` y ajustar valores locales sin subir secretos al repositorio.

En IntelliJ IDEA:

```text
Run/Debug Configurations > Environment variables
```

## Base de datos

HiveRH usa MySQL. Antes de levantar la aplicacion, la base debe existir:

```sql
CREATE DATABASE IF NOT EXISTS hiverh;
```

Hibernate esta configurado con `ddl-auto: update`, por lo que puede crear o actualizar tablas dentro de esa base, pero no crea la base de datos MySQL desde cero.

La aplicacion espera que exista al menos una cuenta administradora en la base usada para probar el sistema.

## Limpieza de demo

Para entornos publicos de prueba se puede activar una limpieza automatica de datos. El job borra datos operativos y, si `DEMO_CLEANUP_INCLUDE_CATALOG_DATA=true`, tambien borra catalogos creados desde Swagger. Los usuarios indicados en `DEMO_CLEANUP_PRESERVED_ACCOUNT_USERS` no se eliminan.

En Railway se recomienda activarlo con variables de entorno, por ejemplo:

```properties
DEMO_CLEANUP_ENABLED=true
DEMO_CLEANUP_DAILY_CRON=0 0 4 * * *
DEMO_CLEANUP_ZONE=UTC
DEMO_CLEANUP_MAX_RECORDS=1000
DEMO_CLEANUP_INCLUDE_CATALOG_DATA=true
DEMO_CLEANUP_PRESERVED_ACCOUNT_USERS=admin
```

## Ejecucion local

La API queda disponible por defecto en:

```text
http://localhost:8080
```

Con Maven Wrapper:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Autenticacion

La API usa un access token JWT corto y un refresh token opaco rotativo. El access token se devuelve en JSON; el refresh token se guarda en una cookie `HttpOnly`, nunca se expone al JavaScript del frontend y en la base solo se persiste su hash.

Flujo para clientes web:

1. Ejecutar `GET /api/auth/csrf` con credenciales/cookies habilitadas y conservar `token` de la respuesta.
2. Ejecutar `POST /api/auth/login` enviando ese valor en `X-XSRF-TOKEN`, tambien con credenciales habilitadas.
3. Conservar `accessToken` solo en memoria y enviarlo en cada request protegido:

```http
Authorization: Bearer <token>
```

4. Ante el primer `401`, ejecutar una sola vez `POST /api/auth/refresh` con la cabecera CSRF y cookies habilitadas. La API rota la cookie y devuelve otro access token.
5. Ejecutar `POST /api/auth/logout` para revocar la familia de refresh tokens y borrar la cookie.

El campo `token` devuelto por `/api/auth/csrf` esta enmascarado con XOR para mitigar BREACH y por eso no coincide visualmente con el valor base de la cookie `XSRF-TOKEN`. El cliente debe conservar el campo de la respuesta y enviarlo sin modificar en `X-XSRF-TOKEN`; no debe leer, comparar ni desenmascarar la cookie. El backend realiza el desenmascarado durante la validacion.

No guardar access ni refresh tokens en `localStorage` o `sessionStorage`. La respuesta de login/refresh incluye `roles` para navegacion y presentacion, pero el backend sigue siendo la autoridad de permisos. El contrato frontend completo esta en `docs/frontend-mvp-v1.md`.

Para despliegues donde frontend y API esten en sitios distintos se debe usar `REFRESH_COOKIE_SAME_SITE=None`, `REFRESH_COOKIE_SECURE=true`, HTTPS y una allowlist exacta en `FRONTEND_ORIGINS`. Con frontend y API en el mismo sitio se prefiere `Strict`.

Roles principales:

- `ADMIN`: administra todo el sistema.
- `STAFF`: gestiona empleados, cronogramas laborales, solicitudes de jornada, licencias, vacaciones y liquidaciones.
- `EMPLOYEE`: consulta y opera sobre recursos propios cuando la regla de negocio lo permite.

## Swagger

Con la aplicacion levantada:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Swagger esta liberado para facilitar pruebas y revision de contratos. Swagger no guarda datos por si mismo: ejecuta requests reales contra la API. Por eso, todo lo que se cree desde Swagger queda guardado en la base MySQL configurada en `DB_URL`.

Flujo recomendado para probar desde Swagger:

1. Levantar MySQL y crear la base `hiverh`.
2. Configurar las variables de entorno.
3. Ejecutar la aplicacion.
4. Entrar a `http://localhost:8080/swagger-ui.html`.
5. Ejecutar `GET /api/auth/csrf` y copiar `token` de la respuesta.
6. Ejecutar `POST /api/auth/login` con una cuenta existente y enviar el token CSRF en la cabecera `X-XSRF-TOKEN`.
7. Copiar `accessToken` de la respuesta.
8. Presionar `Authorize` y pegar solo el access token JWT.

Una vez autorizado, Swagger envia el JWT en los endpoints protegidos.

## Endpoints base

El detalle completo de endpoints esta en `docs/Informe_Entidades_Endpoints.md`. Como referencia rapida, los modulos principales son:

| Modulo | Base path |
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

Los filtros en endpoints `GET` se envian por query params. No hace falta mandar todos los filtros: se puede enviar uno, varios o ninguno.

Ejemplos:

```http
GET /api/employees?dni=43917621&page=0&size=10
GET /api/work-schedules/me?from=2026-08-01&to=2026-08-31
GET /api/work-requests?departmentId=2&status=PENDING&page=0&size=10
GET /api/vacations?status=PENDING&fullName=Juan Perez&page=0&size=10
GET /api/vacations/me?status=PENDING&page=0&size=10
GET /api/payrolls?periodId=1&page=0&size=10
GET /api/payrolls/me?year=2026
```

## Paginacion

Los endpoints paginados usan los parametros estandar de Spring `Pageable`:

```http
page=0
size=10
sort=startDate,desc
```

`page` empieza en 0. `sort` es opcional y ordena los resultados sin cambiar los filtros aplicados.

Endpoints con paginacion:

| Modulo | Endpoint |
|---|---|
| Employees | `GET /api/employees` |
| Work Schedules | `GET /api/work-schedules` |
| Work Requests | `GET /api/work-requests` |
| Licenses | `GET /api/licenses` |
| Payrolls | `GET /api/payrolls` |
| Vacations | `GET /api/vacations` |
| Own vacations | `GET /api/vacations/me` |

Ejemplos:

```http
GET /api/employees?page=0&size=10
GET /api/work-schedules?branchId=1&from=2026-08-01&to=2026-08-31&page=0&size=10
GET /api/work-requests?status=PENDING&page=0&size=10
GET /api/licenses?status=PENDING&page=0&size=10&sort=requestDate,desc
GET /api/payrolls?page=0&size=10
GET /api/vacations?dniEmployee=43917621&page=0&size=10
GET /api/vacations/me?status=APPROVED&page=0&size=10
```

## Reglas importantes

- Un empleado no puede consultar liquidaciones de otro empleado.
- STAFF y ADMIN pueden consultar liquidaciones de cualquier empleado.
- Solo STAFF y ADMIN pueden cargar, modificar, confirmar o anular liquidaciones.
- El empleado solo puede consultar sus propias liquidaciones confirmadas.
- No se permite cargar dos liquidaciones activas para el mismo empleado en el mismo periodo.
- El empleado solo puede consultar su propio cronograma laboral activo.
- Una cuenta con empleado vinculado puede consultar solo sus propias solicitudes de vacaciones desde `GET /api/vacations/me`.
- El empleado puede crear y consultar sus propias solicitudes de jornada, y cancelarlas solo si siguen PENDING.
- STAFF y ADMIN pueden crear, modificar o cancelar cronogramas laborales sin borrar registros fisicos.
- STAFF y ADMIN pueden aprobar o rechazar solicitudes de jornada; al aprobar se registra el revisor y se actualiza el cronograma laboral.
- No se permiten cronogramas activos superpuestos para el mismo empleado, fecha y rango horario.
- No se permiten dos solicitudes PENDING del mismo tipo para el mismo empleado y fecha objetivo.
- El empleado puede eliminar sus propias solicitudes de licencia o vacaciones solo si siguen en estado PENDING.
- STAFF no elimina solicitudes de licencia/vacaciones: las gestiona, aprueba o rechaza.
- ADMIN puede administrar todos los recursos.

## Errores comunes

- `401 Unauthorized`: falta token o el token no es valido.
- `403 Forbidden`: el usuario esta autenticado, pero no tiene permisos para esa accion.
- `404 Not Found`: el recurso solicitado no existe.
- `415 Unsupported Media Type`: el `Content-Type` no coincide con lo que espera el endpoint. Por ejemplo, enviar JSON a un endpoint que espera `multipart/form-data`.

## Stack tecnico

- Java
- Spring Boot
- Spring Web MVC
- Spring Security
- JWT con `jjwt`
- Spring Data JPA
- Hibernate
- MySQL
- Bean Validation / Jakarta Validation
- Lombok
- MapStruct
- Springdoc OpenAPI / Swagger UI
- Maven

## Autores

- Gallego Romero Gonzalo N.
- Herrera Victor M.
- Molina Cristian N.
- Romero Rajoy Jose L.
