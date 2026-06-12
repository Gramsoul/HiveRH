# HiveRH

HiveRH es una API REST para la gestion de Recursos Humanos. Permite administrar empleados, cuentas de usuario, roles, estructura organizacional, liquidaciones de sueldo, licencias, vacaciones, suspensiones, denuncias y certificados.

El proyecto esta planteado como un MVP academico: el foco esta en tener reglas de negocio claras, autenticacion con JWT, permisos por rol y endpoints faciles de probar desde Postman o Swagger.

## Documentacion

La documentacion detallada esta en la carpeta `docs`:

- `docs/Requerimiento.md`: alcance funcional y reglas generales del sistema.
- `docs/Informe_Entidades_Endpoints.md`: recorrido completo del sistema, entidades, endpoints y flujo recomendado para Postman/defensa.
- `docs/Conceptual.md`: modelo conceptual del dominio.
- `docs/DER.pdf`: exportacion PDF del diagrama, mantenida como material complementario.

Este README queda como guia rapida para levantar y entender el proyecto. Para el detalle completo de endpoints conviene ir al informe.

## Requisitos

- JDK 21, de acuerdo con la configuracion actual de compilacion del `pom.xml`.
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
| `SECRET` | Clave para firmar JWT | `clave-super-secreta-de-32-bytes-minimo` |
| `EXPIRATION` | Duracion del token en milisegundos | `86400000` |

Ejemplo:

```properties
DB_URL=jdbc:mysql://localhost:3306/hiverh
DB_USER=root
DB_PASSWORD=admin
SECRET=clave-super-secreta-de-32-bytes-minimo
EXPIRATION=86400000
```

No es obligatorio usar un archivo `.env`. Cada integrante puede configurar estas variables como prefiera: desde IntelliJ IDEA, desde la terminal, desde variables del sistema operativo o desde el entorno que use para ejecutar la aplicacion.

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

## Ejecucion local

La API queda disponible por defecto en:

```text
http://localhost:8080
```

## Autenticacion

La API usa JWT. Para consumir endpoints protegidos:

1. Ejecutar `POST /api/auth/login`.
2. Copiar el token recibido.
3. Enviar el token en cada request protegido:

```http
Authorization: Bearer <token>
```

Roles principales:

- `ADMIN`: administra todo el sistema.
- `RRHH`: gestiona empleados, licencias, vacaciones, suspensiones, denuncias y liquidaciones.
- `EMPLOYEE`: consulta y opera sobre recursos propios cuando la regla de negocio lo permite.

## Swagger

Con la aplicacion levantada:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Swagger esta liberado para facilitar pruebas y revision de contratos.

## Endpoints base

El detalle completo de endpoints esta en `docs/Informe_Entidades_Endpoints.md`. Como referencia rapida, los modulos principales son:

| Modulo | Base path |
|---|---|
| Auth | `/api/auth` |
| Accounts | `/api/accounts` |
| Employees | `/api/employees` |
| Branches | `/api/branch` |
| Departments | `/api/department` |
| Positions | `/api/position` |
| Variations | `/api/variations` |
| Payrolls | `/api/payrolls` |
| Licenses | `/api/license` |
| Certificates | `/api/certificate` |
| Vacations | `/api/vacation` |
| Complaints | `/api/complaint` |
| Suspensions | `/api/suspension` |

Los filtros en endpoints `GET` se envian por query params. No hace falta mandar todos los filtros: se puede enviar uno, varios o ninguno.

Ejemplos:

```http
GET /api/employees?dni=43917621
GET /api/vacation?accepted=false&fullName=Juan Perez
GET /api/payrolls/employee/3?startDate=2026-01-01&endDate=2026-06-30
```

## Reglas importantes

- Un empleado no puede consultar liquidaciones de otro empleado.
- RRHH y ADMIN pueden consultar liquidaciones de cualquier empleado.
- Solo RRHH y ADMIN pueden cargar, modificar o borrar liquidaciones.
- No se permite cargar dos liquidaciones para el mismo empleado en el mismo mes.
- El empleado puede eliminar sus propias solicitudes de licencia o vacaciones solo si no fueron aceptadas.
- RRHH no elimina solicitudes de licencia/vacaciones: las gestiona, aprueba o rechaza.
- ADMIN puede administrar todos los recursos.
- Las denuncias solo pueden ser listadas o revisadas por RRHH o ADMIN.

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
