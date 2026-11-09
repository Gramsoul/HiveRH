# Frontend MVP 1.0

Ultima actualizacion: 2026-10-29.

## Proposito y fuente

Este documento define el alcance de la primera version del frontend de HiveRH y sirve como contexto operativo para planificacion, desarrollo y carga del backlog en Jira.

La fuente funcional es el trabajo practico **"Unidad 2 - TP 2 - Gallego, Molina, Herrera"**, disponible en:

<https://docs.google.com/document/d/1qK63m3kMSnkeTgxGtGXiOOWy4_e6o4tr/edit>

Este corte aplica al **frontend y a la presentacion del MVP 1.0**. No elimina modulos ya implementados en el backend ni modifica por si solo sus contratos.

## Objetivo del MVP

Para el cierre del MVP de la cursada, reducir al menos un 30% el tiempo promedio necesario para registrar un empleado y resolver una solicitud de vacaciones o licencia, comparando un proceso manual simulado con HiveRH en ambiente demo. La validacion debe usar al menos cinco altas de empleados y cinco solicitudes de ausencia, sin errores bloqueantes en los flujos obligatorios.

## Actores y recorridos principales

- `ADMIN`: inicia sesion y mantiene la estructura organizacional minima: sucursales, departamentos y puestos.
- `STAFF`: administra empleados, horarios y la bandeja de vacaciones/licencias pendientes.
- `EMPLOYEE`: consulta su perfil y horario, solicita vacaciones o licencias, adjunta certificados PDF y consulta el estado de sus solicitudes.

Una cuenta `ADMIN` o `STAFF` con empleado vinculado conserva el acceso a sus recursos `/me` cuando la regla del backend lo permite.

## Alcance incluido

### MUST HAVE

- Login con access JWT corto, refresh rotativo en cookie `HttpOnly`, cierre/restauracion de sesion y navegacion protegida por rol.
- Alta, consulta, actualizacion y baja logica de empleados.
- Solicitud y consulta de estado de vacaciones por parte de `EMPLOYEE`.
- Solicitud y consulta de estado de licencias por parte de `EMPLOYEE`.
- Carga y visualizacion de certificados PDF asociados a licencias.
- Bandeja para que `STAFF` o `ADMIN` consulte, filtre, apruebe o rechace vacaciones y licencias.
- Carga y consulta de horarios laborales; `EMPLOYEE` solo consulta los propios.
- Manejo visible de estados de carga, vacio, validacion, error, `401` y `403`.

### SHOULD HAVE

- ABM logico de sucursales, departamentos y puestos para `ADMIN`.
- Filtros operativos y paginacion para empleados, vacaciones, licencias y horarios.
- Foto de perfil, aprovechando la capacidad agregada en el backend durante el sprint de cierre. Su inclusion en la demo final queda pendiente de confirmar y no debe bloquear los flujos MUST.
- Consulta de la cuenta autenticada y autogestion de email/password.

## Fuera del Frontend MVP 1.0

- Payroll y liquidacion legal argentina, aun cuando el backend conserve un modelo academico simplificado.
- `WorkRequest` y cambios puntuales de jornada.
- Administracion avanzada de cuentas y registro de cuentas sin empleado.
- Denuncias y suspensiones. Estos modulos fueron removidos del proyecto y no deben reintroducirse sin confirmacion explicita.
- Integraciones con ARCA, bancos, biometricos o geolocalizacion.
- Aplicacion movil nativa, analitica avanzada, tableros ejecutivos, exportaciones masivas, onboarding documental, evaluacion de desempeno, motor dinamico de permisos e inteligencia artificial.

## Requisitos no funcionales

- Rendimiento: cada operacion principal debe responder en menos de dos segundos en la prueba demo/local de cinco altas y cinco solicitudes, sin errores HTTP `5xx`.
- Seguridad: las rutas protegidas rechazan solicitudes sin token con `401`; las acciones no autorizadas por rol devuelven `403`; el frontend oculta acciones no permitidas, pero la autorizacion definitiva sigue en el backend.
- Mantenibilidad: separacion del frontend por features, modelos tipados para los contratos HTTP y al menos una verificacion documentada por historia MUST.
- Usabilidad: todos los formularios muestran validaciones accionables y evitan envios duplicados mientras una operacion esta en curso.
- Demo: debe existir un recorrido reproducible por rol y datos de prueba conocidos que no dependan de editar la base manualmente durante la presentacion.

## Contratos y brechas detectadas

La inspeccion original partio de `dev` y el contrato de autenticacion fue actualizado en `C-Feature/cookies-refresh-token` el 2026-09-15.

- Login y refresh devuelven `accessToken`, `tokenType`, `expiresInSeconds`, `identifier`, `roles` y `mustChangePassword`. El frontend no necesita decodificar el JWT.
- `identifier` representa el username real de la cuenta. `GET /api/accounts/me` devuelve `email`, `user` y `rol`, incluso cuando la cuenta no tiene empleado vinculado.
- El refresh token se entrega solamente mediante la cookie `hiverh_refresh`, con `HttpOnly`, `Path=/api/auth` y politica `Secure`/`SameSite` configurable por ambiente.
- `GET /api/auth/csrf` entrega un token CSRF XOR enmascarado requerido por `POST /api/auth/login`, `/refresh` y `/logout`. Su valor difiere intencionalmente de la cookie base `XSRF-TOKEN`.
- `GET /api/employees/me` permite obtener el perfil del empleado vinculado.
- La foto de perfil usa `PATCH /api/employees/picture` con `multipart/form-data` (`dni` y `file`) y `GET /api/employees/picture/{dni}` devuelve `image/png`.
- `GET /api/work-schedules/me` cubre la consulta del horario propio.
- `GET /api/vacations/me` permite listar en forma paginada las vacaciones del empleado vinculado y filtrar por estado o rango de fechas. `GET /api/vacations` sigue restringido a `ADMIN` y `STAFF`.
- **Bloqueante para US-04:** no existe un endpoint de listado de licencias propias; un empleado solo puede consultar `GET /api/licenses/{id}` si ya conoce el ID.
- Vacaciones se revisan mediante un `PUT` general; antes de integrar la bandeja hay que confirmar el payload exacto para aprobar/rechazar y evitar que el frontend envie campos que no corresponden.
- Certificados mezclan rutas plurales y singulares: `POST /api/certificates`, `GET/DELETE /api/certificate/{id}` y `GET /api/certificate-info?id=...`.
- CORS tiene allowlist exacta mediante `FRONTEND_ORIGINS` y credenciales habilitadas. Falta el smoke test con el origen real de desarrollo/despliegue.
- La autorizacion de carga de foto necesita validacion funcional: el matcher amplio de `PATCH /api/employees/**` aparece antes que el matcher especifico de la foto.

## Contrato de autenticacion para el frontend

### Respuesta de login y refresh

```json
{
  "accessToken": "eyJ...",
  "tokenType": "Bearer",
  "expiresInSeconds": 600,
  "identifier": "40111222",
  "roles": ["ROLE_EMPLOYEE"],
  "mustChangePassword": true
}
```

- Guardar `accessToken` solo en memoria del servicio/store de autenticacion. No usar `localStorage`, `sessionStorage`, IndexedDB ni cookies creadas por JavaScript.
- No intentar leer ni copiar `hiverh_refresh`: es `HttpOnly` y el navegador la envia automaticamente solo a `/api/auth` cuando la llamada usa credenciales.
- Usar `roles` para guards, menus y experiencia visual. La autorizacion real siempre la decide el backend.
- Usar `mustChangePassword` para redirigir el primer acceso al cambio de password. Al completarlo se revocan los refresh tokens: limpiar el access token y pedir login nuevamente.

### Secuencia obligatoria

1. Al iniciar la app, ejecutar `GET /api/auth/csrf` con `withCredentials: true` y conservar `response.token` en memoria.
2. Si no hay sesion en memoria, ejecutar una vez `POST /api/auth/refresh` con `withCredentials: true` y `X-XSRF-TOKEN`. Si responde `200`, restaurar el estado con su JSON; si responde `401`, mostrar login.
3. Para login, enviar credenciales a `POST /api/auth/login` con `withCredentials: true` y `X-XSRF-TOKEN`.
4. Un interceptor agrega `Authorization: Bearer <accessToken>` a los endpoints protegidos. No agregar bearer a `/api/auth/csrf`, `/login`, `/refresh` o `/logout`.
5. Ante el primer `401` de una request protegida, compartir una unica operacion de refresh entre requests concurrentes, actualizar el token y reintentar cada request una sola vez.
6. Si refresh falla, limpiar el estado en memoria y volver a login. Un `403` significa falta de permisos y no debe disparar refresh.
7. Logout llama `POST /api/auth/logout` con CSRF y credenciales, y limpia siempre el estado local aunque falle la red.

El token CSRF se toma del cuerpo de `GET /api/auth/csrf` y se envia sin modificar. Es una representacion XOR enmascarada: no debe compararse con la cookie `XSRF-TOKEN`, leerse desde ella ni desenmascararse en el frontend. Spring realiza el desenmascarado al validar `X-XSRF-TOKEN`. No se debe depender del interceptor XSRF automatico del framework porque frontend y API pueden tener origenes diferentes.

### Ambientes y cookies

- Local HTTP: `REFRESH_COOKIE_SECURE=false`, `REFRESH_COOKIE_SAME_SITE=Strict`, `FRONTEND_ORIGINS=http://localhost:4200`.
- Produccion en el mismo sitio: HTTPS, `REFRESH_COOKIE_SECURE=true` y preferentemente `SameSite=Strict`.
- Produccion en sitios distintos: HTTPS, `REFRESH_COOKIE_SECURE=true`, `REFRESH_COOKIE_SAME_SITE=None` y el origen frontend exacto en `FRONTEND_ORIGINS`.
- No usar `Access-Control-Allow-Origin: *` cuando se envian credenciales.

## Pendiente de confirmar

- Framework, libreria visual, estrategia de estado, URL de despliegue y repositorio del frontend.
- Dominio final de frontend/API para definir la politica `SameSite` de produccion.
- Si payroll simplificado queda completamente fuera de la interfaz o se mostrara una consulta minima.
- Si `ADMIN` necesita una pantalla de cuentas y roles o si alcanza con los usuarios ya preparados para la demo.
- Si la foto de perfil forma parte de la evaluacion del MVP o queda como mejora SHOULD.
- Prioridad MoSCoW de horarios laborales. El documento los incluye, pero no les asigna una historia propia; se propone `US-10` como MUST para cerrar la trazabilidad.
- La historia de denuncias del documento academico contradice la decision vigente del repositorio. No debe implementarse hasta que el equipo confirme la correccion del documento.

## Estructura recomendada de Jira

### Configuracion del proyecto

- Tipo de proyecto: Scrum.
- Jerarquia: `Epic -> Story -> Sub-task`. Usar `Task` para trabajo tecnico independiente, `Spike` para investigacion y `Bug` solo para defectos observables.
- Version: `MVP 1.0` como `Fix Version` de todos los issues incluidos.
- Componentes: `Frontend Core`, `Auth`, `Employees`, `Organization`, `Vacations`, `Licenses`, `Certificates`, `Work Schedules`, `Backend Contract`, `QA/Demo`.
- Etiquetas MoSCoW: `must`, `should`, `could`, `wont`.
- Flujo sugerido: `Backlog -> Selected for Development -> In Progress -> Code Review -> QA -> Done`. Usar flag de bloqueado en lugar de crear un estado permanente `Blocked`.
- Campos minimos por historia: actor, valor, criterios BDD, prioridad MoSCoW, Story Points, componente, dependencia backend, evidencia y enlace a PR/commit.

### Epicas e issues propuestos

Las estimaciones son iniciales. El equipo debe refinarlas en Planning Poker y ajustar el alcance a su velocidad real.

#### EPIC-FE-01 - Fundaciones del frontend

- `TASK-FE-01` - Inicializar el proyecto frontend, entornos, lint, formato y estructura por features. `3 SP`, MUST.
- `TASK-FE-02` - Crear cliente HTTP, configuracion de base URL, store de access token en memoria, interceptor bearer/refresh y manejo comun de errores. `5 SP`, MUST.
- `TASK-FE-03` - Implementar layout, navegacion responsive y componentes reutilizables de carga, vacio, confirmacion y error. `5 SP`, MUST.
- `TASK-FE-04` - Ejecutar smoke test frontend-backend y resolver configuracion CORS si corresponde. `3 SP`, MUST.
- `TASK-FE-05` - Definir convenciones de ramas, commits, PR, revision y evidencia para Jira. `2 SP`, MUST.

#### EPIC-FE-02 - Autenticacion y acceso por rol

- `US-01` - Como usuario, quiero iniciar y cerrar sesion para acceder solo a las funciones de mi perfil. `8 SP`, MUST.
  - Integrar CSRF, `POST /api/auth/login`, `/refresh` y `/logout` con credenciales.
  - Mantener el access token solo en memoria y restaurar la sesion mediante refresh al recargar.
  - Resolver navegacion desde `roles` de la respuesta, sin decodificar el JWT.
  - Implementar guardas y menu por `ADMIN`, `STAFF` y `EMPLOYEE`.
  - Manejar expiracion, concurrencia de refresh, reintento unico, `401`, `403` y logout.
- `US-FE-01` - Como usuario con empleado vinculado, quiero consultar mi perfil para verificar mis datos. `3 SP`, MUST.
  - Integrar `GET /api/employees/me`.
  - Mostrar datos personales y asignacion activa.
- `US-FE-01B` - Como usuario autenticado, quiero consultar mi cuenta y cambiar mi email o password. `3 SP`, SHOULD.
  - Integrar `GET /api/accounts/me`, `PATCH /api/accounts/me/email` y `PATCH /api/accounts/me/password`.
  - La cuenta existe aunque el usuario no tenga empleado asociado.
  - Los cambios de email/password revocan refresh tokens; cerrar la sesion o solicitar una nueva autenticacion al finalizar.

#### EPIC-FE-03 - Gestion de empleados

- `US-02A` - Como Staff, quiero listar, filtrar y ver el detalle de empleados para localizar su informacion. `5 SP`, MUST.
- `US-02B` - Como Staff, quiero registrar un empleado para incorporarlo al sistema con su cuenta vinculada. `8 SP`, MUST.
- `US-02C` - Como Staff, quiero editar los datos personales y laborales de un empleado para mantenerlos actualizados. `5 SP`, MUST.
- `US-02D` - Como Staff, quiero dar de baja logicamente un empleado para conservar su historial. `3 SP`, MUST.
- `US-FE-02` - Como usuario autorizado, quiero cargar y visualizar una foto de perfil. `3 SP`, SHOULD, pendiente de confirmar.
  - Validar tipos/tamanos en interfaz sin reemplazar la validacion del backend.
  - Consumir la imagen como `Blob` y contemplar avatar por defecto.

#### EPIC-FE-04 - Estructura organizacional

- `US-06` - Como Admin, quiero gestionar sucursales, departamentos y puestos para ubicar correctamente a los empleados. `8 SP`, SHOULD.
  - Sub-task: listado, alta, edicion y baja logica de sucursales.
  - Sub-task: listado, alta, edicion y cambio de estado de departamentos.
  - Sub-task: listado, alta, edicion y cambio de estado de puestos.
  - Sub-task: selects dependientes y reutilizables en formularios de empleado.

#### EPIC-FE-05 - Vacaciones

- `US-03A` - Como Employee, quiero solicitar vacaciones para gestionar mi ausencia. `5 SP`, MUST.
  - Formulario con fechas y validacion de rango.
  - Confirmacion y visualizacion del estado creado.
  - Mensaje claro ante superposicion o validacion del backend.
- `US-03B` - Como Employee, quiero consultar el estado de mis vacaciones. `5 SP`, MUST. Contrato backend disponible en `GET /api/vacations/me`.
- `US-05A` - Como Staff, quiero listar, filtrar, aprobar o rechazar vacaciones pendientes. `8 SP`, MUST.
  - Confirmar contrato backend de revision antes de implementar.
  - Registrar comentario de revision cuando corresponda.

#### EPIC-FE-06 - Licencias y certificados

- `US-04A` - Como Employee, quiero solicitar una licencia para documentar mi ausencia. `5 SP`, MUST.
- `US-04B` - Como Employee, quiero adjuntar y visualizar un certificado PDF en una licencia. `5 SP`, MUST.
  - Implementar upload `multipart/form-data`.
  - Validar tipo de archivo y mostrar progreso/error.
  - Abrir el PDF autorizado sin descargar datos de otra persona.
- `US-04C` - Como Employee, quiero consultar el estado de mis licencias. `5 SP`, MUST, bloqueada por listado propio faltante.
- `US-05B` - Como Staff, quiero listar, filtrar, revisar y resolver licencias junto con sus certificados. `8 SP`, MUST.

#### EPIC-FE-07 - Horarios laborales

- `US-10A` - Como Staff, quiero listar, cargar, editar y cancelar horarios para organizar la jornada de los empleados. `8 SP`, MUST propuesto.
- `US-10B` - Como Employee, quiero consultar mi horario por rango de fechas. `5 SP`, MUST propuesto.

#### EPIC-FE-08 - Calidad, seguridad y demo

- `TASK-QA-01` - Documentar y ejecutar casos por rol para `401`, `403`, validaciones y ownership. `5 SP`, MUST.
- `TASK-QA-02` - Verificar al menos un caso feliz y uno de error por cada historia MUST. `5 SP`, MUST.
- `TASK-QA-03` - Preparar datos y guion reproducible de demostracion por actor. `3 SP`, MUST.
- `TASK-QA-04` - Medir el objetivo SMART con cinco altas y cinco solicitudes, registrar tiempos y ausencia de `5xx`. `3 SP`, MUST.
- `TASK-QA-05` - Ejecutar regresion final, corregir bloqueantes y etiquetar la version `MVP 1.0`. `5 SP`, MUST.

### Issues de contrato backend necesarios

- `BE-MVP-01` - Completado: consulta paginada de vacaciones propias en `GET /api/vacations/me`.
- `BE-MVP-02` - Agregar consulta paginada de licencias propias, por ejemplo `GET /api/licenses/me`.
- `BE-MVP-03` - Confirmar o separar el contrato de aprobar/rechazar vacaciones.
- `BE-MVP-04` - Ejecutar smoke test de CORS/cookies con el origen real y cerrar variables de produccion. La allowlist backend ya esta implementada.
- `BE-MVP-05` - Revisar autorizacion y contrato de foto de perfil según el actor que pueda modificarla.

Estos issues deben enlazarse con relacion `blocks/is blocked by` a las historias frontend correspondientes.

## Criterios de aceptacion base

Ejemplo para cada historia:

```text
DADO un usuario autenticado con el rol y los datos requeridos
CUANDO completa la accion desde la interfaz
ENTONCES el frontend envia el contrato esperado, muestra el resultado actualizado
Y no expone acciones ni datos de otros usuarios
Y presenta de forma accionable los errores de validacion, autenticacion o autorizacion.
```

Los criterios deben especializarse por historia; no alcanza con copiar este texto sin agregar reglas del dominio.

## Definition of Ready

Una historia entra a sprint cuando:

- actor, valor y resultado esperado estan claros;
- tiene criterios BDD verificables;
- el contrato backend fue inspeccionado o la dependencia esta explicitamente enlazada;
- existen diseno o boceto suficientes para construirla;
- el equipo la estimo y puede completarla dentro de un sprint;
- no contiene decisiones funcionales importantes sin responsable.

## Definition of Done

Una historia queda `Done` cuando:

- cumple todos los criterios de aceptacion;
- paso revision por al menos otro integrante;
- tiene manejo de carga, vacio, error y permisos cuando aplica;
- incluye pruebas o evidencia reproducible;
- no introduce errores bloqueantes ni `5xx` en su flujo;
- Jira enlaza PR/commit, evidencia y dependencias resueltas;
- la documentacion afectada fue actualizada.

## Sprints sugeridos

- **Sprint 1 - Backend finalizado:** registrar solo trabajo real y verificable ya completado, incluyendo foto de perfil y correcciones asociadas. Marcarlo `Done` con enlaces a commits o PR; no inventar fechas ni esfuerzo retroactivo.
- **Sprint 2 - Primer corte vertical:** fundaciones minimas, login por rol, perfil propio, catalogos de solo lectura y listado/alta de empleados.
- **Sprint 3 - Empleados y vacaciones:** completar ABM logico de empleados, estructura organizacional necesaria y circuito completo de vacaciones.
- **Sprint 4 - Licencias, certificados y horarios:** completar ambos recorridos por `EMPLOYEE` y `STAFF`.
- **Sprint 5 - Calidad y demo:** seguridad, regresion, medicion SMART, correccion de bloqueantes y presentacion.

La cantidad de sprints debe ajustarse al calendario real. Si hay menos tiempo, se recorta primero foto de perfil, filtros avanzados y mantenimiento completo de catalogos; no se recortan los recorridos end-to-end de login, empleados, vacaciones y licencias.

## Evidencia y trazabilidad para la cursada

- Vincular cada historia con su epica, requisito (`FR`/`NFR`) y prioridad MoSCoW.
- Adjuntar captura o video corto del criterio cumplido y enlazar PR/commit.
- Registrar Sprint Goal, capacidad, compromiso inicial, trabajo terminado y arrastre.
- Usar burndown/velocity solo con estimaciones reales del equipo.
- Cerrar cada sprint con Review y Retrospective documentadas en Jira o Confluence.
- Mantener visibles las decisiones `Won't Have`; no borrar issues para ocultar recortes de alcance.
- Para el sprint backend ya cerrado, reconstruir tickets solo desde evidencia real y sin falsear historial.
