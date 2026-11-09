# Decisions - HiveRH

Ultima actualizacion: 2026-10-21.

## Decisiones registradas

### Access JWT corto y refresh token rotativo en cookie

- Fecha: septiembre 2026.
- Decision: usar un access JWT corto en memoria del frontend y un refresh token opaco, rotativo y de un solo uso en cookie `HttpOnly`.
- Motivo: limitar la exposicion del JWT, evitar persistir credenciales de larga duracion en Web Storage y permitir revocacion de sesiones.
- Consecuencia: login/refresh devuelven `accessToken`, `roles`, `identifier`, `expiresInSeconds` y `mustChangePassword`; el frontend no necesita decodificar el JWT.
- Consecuencia: el refresh token solo se persiste como SHA-256, mantiene expiracion absoluta por familia y la reutilizacion de uno rotado revoca la familia activa.
- Consecuencia: cambios de rol, email, password y baja laboral revocan todas las sesiones de refresh de la cuenta.

### CSRF y CORS para endpoints con cookie

- Fecha: septiembre 2026.
- Decision: exigir double-submit CSRF en `POST /api/auth/login`, `POST /api/auth/refresh` y `POST /api/auth/logout`; el resto de la API sigue autenticado por bearer token.
- Motivo: esos endpoints dependen de estado transportado automaticamente por el navegador o crean una sesion asociada a una cookie.
- Decision: `GET /api/auth/csrf` expone una representacion XOR enmascarada del token para mitigar BREACH. La cookie `XSRF-TOKEN` conserva el valor base y no tiene que coincidir visualmente con el cuerpo de la respuesta.
- Consecuencia: el frontend debe conservar `response.token`, enviarlo sin modificar como `X-XSRF-TOKEN` y usar `withCredentials: true` en auth. No debe leer ni desenmascarar la cookie; Spring realiza el desenmascarado al validar el header.
- Decision: CORS acepta credenciales solo para la allowlist exacta `FRONTEND_ORIGINS`; no usar `*` con cookies.
- Pendiente de confirmar: con dominios de produccion definitivos, elegir entre `SameSite=Strict` o `SameSite=None; Secure`.

### Corte vertical para Frontend MVP 1.0

- Fecha: 2026-09-15.
- Decision: el primer frontend no intentara cubrir todos los modulos del backend. Se concentrara en autenticacion por rol, empleados, estructura organizacional minima, vacaciones, licencias con certificados y horarios laborales.
- Motivo: disponer de recorridos completos y demostrables dentro del tiempo de la cursada, priorizando el objetivo SMART y las historias MUST del trabajo practico.
- Consecuencia: payroll, WorkRequest, administracion avanzada de cuentas y el resto de las features no incluidas no deben generar pantallas para el MVP 1.0 sin una decision posterior.
- Trazabilidad: `docs/frontend-mvp-v1.md` contiene alcance, brechas backend, epicas, historias, tareas y criterios de gestion para Jira.
- Pendiente de confirmar: foto de perfil como SHOULD, prioridad exacta de horarios, necesidad de UI de cuentas/roles y exclusion total del payroll simplificado.

### Arquitectura package by feature

- Fecha aproximada: previa a agosto 2026.
- Decision: organizar modulos bajo `Features/<Modulo>` con controller, service, repository, entity, DTO y mapper cuando aplica.
- Motivo: mantener cada dominio acotado y facil de navegar.
- Consecuencia: al modificar una feature, revisar primero su carpeta y luego dependencias en `Common`.

### Roles `ADMIN`, `STAFF`, `EMPLOYEE`

- Fecha aproximada: agosto 2026.
- Decision: usar `STAFF` como rol operativo de Recursos Humanos en lugar de `RRHH`.
- Motivo: nombre mas general y consistente.
- Consecuencia: no reintroducir `RRHH` en enums, seguridad, docs o tests.

### Register solo administrativo

- Fecha aproximada: agosto 2026.
- Decision: `POST /api/auth/register` queda permitido solo para `ADMIN`.
- Motivo: el flujo principal de usuarios debe pasar por alta de empleado y cuenta vinculada; register se conserva para cuentas sin empleado asociado, especialmente admins adicionales.
- Consecuencia: el primer admin debe existir por seed, SQL manual o base preparada. `STAFF` no crea cuentas por register.
- Alternativa descartada: register publico o disponible para `STAFF`.

### Alta de empleado crea cuenta vinculada

- Fecha aproximada: previa/agosto 2026.
- Decision: `POST /api/employees` crea empleado activo, primera asignacion laboral y una cuenta `EMPLOYEE` con usuario/password inicial igual al DNI.
- Motivo: simplificar onboarding y asegurar que el empleado tenga identidad en el sistema.
- Consecuencia: para crear un staff real, primero se crea empleado y luego se actualiza el rol de su cuenta.

### Actualizacion de roles con path `/rol`

- Fecha aproximada: agosto 2026.
- Decision: unificar el endpoint de cambio de rol en `PATCH /api/accounts/{identifier}/rol`.
- Motivo: evitar mezcla entre `/role` y `/rol`.
- Consecuencia: actualizar clientes/docs si aparece `/role`.

### Restricciones de rol

- Fecha aproximada: agosto 2026.
- Decision: solo `ADMIN` puede asignar `ADMIN`; `STAFF` puede asignar roles que no sean `ADMIN`; `EMPLOYEE` no puede cambiar roles.
- Motivo: evitar escalada de privilegios.
- Consecuencia: probar siempre cambios de rol desde service y desde capa web/security.

### Cuenta principal protegida

- Fecha aproximada: agosto 2026.
- Decision: la cuenta con username literal `admin` no debe poder darse de baja desde el flujo de empleado.
- Motivo: conservar acceso administrativo principal.
- Consecuencia: cambios en baja de empleados deben mantener esta proteccion.

### Estados con nombres especificos

- Fecha aproximada: agosto 2026.
- Decision: usar `AccountStatus`, `EmployeeStatus` y `AbsenceStatus` en lugar de enums genericos o viejos.
- Motivo: nombres mas claros por dominio.
- Consecuencia: no reintroducir `StatusEnum` ni `LicenseStatusEnum`.

### Suspensiones y denuncias removidas

- Fecha aproximada: agosto 2026.
- Decision: eliminar modulos de suspensiones y denuncias.
- Motivo: no cerraban en la idea funcional del proyecto.
- Consecuencia: no agregar permisos, schedulers, entidades ni relaciones nuevas para esas features sin confirmacion explicita.

### Asignacion laboral historica

- Fecha aproximada: agosto 2026.
- Decision: mover sucursal/departamento/puesto a `EmployeeAssignmentEntity` con `startDate`, `endDate` y `active`.
- Motivo: conservar historial de cambios laborales.
- Consecuencia: filtros por sucursal/departamento/puesto deben considerar asignacion activa o efectiva para la fecha.

### Payroll mensual por periodo

- Fecha aproximada: agosto 2026.
- Decision: reemplazar liquidacion directa por modelo con `PayrollPeriod`, `PayrollConcept`, `PayrollDetail` y `Payroll`.
- Motivo: representar un flujo mensual con historial, borradores y confirmacion.
- Consecuencia: `Payroll` guarda `baseSalarySnapshot`; el total sale de sueldo snapshot + additions - deductions.
- Alternativa descartada: calcular directamente desde sueldo actual + `Variation`.

### `Variation` reemplazado

- Fecha aproximada: agosto 2026.
- Decision: eliminar `Variation` y usar conceptos reutilizables mas detalles por liquidacion.
- Motivo: separar catalogo de conceptos de la aplicacion concreta en cada payroll.
- Consecuencia: no crear nuevas referencias a `Variation`.

### WorkSchedule y WorkRequest separados

- Fecha aproximada: agosto 2026.
- Decision: separar cronogramas asignados (`WorkSchedule`) de solicitudes puntuales (`WorkRequest`).
- Motivo: distinguir lo que la empresa asigna de lo que el empleado solicita modificar o justificar.
- Consecuencia: al aprobar una solicitud se registra revisor y se genera/ajusta el cronograma.

### Endpoints `/me` por empleado vinculado

- Fecha aproximada: agosto 2026.
- Decision: permitir experiencia self-service a cualquier cuenta autenticada que tenga empleado vinculado.
- Motivo: `STAFF` y `ADMIN` tambien pueden ser empleados si fueron creados por el flujo normal y luego promovidos.
- Consecuencia: no bloquear `/me` solo por tener rol administrativo; validar ownership en services.

### Limpieza demo opt-in

- Fecha aproximada: agosto 2026.
- Decision: agregar scheduler configurable para limpiar datos demo cuando `hiverh.demo-cleanup.enabled=true`.
- Motivo: mantener ambientes publicos de prueba controlados.
- Consecuencia: preservar `admin` por configuracion y revisar tablas antes de cambiar schema.

### Tests versionados

- Fecha aproximada: agosto 2026.
- Decision: los tests bajo `src/test/java` deben versionarse.
- Motivo: documentan reglas de negocio y evitan regresiones.
- Consecuencia: ignorar `target/`, no los tests.
