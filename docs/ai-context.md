# AI Context - HiveRH

Ultima actualizacion: 2026-10-29.

## Contexto general

HiveRH es una API REST academica para gestion interna de Recursos Humanos. El objetivo es administrar empleados, cuentas, roles, estructura organizacional, jornadas laborales, solicitudes puntuales, liquidaciones, vacaciones, licencias y certificados.

El proyecto esta pensado como MVP: reglas de negocio claras, autenticacion JWT, autorizacion por rol y endpoints probables desde Swagger, Postman o IntelliJ HTTP Client.

## Usuarios objetivo

- `ADMIN`: administra configuracion base, cuentas, roles y recursos generales.
- `STAFF`: opera la gestion diaria de Recursos Humanos.
- `EMPLOYEE`: consulta y gestiona recursos propios.

Importante: una cuenta `STAFF` o `ADMIN` puede tener empleado vinculado. En ese caso debe poder usar endpoints `/me` propios cuando la regla lo permita.

## Estado actual del producto

- Backend Spring Boot con arquitectura package by feature.
- API REST protegida con access JWT corto y refresh token opaco rotativo en cookie `HttpOnly`.
- MySQL como persistencia.
- Swagger/OpenAPI configurado.
- Documentacion funcional existente en `README.md`, `docs/Requerimiento.md`, `docs/Postman_Endpoints.md` y `docs/Informe_Entidades_Endpoints.md`.
- Tests unitarios recientes para cuentas, seguridad, empleados, work schedules, work requests y payroll.
- Tests especificos para rotacion, reutilizacion, revocacion y atributos de la cookie de refresh.
- No hay frontend en el repo inspeccionado.

## Frontend y MVP 1.0

El equipo inicio la planificacion del frontend con un corte vertical reducido para la presentacion del MVP 1.0. La prioridad no es exponer todos los modulos existentes del backend, sino completar recorridos demostrables para login por rol, empleados, estructura organizacional minima, vacaciones, licencias con certificados PDF y horarios laborales.

El alcance, las exclusiones, las brechas de contrato detectadas y una propuesta de backlog Scrum/Jira estan documentados en `docs/frontend-mvp-v1.md`.

Este recorte aplica al frontend y al MVP 1.0; no elimina features del backend. Payroll, WorkRequest, administracion avanzada de cuentas y otras funciones no incluidas quedan fuera de la primera interfaz salvo confirmacion posterior.

## Autenticacion para el frontend

- El access token es un JWT corto: se devuelve en `accessToken`, se conserva solo en memoria y se envia como `Authorization: Bearer`.
- El refresh token es opaco, rotativo y de un solo uso. El navegador lo recibe como cookie `HttpOnly`; JavaScript no debe intentar leerlo y la base guarda solo su hash.
- Antes de `login`, `refresh` o `logout`, el cliente obtiene `GET /api/auth/csrf` y envia el campo `token` sin modificar como cabecera `X-XSRF-TOKEN`. Ese campo esta enmascarado con XOR y no coincide visualmente con la cookie base `XSRF-TOKEN`; el backend lo desenmascara al validarlo. Las llamadas de auth deben usar credenciales/cookies (`withCredentials: true`).
- `POST /api/auth/refresh` rota la cookie y devuelve otro `accessToken`. Ante un `401`, el frontend hace un solo refresh y reintenta la solicitud original una vez; un `403` no dispara refresh.
- `POST /api/auth/logout` revoca toda la familia del refresh token y borra la cookie.
- `roles` y `mustChangePassword` vienen en login/refresh para navegacion y UX. El frontend no necesita decodificar el JWT y nunca debe tratar esos datos como autorizacion definitiva.
- `identifier` en login/refresh siempre representa el username real de la cuenta, aunque el inicio de sesion se haya hecho con email. `GET /api/accounts/me` devuelve el `email`, `user` y `rol` actuales de la cuenta autenticada.
- Cambios de rol, email, password y baja del empleado revocan los refresh tokens de la cuenta. El access token ya emitido puede vivir hasta su expiracion corta.
- CORS usa una allowlist exacta en `FRONTEND_ORIGINS` y permite credenciales. Si frontend y API estan en sitios distintos, configurar `SameSite=None`, `Secure=true` y HTTPS.

## Modulos principales

- `Auth`: login y register administrativo.
- `Account`: consulta de la cuenta autenticada mediante `GET /api/accounts/me`, listado de cuentas, cambio de rol, email propio y password propia.
- `Employee`: alta, consulta, actualizacion y baja logica de empleados.
- `EmployeeAssignment`: historial de asignaciones laborales con sucursal, departamento, puesto, `startDate`, `endDate` y `active`.
- `Branch`, `Department`, `Position`: catalogos organizacionales.
- `WorkSchedule`: cronogramas asignados por fecha, tipo y horario cuando aplica.
- `WorkRequest`: solicitudes puntuales del empleado sobre su jornada.
- `PayrollPeriod`: periodo mensual de liquidacion.
- `PayrollConcept`: concepto reutilizable de suma o descuento.
- `PayrollDetail`: detalle aplicado a una liquidacion concreta.
- `Payroll`: liquidacion mensual con snapshot de sueldo base y estado.
- `Vacation`: solicitudes de vacaciones.
- `License`: licencias del empleado.
- `Certificate`: certificados PDF asociados a licencias.
- `Common/Maintenance`: scheduler opt-in para limpiar datos demo.

## Flujos importantes

### Primer acceso y cuentas

El sistema necesita una cuenta `ADMIN` inicial cargada previamente por seed, SQL manual o base preparada. `POST /api/auth/register` ya no es publico: solo `ADMIN` puede usarlo.

El flujo principal para usuarios operativos es:

1. `ADMIN` o `STAFF` crea un empleado en `POST /api/employees`.
2. El sistema crea automaticamente una cuenta `EMPLOYEE` vinculada al empleado, con usuario/password inicial igual al DNI.
3. Un usuario autorizado actualiza el rol con `PATCH /api/accounts/{identifier}/rol`.
4. Solo `ADMIN` puede asignar rol `ADMIN`; `STAFF` no puede crear ni asignar admins.

### Empleados y asignaciones

El empleado no guarda directamente sucursal/departamento/puesto como campos simples. Esos datos viven en `EmployeeAssignmentEntity`. Al crear empleado se crea la primera asignacion activa. Al cambiar sucursal/departamento/puesto se cierra la activa y se crea otra, preservando historial.

### Jornada laboral

`WorkSchedule` representa lo asignado por la empresa. `WorkRequest` representa lo pedido por el empleado. Al aprobar una `WorkRequest`, el sistema registra el revisor y genera o ajusta el cronograma laboral.

Restricciones clave:

- No puede haber cronogramas activos superpuestos para el mismo empleado, fecha y rango horario.
- No puede haber dos solicitudes `PENDING` del mismo tipo para el mismo empleado y fecha objetivo.
- El empleado solo cancela solicitudes propias pendientes.

### Payroll

El modelo nuevo organiza liquidaciones por periodo mensual. `Payroll` guarda `baseSalarySnapshot`, calcula `totalAdditions` y `totalDeductions` desde `PayrollDetail`, y obtiene el total con:

```text
baseSalarySnapshot + totalAdditions - totalDeductions
```

Las liquidaciones nacen `DRAFT`, pueden confirmarse como `CONFIRMED` o anularse como `CANCELLED`. El empleado solo consulta las propias confirmadas.

### Ausencias

Vacaciones y licencias usan `AbsenceStatus`. El empleado puede eliminar/cancelar sus propias solicitudes pendientes segun autorizacion. `STAFF` y `ADMIN` revisan solicitudes.

## Integraciones externas

- MySQL por JDBC.
- SMTP, configurado para Gmail por `spring.mail`.
- Access JWT con `jjwt` y refresh tokens opacos persistidos como hash.
- Swagger/OpenAPI con Springdoc.
- Postman/IntelliJ HTTP Client para pruebas manuales.
- Railway se menciona como posible entorno demo en README, principalmente por variables de cleanup.

## Supuestos relevantes

- El usuario `admin` literal representa la cuenta principal y debe conservarse.
- La app no debe depender de registros fisicos borrados para historial; se prefieren estados como `INACTIVE`, `TERMINATED`, `CANCELLED` o desactivacion logica cuando el modulo lo soporta.
- Las rutas de empleado suelen usar DNI cuando se identifica un empleado desde API.
- `docs/DER.pdf` es un artefacto importante, pero debe contrastarse con el codigo actual antes de implementar cambios.
- Pendiente de confirmar: estrategia definitiva para crear el primer ADMIN y para manejar schema en ambientes con datos reales.
- Pendiente de confirmar: dominio final del frontend y la API para elegir `SameSite=Strict` (mismo sitio) o `SameSite=None; Secure` (sitios distintos).
- Pendiente de confirmar: politica de retencion/limpieza de sesiones de refresh expiradas y revocadas despues del MVP.
