# Current State - HiveRH

Ultima actualizacion: 2026-10-29.

## Que funciona

- Proyecto Spring Boot con Maven Wrapper y Java 17.
- Autenticacion stateless hibrida: access JWT corto y refresh token opaco rotativo en cookie `HttpOnly`.
- `GET /api/auth/csrf`, `POST /api/auth/login`, `POST /api/auth/refresh` y `POST /api/auth/logout` implementados; los tres `POST` requieren en `X-XSRF-TOKEN` el token XOR enmascarado devuelto por el `GET`.
- Refresh tokens persistidos solo como SHA-256, con expiracion absoluta por familia, bloqueo pesimista para rotacion y deteccion de reutilizacion.
- CORS configurable por allowlist exacta y con credenciales habilitadas.
- `POST /api/auth/register` restringido a `ADMIN`.
- Roles actuales: `ADMIN`, `STAFF`, `EMPLOYEE`.
- Cuentas con `AccountStatus` y username/email unicos en entidad.
- `GET /api/accounts/me` devuelve `email`, `user` y `rol` de la cuenta autenticada sin depender de que tenga un empleado vinculado.
- Alta de empleados con cuenta `EMPLOYEE` automatica.
- Baja logica de empleados: `EmployeeStatus.TERMINATED`, cierre de asignaciones activas y cuenta asociada inactiva.
- Proteccion de cuenta principal `admin` contra baja por flujo de empleado.
- Estructura organizacional: branches, departments y positions.
- Asignaciones laborales historicas con `EmployeeAssignmentEntity`.
- Work schedules con validacion de solapamiento.
- Work requests con restriccion de duplicados pendientes y registro de revisor.
- Payroll nuevo con periodos, conceptos, detalles, snapshot de sueldo y estados.
- Vacaciones/licencias usan `AbsenceStatus`.
- `GET /api/vacations/me` permite consultar en forma paginada las vacaciones del empleado vinculado, con filtros por estado y rango de fechas.
- Certificados PDF asociados a licencias.
- Swagger/OpenAPI disponible en `/swagger-ui.html`.
- Demo cleanup scheduler existe y es opt-in por variables.
- Tests unitarios disponibles para account, employee, security authorization, refresh tokens/cookies, work schedule, work request y payroll.
- Empleados tienen avatar por defecto y endpoints para guardar/consultar foto de perfil.

## Planificacion actual del frontend

- El primer frontend se planifica como `MVP 1.0` y no cubrira todos los modulos del backend.
- El alcance priorizado incluye autenticacion por rol, empleados, estructura organizacional minima, vacaciones, licencias/certificados y horarios laborales.
- El detalle operativo y el backlog sugerido estan en `docs/frontend-mvp-v1.md`.
- Pendiente de confirmar: stack y repositorio frontend, foto de perfil como parte de la demo, pantalla de cuentas/roles y alcance del payroll simplificado.
- Brechas detectadas para el corte vertical: falta el listado `/me` de licencias; el contrato de revision de vacaciones debe confirmarse; la allowlist CORS debe probarse con el origen real del frontend.

## Tests y scripts disponibles

- `src/test/java/com/HiveGroup/HiveRH/HiveRhApplicationTests.java`: carga de contexto Spring.
- `src/test/java/com/HiveGroup/HiveRH/Features/Account/AccountServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/Employee/EmployeeServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Common/Security/Config/SecurityAuthorizationServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Common/Security/Auth/RefreshTokenServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Common/Security/Auth/RefreshTokenCookieServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/WorkSchedule/WorkScheduleControllerTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/WorkSchedule/WorkScheduleServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/WorkRequest/WorkRequestServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/Payroll/PayrollServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/Vacation/VacationControllerTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/Vacation/VacationServiceTest.java`.
- `HiveRH.http`: smoke test manual para IntelliJ HTTP Client, revisar rutas antes de usar.
- `docs/tp3_v02_work/build_tp3_v02.py`: script auxiliar de documentacion.
- `testdata/certificate-sample.pdf`: archivo de prueba para certificados.

## Incompleto o pendiente

- Pendiente de confirmar como se crea/provisiona el primer `ADMIN` en cada ambiente.
- Pendiente de confirmar dominio/origen de produccion del frontend para cerrar `FRONTEND_ORIGINS` y la politica `SameSite`.
- Pendiente de confirmar limpieza periodica de registros de refresh expirados/revocados; se conservan para deteccion de reutilizacion y auditoria basica.
- Pendiente de confirmar estrategia definitiva de schema por ambiente. `application.yaml` y README usan actualmente `spring.jpa.hibernate.ddl-auto: update`.
- No se vio frontend en el repo.
- No se vio una estrategia formal de migraciones tipo Flyway/Liquibase.
- `HiveRH.http` conserva rutas singulares antiguas como `/api/branch`, `/api/department` y `/api/position`; la seccion de autenticacion si fue actualizada al contrato nuevo.
- La coleccion `docs/HIVEGROUP.postman_collection.json` no fue validada en esta inspeccion.
- No hay tests de integracion web completos con filtro JWT, base de datos y permisos por endpoint.
- `HiveRhApplicationTests` requiere `DB_URL` y una base accesible; sin esas variables la suite completa falla al crear JPA aunque los tests unitarios pasen.

## Bugs o riesgos visibles

- `ddl-auto: update` modifica el esquema automaticamente y no reemplaza una estrategia formal de migraciones; revisar su uso antes de apuntar a una base con datos importantes.
- El scheduler de demo cleanup hace deletes fisicos sobre tablas operativas cuando esta habilitado; revisar variables antes de activarlo.
- El scheduler depende de nombres de tablas hardcodeados; cualquier rename de entidad/tabla exige actualizarlo.
- Warnings de compilacion observados en corridas previas:
  - Lombok `@Builder` ignora inicializadores en algunas entidades si no usan `@Builder.Default`.
  - MapStruct reporta propiedades destino no mapeadas en algunos mappers.
- Los endpoints de certificados mezclan plural y singular: `POST /api/certificates`, pero `GET/DELETE /api/certificate/{id}` y `GET /api/certificate-info`.
- Algunos textos de docs antiguas hablan de eliminar vacaciones/licencias; confirmar si el comportamiento deseado es delete fisico, cancelacion o baja logica.

## Proximos pasos recomendados

- Terminar de corregir las rutas singulares antiguas de `HiveRH.http` y validar la coleccion Postman completa contra los controllers.
- Definir si `ddl-auto` debe quedar en `create`, `update` o `validate` segun ambiente.
- Agregar seed/migracion para el primer admin o documentar un flujo SQL unico y actualizado.
- Agregar tests de integracion para seguridad real de endpoints: register, role update, `/me`, payroll y work requests.
- Revisar semantica de delete/cancel para vacaciones, licencias y certificados.
- Considerar un perfil de test con H2 o Testcontainers si se quiere probar repositories/controladores sin depender de MySQL local.
- Revisar warnings de Lombok/MapStruct antes de una entrega final.

## Preguntas abiertas

- El primer admin debe nacer por SQL manual, data initializer, migracion o variable de entorno?
- Register se mantiene solo para admins sin empleado asociado o se eliminara por completo mas adelante?
- Vacaciones/licencias deben borrarse fisicamente o pasar a `CANCELLED` para conservar historial?
- El scheduler de demo cleanup se usara solo en Railway/demo o tambien localmente?
- `ddl-auto: update` es la estrategia intencional para desarrollo y que valor se usara en demo/produccion?
- La documentacion final debe considerar `STAFF` como nombre visible para usuarios o mostrarlo como "Recursos Humanos" en textos de negocio?
