# Informe HiveRH

Este informe describe el recorrido completo del sistema HiveRH, desde la autenticación inicial hasta la operación de los módulos principales.

La idea es que sirva como guía de defensa, documentación de endpoints y orden recomendado para completar una colección de Postman.

El proyecto expone una API RESTful orientada a la gestión de recursos humanos. A través de esta API se pueden administrar cuentas, empleados, estructura organizacional, cronogramas laborales, solicitudes de jornada, licencias, certificados, vacaciones, períodos, conceptos y liquidaciones de sueldo.

---

## Descripción general del proyecto

### Arquitectura

El proyecto utiliza una arquitectura package by feature. Esto significa que cada módulo agrupa sus propias clases relacionadas, como Controller, Service, Repository, Entity, DTO y Mapper cuando corresponde.

### Seguridad

La seguridad está implementada con Spring Security y JWT. El sistema utiliza un filtro OncePerRequestFilter para validar el token en cada request protegida. Además, se manejan roles y autorización por endpoint o por método.

### Persistencia

La persistencia se trabaja con Spring Data JPA, utilizando entidades relacionales y una base de datos SQL.

### DTOs

Se utilizan DTOs de request y response para evitar exponer directamente las entidades en la mayoría de los módulos. Esto permite separar la estructura interna del sistema de los datos que se reciben o devuelven por la API.

### Excepciones

El sistema cuenta con un manejo centralizado de excepciones mediante GlobalExceptionHandler. También se utilizan excepciones propias, como EntityNotFoundException, para representar errores específicos del dominio.

### Documentación

La API puede probarse desde Swagger/OpenAPI y también desde Postman. Esto facilita la validación de endpoints, permisos, requests y responses.

---

## Seguridad, roles y autenticación

El sistema trabaja con autenticación stateless. Primero se realiza el login, luego se obtiene un token JWT y finalmente ese token se envía en el header Authorization para acceder a los endpoints protegidos.

Las cuentas implementan UserDetails, por lo que Spring Security puede obtener el username, password, authorities y el estado de la cuenta.

### Roles del sistema

#### ADMIN

Es el rol con mayor nivel de permisos dentro del sistema.

Puede crear sucursales, departamentos, puestos, períodos y conceptos de liquidación, empleados, modificar roles y acceder a recursos administrativos.

#### STAFF

Es el rol operativo del área de Recursos Humanos.

Puede gestionar empleados, cronogramas laborales, solicitudes de jornada, licencias, vacaciones, liquidaciones y consultar información sensible según la configuración de seguridad del sistema.

#### EMPLOYEE

Es el rol de empleado común.

Puede autenticarse, ver su propio perfil y acceder a recursos propios cuando el SecurityAuthorizationService lo permite.

### JWT

El login genera un token JWT.

Luego, cada request protegida debe enviar el token en el header Authorization con el siguiente formato:

```http
Authorization: Bearer
```

### @PreAuthorize

Se utiliza para validar el acceso a determinados recursos, como empleados, licencias y certificados, según el usuario autenticado y sus permisos.

### anyRequest().authenticated()

Los endpoints que no tienen una regla específica igualmente requieren un token válido.

Los módulos operativos separan permisos por rol: ADMIN y STAFF gestionan recursos administrativos, mientras que EMPLOYEE solo accede a recursos propios cuando corresponde.

---

## Detalle por módulo y comportamiento esperado

### Auth

Permite iniciar sesión y registrar cuentas dentro del sistema.

El login recibe las credenciales del usuario y, si son correctas, devuelve un token JWT para poder acceder a los endpoints protegidos.

El registro de cuentas requiere que el usuario tenga un rol permitido y guarda la contraseña encriptada para mantener la seguridad.

### Account

Permite al usuario autenticado modificar los datos propios de su cuenta.

Cada usuario puede cambiar su email y su contraseña.

Además, un usuario con rol ADMIN puede cambiar los roles de otras cuentas del sistema.

### Branch

Administra las sucursales de la empresa.

Permite crear, consultar, actualizar y eliminar sucursales.

La eliminación no borra el registro de forma definitiva, sino que realiza una eliminación lógica marcando la sucursal como inactiva.

### Department

Administra los departamentos de la empresa.

Permite realizar filtros por ID, nombre y estado activo.

La eliminación también es lógica, por lo tanto el departamento queda marcado como inactivo.

### Position

Administra los puestos de trabajo.

Permite filtrar los puestos por departamento, nombre y estado activo.

Al eliminar un puesto, no se borra físicamente de la base de datos, sino que se marca como inactivo.

### Employee

Gestiona los empleados de la empresa.

Al crear un empleado, el sistema exige que se indique una sucursal, un puesto y un departamento.

Esos datos no se guardan como relación directa del empleado, sino como su primera asignación laboral activa.

Además, al registrar un nuevo empleado se genera automáticamente una cuenta con rol EMPLOYEE por defecto.

### WorkSchedule

Representa el cronograma laboral asignado a un empleado para una fecha concreta.

Permite registrar días laborales, días libres, feriados y horas extra. Los cronogramas son gestionados por ADMIN o STAFF y el empleado solo puede consultar sus propios cronogramas activos.

El sistema evita que un empleado tenga dos cronogramas activos superpuestos en la misma fecha y rango horario. Cuando un cronograma deja de aplicar no se borra físicamente, sino que se marca como CANCELLED.

### WorkRequest

Representa solicitudes puntuales del empleado relacionadas con su jornada laboral.

Permite pedir un día libre, solicitar cambio de turno, avisar entrada tarde, pedir salida anticipada, solicitar horas extra o pedir un día compensatorio.

Las solicitudes nacen PENDING y quedan sujetas a revisión por ADMIN o STAFF. Al aprobar o rechazar se registra el usuario administrativo que revisó la solicitud y un comentario opcional. Si la solicitud se aprueba, el sistema genera o modifica el WorkSchedule correspondiente.

El sistema evita que un empleado tenga dos solicitudes PENDING del mismo tipo para la misma fecha objetivo.

### PayrollPeriod

Representa un período mensual de liquidación, identificado por mes y año.

El período nace OPEN y puede cerrarse cuando no quedan liquidaciones en estado DRAFT.

### PayrollConcept

Define conceptos reutilizables de liquidación, como bonos, horas extra o descuentos por adelanto.

Cada concepto indica si suma al sueldo mediante ADDITION o si descuenta mediante DEDUCTION.

### PayrollDetail

Registra cuánto se aplicó de un concepto en una liquidación específica.

El detalle guarda importe y descripción opcional, y es la base para calcular sumas y descuentos.

### Payroll

Genera liquidaciones mensuales de sueldo por empleado y período.

El sistema guarda baseSalarySnapshot y calcula el total como baseSalarySnapshot + totalAdditions - totalDeductions.

Las liquidaciones nacen DRAFT, pueden actualizarse mientras el período esté abierto y luego se confirman o anulan.

### Vacation

Gestiona las vacaciones de los empleados.

Valida que las fechas ingresadas sean correctas, que el empleado esté activo y que no exista superposición con otros períodos de vacaciones ya registrados.

### License

Gestiona las licencias de los empleados.

Permite asociar certificados a una licencia y también permite realizar actualizaciones parciales mediante PATCH, modificando solo los campos enviados en la solicitud.

### Certificate

Gestiona los certificados PDF asociados a las licencias.

Utiliza multipart/form-data para permitir la carga de archivos desde el cliente hacia el sistema y registra la fecha de carga del certificado.

## Preparación inicial

- Levantar la base de datos MySQL y configurar las variables DB_URL, DB_USER, DB_PASSWORD, EMAIL_ADDRESS, EMAIL_PASSWORD, SECRET y EXPIRATION.
- Ejecutar la aplicación Spring Boot.
- Tener al menos una cuenta ADMIN inicial. Como el endpoint /api/auth/register está protegido, el primer ADMIN debe existir previamente por seed, carga manual o base ya preparada.
- En Postman, crear una variable token y enviar Authorization: Bearer {{token}} en todos los endpoints protegidos.

---

## 1. Autenticación y cuentas

- El usuario se autentica con POST /api/auth/login enviando identifier y password.
- El identifier puede ser usuario o email porque la búsqueda se realiza por user o email.
- Si las credenciales son correctas, la API devuelve un token JWT que incluye el rol como authority ROLE_ADMIN, ROLE_STAFF o ROLE_EMPLOYEE.
- Con el token activo se puede registrar una cuenta, cambiar email propio, cambiar contraseña propia o, si se es ADMIN, cambiar roles.

---

## 2. Configuración base de la empresa

- Primero se cargan las sucursales con /api/branches.
- Después se cargan los departamentos con /api/departments.
- Luego se cargan los puestos con /api/positions.
- Estos tres módulos son la base para crear empleados porque el alta de empleado exige id_branch, id_position e id_department.

---

## 3. Alta de empleado

- Con sucursal, puesto y departamento ya existentes, se crea el empleado mediante POST /api/employees.
- El empleado nace con estado ACTIVE.
- Se crea una asignación laboral activa con sucursal, puesto y departamento. Su startDate inicial toma la fecha de contratación del empleado.
- El sistema crea automáticamente una cuenta por defecto para ese empleado: usuario igual al DNI, email {dni}@hiverh.local y contraseña inicial igual al DNI.
- La respuesta del empleado incluye sus datos personales, estado, cuenta asociada y asignaciones laborales con sucursal, puesto, departamento, startDate, endDate y active.

---

## 4. Gestión de empleados

- STAFF o ADMIN pueden listar empleados y filtrarlos por nombre, DNI, sucursal, fecha de ingreso, estado, puesto, departamento o rango salarial.
- PATCH permite actualizar datos puntuales sin enviar todo el objeto.
- PUT actualiza el empleado completo y recibe sucursal, puesto y departamento para mantener la asignación laboral actual.
- Si cambia sucursal, puesto o departamento, el sistema cierra la asignación activa con endDate y crea una nueva asignación activa.
- DELETE no borra físicamente al empleado: cambia su estado a TERMINATED y cierra sus asignaciones activas.
- El empleado autenticado puede consultar su propio perfil con GET /api/employees/me.

---

## 5. Jornada laboral

- ADMIN o STAFF crean cronogramas laborales mediante /api/work-schedules indicando el DNI del empleado, fecha, tipo y horario cuando corresponde.
- El tipo WORKDAY o EXTRA_HOURS requiere startTime y endTime; DAY_OFF y HOLIDAY representan bloques de día completo sin rango horario.
- El empleado autenticado consulta únicamente sus cronogramas activos desde /api/work-schedules/me.
- El sistema valida que no existan cronogramas activos superpuestos para el mismo empleado, fecha y rango horario.
- El empleado crea solicitudes puntuales desde /api/work-requests/me. Estas solicitudes no reemplazan vacaciones ni licencias largas, sino pedidos diarios o de horario.
- Las solicitudes nacen PENDING y el empleado solo puede cancelarlas mientras sigan en ese estado.
- ADMIN o STAFF revisan solicitudes desde /api/work-requests y pueden aprobarlas o rechazarlas.
- Al aprobar una solicitud, el sistema registra reviewed_by_account_id, reviewComment y genera o ajusta el cronograma laboral asociado.

---

## 6. Conceptos y períodos de liquidación

- Antes de liquidar sueldos, ADMIN o STAFF crean un período mensual con /api/payroll-periods.
- El período representa un mes y año concreto, por ejemplo 8/2026, y nace en estado OPEN.
- También se cargan conceptos reutilizables con /api/payroll-concepts.
- Un concepto ADDITION suma al sueldo y un concepto DEDUCTION descuenta del sueldo.
- Los conceptos se desactivan de forma lógica para conservar el historial de liquidaciones ya generadas.

---

## 7. Payroll / liquidación de sueldo

- Para crear una liquidación se llama a POST /api/payrolls con dniEmployee, periodId y una lista opcional de detalles.
- El sistema busca el empleado, valida que esté ACTIVE, que tenga sueldo base válido y que el período esté abierto.
- También valida que ese empleado no tenga otra liquidación activa para el mismo período.
- La liquidación nace DRAFT y guarda baseSalarySnapshot con el sueldo base del momento.
- Cada detalle referencia un PayrollConcept y un importe positivo.
- El total final se calcula como baseSalarySnapshot + totalAdditions - totalDeductions.
- Solo se pueden modificar liquidaciones DRAFT.
- ADMIN y STAFF pueden confirmar o anular liquidaciones. El empleado solo puede consultar sus propias liquidaciones CONFIRMED desde /api/payrolls/me.

---

## 8. Vacaciones

- Las vacaciones se registran con /api/vacations y se asocian a un empleado por DNI.
- El sistema valida empleado activo, fechas obligatorias, fecha final posterior a inicio, que la solicitud no sea posterior al inicio y que exista una anticipación mínima de 5 días hábiles.
- También evita vacaciones superpuestas para el mismo empleado.
- Permite listar por estado, rango de fechas, DNI del empleado y nombre completo.

---

## 9. Licencias y certificados

- Las licencias se registran con /api/licenses y representan ausencias justificadas, por ejemplo licencia médica.
- Las licencias nuevas se crean para el empleado autenticado, quedan inicialmente en estado PENDING y luego STAFF o ADMIN pueden revisarlas con estado APPROVED, REJECTED o CANCELLED.
- Se puede crear la licencia y luego adjuntar uno o más certificados PDF con /api/certificates usando multipart/form-data.
- Un ADMIN o STAFF puede listar todas las licencias; un empleado puede acceder a sus propias licencias/certificados según las reglas de autorización.
- El certificado se guarda como bytes, registra su fecha de carga y puede consultarse como PDF o como información resumida.

---

## Paginación

Los endpoints paginados reciben los parámetros estándar de Spring Pageable:

```http
page=0
size=10
sort=requestDate,desc
```

La página inicial es 0. El parámetro sort es opcional y permite ordenar sin cambiar los filtros.

Endpoints paginados actuales:

- GET /api/employees
- GET /api/work-schedules
- GET /api/work-requests
- GET /api/licenses
- GET /api/payrolls
- GET /api/vacations

---

# Informe de endpoints

## Auth

### POST /api/auth/login

Permite iniciar sesión en el sistema enviando identifier y password.

El identifier puede corresponder al usuario o al email de la cuenta.

Si las credenciales son correctas, el sistema devuelve un token JWT que luego se utiliza para acceder a los endpoints protegidos.

### POST /api/auth/register

Permite registrar nuevas cuentas dentro del sistema.

Este endpoint está disponible únicamente para usuarios con rol ADMIN o STAFF.

Al registrar una cuenta, la contraseña se guarda encriptada por seguridad.

---

## Account

### PATCH /api/accounts/me/email

Permite que el usuario autenticado cambie su propio email.

Solo modifica el email de la cuenta que está usando el token actual.

### PATCH /api/accounts/me/password

Permite que el usuario autenticado cambie su propia contraseña.

La nueva contraseña se guarda encriptada.

### PATCH /api/accounts/{identifier}/rol

Permite que un usuario con rol ADMIN cambie el rol de otra cuenta.

Se utiliza para modificar permisos de acceso dentro del sistema. El identifier puede ser usuario, email o DNI cuando la cuenta automática del empleado usa el DNI como usuario.

---

## Branch

### GET /api/branches

Lista las sucursales activas registradas en el sistema.

### POST /api/branches

Crea una nueva sucursal.

### PUT /api/branches/{id_branch}

Actualiza los datos de una sucursal existente.

### DELETE /api/branches/{id_branch}

Realiza una baja lógica de la sucursal.

No elimina el registro de la base de datos, sino que la marca como inactiva.

---

## Department

### GET /api/departments

Lista los departamentos registrados en el sistema.

Permite aplicar filtros por ID, nombre y estado activo.

### POST /api/departments

Crea un nuevo departamento.

### PUT /api/departments/{id_department}

Actualiza los datos principales de un departamento.

### PATCH /api/departments/{id_department}/status

Activa o desactiva el departamento mediante alta o baja lógica.

El registro no se elimina físicamente, sino que queda marcado como inactivo.

---

## Position

### GET /api/positions

Lista los puestos de trabajo registrados en el sistema.

Permite filtrar por departamento, nombre y estado activo.

### POST /api/positions

Crea un nuevo puesto de trabajo.

### PUT /api/positions/{id}

Actualiza los datos principales de un puesto de trabajo.

### PATCH /api/positions/{id}/status

Activa o desactiva el puesto mediante alta o baja lógica.

El puesto no se borra definitivamente, sino que queda marcado como inactivo.

---

## Employee

### GET /api/employees

Lista los empleados registrados en el sistema en formato paginado.

Permite aplicar filtros según los parámetros disponibles.

### GET /api/employees/me

Devuelve el empleado asociado a la cuenta autenticada.

Sirve para que un usuario pueda consultar sus propios datos como empleado.

### GET /api/employees/{dni}

Consulta un empleado específico por su DNI.

### POST /api/employees

Crea un nuevo empleado en estado ACTIVE.

Al crear el empleado, también se genera automáticamente una cuenta con rol EMPLOYEE por defecto.

También crea la primera asignación laboral activa con sucursal, puesto y departamento.

### PATCH /api/employees/{dni}

Actualiza parcialmente los datos de un empleado.

Solo modifica los campos enviados en la solicitud.

Si se envía id_branch, id_position o id_department, se actualiza la asignación laboral activa conservando historial.

### PUT /api/employees/{dni}

Actualiza los datos del empleado.

Debe recibir la asignación laboral actual mediante id_branch, id_position e id_department.

### DELETE /api/employees/{dni}

Realiza una baja lógica del empleado.

El empleado no se elimina físicamente, sino que su estado cambia a TERMINATED y sus asignaciones activas pasan a inactivas.

---

## WorkSchedule

### GET /api/work-schedules/me

Devuelve los cronogramas activos del empleado autenticado.

Puede recibir from y to como filtros opcionales por rango de fechas.

### GET /api/work-schedules

Lista cronogramas laborales en formato paginado.

Permite filtrar por dniEmployee, departmentId, branchId, from, to, type y status.

### GET /api/work-schedules/{id}

Consulta un cronograma laboral específico por ID.

### POST /api/work-schedules

Crea un cronograma laboral activo para un empleado identificado por DNI.

Solo ADMIN o STAFF pueden crear cronogramas.

### PATCH /api/work-schedules/{id}

Actualiza parcialmente un cronograma activo.

El sistema vuelve a validar fechas, horarios y superposición antes de guardar.

### PATCH /api/work-schedules/{id}/cancel

Cancela un cronograma laboral sin borrarlo físicamente.

---

## WorkRequest

### POST /api/work-requests/me

Crea una solicitud de jornada para el empleado autenticado.

La solicitud nace en estado PENDING y no recibe DNI ni ID de empleado en el body.

### GET /api/work-requests/me

Lista las solicitudes de jornada del empleado autenticado.

Puede filtrar por from, to, requestType y status.

### GET /api/work-requests/me/{id}

Consulta una solicitud propia por ID.

Si la solicitud pertenece a otro empleado, el acceso se rechaza.

### PATCH /api/work-requests/me/{id}/cancel

Cancela una solicitud propia solo si todavía está PENDING.

### GET /api/work-requests

Lista solicitudes de jornada en formato paginado para ADMIN o STAFF.

Permite filtrar por dniEmployee, departmentId, branchId, from, to, requestType y status.

### GET /api/work-requests/{id}

Consulta una solicitud de jornada específica por ID.

### PATCH /api/work-requests/{id}/approve

Aprueba una solicitud PENDING, registra la cuenta revisora y genera o ajusta el cronograma laboral asociado.

### PATCH /api/work-requests/{id}/reject

Rechaza una solicitud PENDING y registra la cuenta revisora.

---

## PayrollPeriod

### GET /api/payroll-periods

Lista períodos de liquidación. Permite filtrar por mes, año y estado.

### GET /api/payroll-periods/{id}

Consulta un período de liquidación específico.

### POST /api/payroll-periods

Crea un período mensual en estado OPEN.

### PATCH /api/payroll-periods/{id}/close

Cierra un período OPEN si no tiene liquidaciones DRAFT.

---

## PayrollConcept

### GET /api/payroll-concepts

Lista conceptos de liquidación. Permite filtrar por nombre, tipo y estado activo.

### GET /api/payroll-concepts/{id}

Consulta un concepto de liquidación específico.

### POST /api/payroll-concepts

Crea un concepto reutilizable de tipo ADDITION o DEDUCTION.

### PATCH /api/payroll-concepts/{id}

Actualiza parcialmente un concepto.

### DELETE /api/payroll-concepts/{id}

Desactiva un concepto sin borrar detalles históricos.

---

## Payroll

### GET /api/payrolls/me

Devuelve las liquidaciones CONFIRMED del empleado autenticado.

Puede recibir year como filtro opcional.

### GET /api/payrolls/me/{id}

Devuelve el detalle de una liquidación propia si está CONFIRMED.

### GET /api/payrolls

Lista las liquidaciones de sueldo registradas en el sistema en formato paginado.

Permite filtrar por periodId, mes, año, estado y DNI del empleado.

### GET /api/payrolls/{id}

Consulta una liquidación por ID con sus detalles.

### POST /api/payrolls

Crea una liquidación DRAFT.

El sistema toma el sueldo base actual como baseSalarySnapshot y calcula totales desde los detalles.

### PATCH /api/payrolls/{id}

Actualiza una liquidación DRAFT.

### PATCH /api/payrolls/{id}/confirm

Confirma una liquidación DRAFT.

### PATCH /api/payrolls/{id}/cancel

Anula una liquidación mientras su período siga abierto.

---

## Vacation

### GET /api/vacations

Lista las vacaciones registradas en formato paginado.

Permite aplicar filtros por estado, rango de fechas, DNI del empleado y nombre completo.

### POST /api/vacations

Registra vacaciones para un empleado activo.

El sistema valida que las fechas sean correctas y que no haya superposición con otros períodos.

### PUT /api/vacations/{id_vacation}

Actualiza un registro de vacaciones existente.

### DELETE /api/vacations/{id_vacation}

Elimina el registro de vacaciones indicado.

---

## License

### GET /api/licenses

Lista las licencias registradas en el sistema en formato paginado.

Permite aplicar filtros por estado, DNI del empleado, rango de fechas y si es paga.

### GET /api/licenses/{id_license}

Consulta una licencia específica por su ID.

### POST /api/licenses

Crea una nueva licencia asociada al empleado autenticado. El empleado no indica id ni DNI en el body.

### PATCH /api/licenses/{id_license}

Permite a STAFF o ADMIN revisar una licencia, actualizando su estado, si es paga y el comentario de revisión.

Los estados posibles son PENDING, APPROVED, REJECTED y CANCELLED.

### DELETE /api/licenses/{id_license}

Elimina una licencia.

---

## Certificate

### POST /api/certificates

Carga un certificado PDF asociado a una licencia.

Este endpoint utiliza multipart/form-data para permitir el envío de archivos.

### GET /api/certificate/{id_certificate}

Descarga el archivo PDF almacenado correspondiente al certificado.

### GET /api/certificate-info?id={id}

Consulta la información del certificado sin descargar el archivo PDF. La respuesta incluye descripción, fecha de carga y licencia asociada.

### DELETE /api/certificate/{id_certificate}

Elimina el certificado indicado.

---

# Informe de requests para Postman

## Cuenta ADMIN inicial para pruebas

Antes de comenzar con las pruebas en Postman, se agregó manualmente una cuenta ADMIN en la base de datos.

Esto fue necesario porque el endpoint de registro de cuentas está protegido y requiere permisos previos.

Usuario: admin  
Email: admin@hiverh.local  
Password: 123  
Rol: ADMIN  
Estado: ACTIVE

La contraseña se encuentra cifrada con bcrypt. Aunque para iniciar sesión se utiliza 123, en la base de datos se guarda su versión encriptada.

SQL utilizado:

```sql
INSERT INTO account (
user,
password,
email,
rol,
status
) VALUES (
'admin',
'$2a$10$czl.qKI0ivobJHuvXyYtHuuC86AvTp4r52LszMK3UdCNQ85mXguF6',
'admin@hiverh.local',
'ADMIN',
'ACTIVE'
);
```

Con esta cuenta se obtiene el primer token JWT para poder probar los endpoints protegidos y crear las demás cuentas del sistema.

Todos los endpoints protegidos deben enviar el siguiente header:

```http
Authorization: Bearer {{token}}
```

También deben usar:

```http
Content-Type: application/json
```

La única excepción es el módulo Certificate, ya que para cargar archivos PDF se utiliza multipart/form-data.

---

## Login

Método: POST

Endpoint: /api/auth/login

Permite iniciar sesión en el sistema.

Se envía el identifier y la contraseña.

Si los datos son correctos, la API devuelve un token JWT.

Body:

```json
{
  "identifier": "admin",
  "password": "123456"
}
```

---

## Registrar cuenta

Método: POST

Endpoint: /api/auth/register

Permite registrar una nueva cuenta dentro del sistema.

Este endpoint está disponible para usuarios con rol ADMIN o STAFF.

Body:

```json
{
  "user": "staff1",
  "email": "staff1@hiverh.com",
  "password": "123456",
  "rol": "STAFF"
}
```

---

## Crear sucursal

Método: POST

Endpoint: /api/branches

Permite crear una nueva sucursal.

Body:

```json
{
  "name": "Casa Central",
  "city": "Mar del Plata",
  "address": "Av. Independencia 1234"
}
```

---

## Crear departamento

Método: POST

Endpoint: /api/departments

Permite crear un nuevo departamento dentro de la empresa.

Body:

```json
{
  "name": "Recursos Humanos"
}
```

---

## Crear puesto

Método: POST

Endpoint: /api/positions

Permite crear un nuevo puesto de trabajo.

Body:

```json
{
  "name": "Analista de Personal"
}
```

---

## Crear empleado

Método: POST

Endpoint: /api/employees

Permite crear un nuevo empleado.

El empleado se registra en estado ACTIVE y se genera una cuenta EMPLOYEE por defecto.

Body:

```json
{
  "name": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "2235551111",
  "genre": "MALE",
  "dni": "40111222",
  "city": "Mar del Plata",
  "address": "San Martín 1000",
  "birth_date": "1995-05-10",
  "hire_date": "2026-06-01",
  "base_salary": 850000.0,
  "id_branch": 1,
  "id_position": 1,
  "id_department": 1
}
```

---

## Actualizar empleado parcial

Método: PATCH

Endpoint: /api/employees/40111222

Permite actualizar parcialmente los datos de un empleado.

Solo se modifican los campos enviados en el body.

Body:

```json
{
  "phoneNumber": "2235552222",
  "base_salary": 900000.0,
  "id_branch": 1,
  "id_position": 1,
  "id_department": 1
}
```

---

## Crear cronograma laboral

Método: POST

Endpoint: /api/work-schedules

Permite que ADMIN o STAFF creen un cronograma laboral para un empleado identificado por DNI.

Body:

```json
{
  "dniEmployee": "40111222",
  "workDate": "2026-08-17",
  "startTime": "08:00:00",
  "endTime": "14:00:00",
  "type": "WORKDAY",
  "note": "Turno mañana"
}
```

---

## Crear solicitud de jornada propia

Método: POST

Endpoint: /api/work-requests/me

Permite que el empleado autenticado cree una solicitud puntual de jornada. No se envía DNI porque el sistema usa la cuenta autenticada.

Body:

```json
{
  "requestType": "SHIFT_CHANGE",
  "targetDate": "2026-08-17",
  "startTime": "14:00:00",
  "endTime": "20:00:00",
  "reason": "Cambio de turno por trámite personal",
  "compensationDescription": "Compensa horas en el turno tarde"
}
```

---

## Aprobar solicitud de jornada

Método: PATCH

Endpoint: /api/work-requests/1/approve

Permite que ADMIN o STAFF aprueben una solicitud pendiente. Al aprobar, se registra la cuenta revisora y se genera o modifica el cronograma laboral correspondiente.

Body:

```json
{
  "reviewComment": "Cambio aprobado para la fecha solicitada"
}
```

---

## Crear período de liquidación

Método: POST

Endpoint: /api/payroll-periods

Permite crear un período mensual de liquidación.

El período nace en estado OPEN.

Body:

```json
{
  "month": 6,
  "year": 2026
}
```

---

## Crear concepto de liquidación

Método: POST

Endpoint: /api/payroll-concepts

Permite crear un concepto reutilizable para las liquidaciones.

El tipo ADDITION suma al sueldo y DEDUCTION descuenta.

Body:

```json
{
  "name": "Bono por presentismo",
  "description": "Bono mensual por asistencia perfecta",
  "type": "ADDITION"
}
```

---

## Crear liquidación en borrador

Método: POST

Endpoint: /api/payrolls

Permite crear una liquidación de sueldo en estado DRAFT.

El sistema guarda el sueldo base como snapshot y calcula los totales desde los detalles.

Body:

```json
{
  "dniEmployee": "40111222",
  "periodId": 1,
  "details": [
    {
      "payrollConceptId": 1,
      "amount": 50000.0,
      "description": "Bono mensual por asistencia perfecta"
    },
    {
      "payrollConceptId": 2,
      "amount": 25000.0,
      "description": "Descuento aplicado por adelanto de sueldo"
    }
  ]
}
```

---

## Registrar vacaciones

Método: POST

Endpoint: /api/vacations

Permite registrar vacaciones para un empleado activo.

Body:

```json
{
  "requestDate": "2026-06-11",
  "startDate": "2026-07-01",
  "endDate": "2026-07-10",
  "dniEmployee": "40111222"
}
```

---

## Registrar licencia

Método: POST

Endpoint: /api/licenses

Permite registrar una licencia asociada a un empleado.

Body:

```json
{
  "startDate": "2026-06-15",
  "endDate": "2026-06-17",
  "motive": "Licencia médica",
  "idCertificates": []
}
```

---

## Actualizar licencia

Método: PATCH

Endpoint: /api/licenses/1

Permite revisar una licencia.

Solo STAFF o ADMIN pueden actualizar el estado, si es paga y el comentario de revisión.

Body:

```json
{
  "status": "APPROVED",
  "isPaid": true,
  "reviewComment": "Licencia aprobada con certificado"
}
```

---

## Cargar certificado

Método: POST

Endpoint: /api/certificates

Permite cargar un certificado PDF asociado a una licencia.

Este endpoint usa multipart/form-data porque recibe un archivo.

Body form-data:

```text
idLicense = 1
```

```text
description = Certificado médico
```

```text
file = archivo.pdf
```

---

## Cambiar email propio

Método: PATCH

Endpoint: /api/accounts/me/email

Permite cambiar el email de la cuenta autenticada.

Body:

```json
{
  "email": "nuevo.email@hiverh.com"
}
```

---

## Cambiar password propia

Método: PATCH

Endpoint: /api/accounts/me/password

Permite cambiar la contraseña de la cuenta autenticada.

Body:

```json
{
  "currentPassword": "123",
  "newPassword": "123456"
}
```

---

## Cambiar rol de cuenta

Método: PATCH

Endpoint: /api/accounts/40111222/rol

Permite que un usuario ADMIN o STAFF cambie el rol de otra cuenta, con la restricción de que solo ADMIN puede asignar el rol ADMIN.

Body:

```json
{
  "rol": "EMPLOYEE"
}
```
