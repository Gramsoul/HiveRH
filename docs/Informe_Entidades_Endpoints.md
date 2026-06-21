# Informe HiveRH

Este informe describe el recorrido completo del sistema HiveRH, desde la autenticación inicial hasta la operación de los módulos principales.

La idea es que sirva como guía de defensa, documentación de endpoints y orden recomendado para completar una colección de Postman.

El proyecto expone una API RESTful orientada a la gestión de recursos humanos. A través de esta API se pueden administrar cuentas, empleados, estructura organizacional, licencias, certificados, vacaciones, denuncias, suspensiones, variaciones salariales y liquidaciones de sueldo.

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

Puede crear sucursales, departamentos, puestos, variaciones, empleados, modificar roles y acceder a recursos administrativos.

#### RRHH

Es el rol operativo del área de Recursos Humanos.

Puede gestionar empleados, licencias, suspensiones y consultar información sensible según la configuración de seguridad del sistema.

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

Algunos módulos, como payroll, vacation y complaint, quedan protegidos por autenticación general, aunque no necesariamente diferenciados por rol.

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

Además, al registrar un nuevo empleado se genera automáticamente una cuenta con rol EMPLOYEE por defecto.

### Variation

Gestiona los conceptos salariales que se utilizan en las liquidaciones.

Una variación con total positivo suma al sueldo del empleado, mientras que una variación con total negativo descuenta del sueldo.

No se acepta una variación con total igual a cero.

### Payroll

Genera las liquidaciones de sueldo de los empleados.

El sistema calcula el total tomando como base el sueldo del empleado y aplicando las variaciones correspondientes.

También valida que el empleado esté activo, que tenga un sueldo válido y que no exista más de una liquidación para el mismo empleado dentro del mismo mes.

### Vacation

Gestiona las vacaciones de los empleados.

Valida que las fechas ingresadas sean correctas, que el empleado esté activo y que no exista superposición con otros períodos de vacaciones ya registrados.

### License

Gestiona las licencias de los empleados.

Permite asociar certificados a una licencia y también permite realizar actualizaciones parciales mediante PATCH, modificando solo los campos enviados en la solicitud.

### Certificate

Gestiona los certificados PDF asociados a las licencias.

Utiliza multipart/form-data para permitir la carga de archivos desde el cliente hacia el sistema.

### Complaint

Gestiona las denuncias realizadas dentro del sistema.

Al crear una denuncia, esta queda inicialmente en estado pendiente.

Luego puede ser marcada como revisada cuando corresponda.

### Suspension

Gestiona las suspensiones de empleados.

Al registrar una suspensión, el sistema cambia automáticamente el estado del empleado a SUSPENDED.

---

## Preparación inicial

- Levantar la base de datos MySQL y configurar las variables DB_URL, DB_USER, DB_PASSWORD, SECRET y EXPIRATION.
- Ejecutar la aplicación Spring Boot.
- Tener al menos una cuenta ADMIN inicial. Como el endpoint /api/auth/register está protegido, el primer ADMIN debe existir previamente por seed, carga manual o base ya preparada.
- En Postman, crear una variable token y enviar Authorization: Bearer {{token}} en todos los endpoints protegidos.

---

## 1. Autenticación y cuentas

- El usuario se autentica con POST /api/auth/login enviando identifier y password.
- El identifier puede ser usuario o email porque la búsqueda se realiza por user o email.
- Si las credenciales son correctas, la API devuelve un token JWT que incluye el rol como authority ROLE_ADMIN, ROLE_RRHH o ROLE_EMPLOYEE.
- Con el token activo se puede registrar una cuenta, cambiar email propio, cambiar contraseña propia o, si se es ADMIN, cambiar roles.

---

## 2. Configuración base de la empresa

- Primero se cargan las sucursales con /api/branch.
- Después se cargan los departamentos con /api/department.
- Luego se cargan los puestos con /api/position.
- Estos tres módulos son la base para crear empleados porque el alta de empleado exige id_branch, id_position e id_department.

---

## 3. Alta de empleado

- Con sucursal, puesto y departamento ya existentes, se crea el empleado mediante POST /api/employees.
- El empleado nace con estado ACTIVE.
- El sistema crea automáticamente una cuenta por defecto para ese empleado: usuario igual al DNI, email {dni}@hiverh.local y contraseña inicial igual al DNI.
- La respuesta del empleado incluye sus datos personales, estado, sucursal, cuenta asociada y asignación con puesto/departamento.

---

## 4. Gestión de empleados

- RRHH o ADMIN pueden listar empleados y filtrarlos por nombre, DNI, sucursal, fecha de ingreso, estado, puesto, departamento o rango salarial.
- PATCH permite actualizar datos puntuales sin enviar todo el objeto. En el controlador actual, PUT también invoca la lógica parcial de actualización.
- DELETE no borra físicamente al empleado: cambia su estado a TERMINATED.
- El empleado autenticado puede consultar su propio perfil con GET /api/employees/me.

---

## 5. Variaciones de sueldo

- Antes de liquidar sueldos, se cargan variaciones con /api/variations.
- Una variación representa un concepto que modifica la liquidación: bono, premio, descuento, adelanto, penalización, etc.
- Si total es positivo suma al sueldo base; si total es negativo descuenta.
- El sistema no acepta variaciones con total igual a cero.

---

## 6. Payroll / liquidación de sueldo

- Para crear una liquidación se llama a POST /api/payrolls con payrollDate, idEmployee y una lista opcional de idVariations.
- El sistema busca el empleado, valida que esté ACTIVE, que tenga sueldo base válido y que la fecha de liquidación no sea anterior a su contratación.
- También valida que ese empleado no tenga otra liquidación en el mismo mes.
- El total final se calcula como sueldo base + suma de variaciones.
- No se puede repetir una misma variación dentro de la misma liquidación y el total final no puede quedar negativo.

---

## 7. Vacaciones

- Las vacaciones se registran con /api/vacation y se asocian a un empleado.
- El sistema valida empleado activo, fechas obligatorias, fecha final posterior a inicio y que la solicitud no sea posterior al inicio.
- También evita vacaciones superpuestas para el mismo empleado.
- Permite listar por estado de aceptación, rango de fechas y nombre completo.

---

## 8. Licencias y certificados

- Las licencias se registran con /api/license y representan ausencias justificadas, por ejemplo licencia médica.
- Se puede crear la licencia y luego adjuntar uno o más certificados PDF con /api/certificate usando multipart/form-data.
- Un ADMIN o RRHH puede listar todas las licencias; un empleado puede acceder a sus propias licencias/certificados según las reglas de autorización.
- El certificado se guarda como bytes y puede consultarse como PDF o como información resumida.

---

## 9. Denuncias internas

- Las denuncias se crean con /api/complaint y se asocian a un empleado activo.
- Al crearse quedan en estado PENDING.
- Luego se puede cambiar el estado a REVIEWED mediante PUT /api/complaint/{id_complaint}.
- El listado permite filtrar por ID, título, estado y rango de fechas.

---

## 10. Suspensiones

- Una suspensión se registra con /api/suspension indicando empleado, motivo y rango de fechas.
- El sistema valida empleado, motivo obligatorio y fechas coherentes.
- Al crear la suspensión cambia el estado del empleado a SUSPENDED.
- Luego las liquidaciones y vacaciones no deberían poder registrarse para ese empleado mientras no vuelva a estar ACTIVE, porque esos servicios validan el estado del empleado.

---

# Informe de endpoints

## Auth

### POST /api/auth/login

Permite iniciar sesión en el sistema enviando identifier y password.

El identifier puede corresponder al usuario o al email de la cuenta.

Si las credenciales son correctas, el sistema devuelve un token JWT que luego se utiliza para acceder a los endpoints protegidos.

### POST /api/auth/register

Permite registrar nuevas cuentas dentro del sistema.

Este endpoint está disponible únicamente para usuarios con rol ADMIN o RRHH.

Al registrar una cuenta, la contraseña se guarda encriptada por seguridad.

---

## Account

### PATCH /api/accounts/me/email

Permite que el usuario autenticado cambie su propio email.

Solo modifica el email de la cuenta que está usando el token actual.

### PATCH /api/accounts/me/password

Permite que el usuario autenticado cambie su propia contraseña.

La nueva contraseña se guarda encriptada.

### PATCH /api/accounts/{id}/role

Permite que un usuario con rol ADMIN cambie el rol de otra cuenta.

Se utiliza para modificar permisos de acceso dentro del sistema.

---

## Branch

### GET /api/branch

Lista las sucursales activas registradas en el sistema.

### POST /api/branch

Crea una nueva sucursal.

### PUT /api/branch/{id_branch}

Actualiza los datos de una sucursal existente.

### DELETE /api/branch/{id_branch}

Realiza una baja lógica de la sucursal.

No elimina el registro de la base de datos, sino que la marca como inactiva.

---

## Department

### GET /api/department

Lista los departamentos registrados en el sistema.

Permite aplicar filtros por ID, nombre y estado activo.

### POST /api/department

Crea un nuevo departamento.

### DELETE /api/department/{id_department}

Realiza una baja lógica del departamento.

El registro no se elimina físicamente, sino que queda marcado como inactivo.

---

## Position

### GET /api/position

Lista los puestos de trabajo registrados en el sistema.

Permite filtrar por departamento, nombre y estado activo.

### POST /api/position

Crea un nuevo puesto de trabajo.

### DELETE /api/position/{id}

Realiza una baja lógica del puesto.

El puesto no se borra definitivamente, sino que queda marcado como inactivo.

---

## Employee

### GET /api/employees

Lista los empleados registrados en el sistema.

Permite aplicar filtros según los parámetros disponibles.

### GET /api/employees/me

Devuelve el empleado asociado a la cuenta autenticada.

Sirve para que un usuario pueda consultar sus propios datos como empleado.

### GET /api/employees/{id}

Consulta un empleado específico por su ID.

### POST /api/employees

Crea un nuevo empleado en estado ACTIVE.

Al crear el empleado, también se genera automáticamente una cuenta con rol EMPLOYEE por defecto.

### PATCH /api/employees/{id}

Actualiza parcialmente los datos de un empleado.

Solo modifica los campos enviados en la solicitud.

### PUT /api/employees/{id}

Actualiza los datos del empleado.

### DELETE /api/employees/{dni}

Realiza una baja lógica del empleado.

El empleado no se elimina físicamente, sino que su estado cambia a TERMINATED.

---

## Variation

### GET /api/variations

Lista las variaciones salariales registradas.

Permite aplicar filtros según los parámetros disponibles.

### GET /api/variations/{id}

Consulta una variación salarial específica por su ID.

### POST /api/variations

Crea una nueva variación salarial.

Estas variaciones pueden representar sumas o descuentos en una liquidación.

### PATCH /api/variations/{id}

Actualiza parcialmente una variación salarial.

Solo se modifican los campos enviados.

### PUT /api/variations/{id}

Reemplaza los datos de una variación salarial existente.

### DELETE /api/variations/{id}

Elimina una variación salarial.

---

## Payroll

### GET /api/payrolls

Lista las liquidaciones de sueldo registradas en el sistema.

### GET /api/payrolls/{id}

Consulta una liquidación de sueldo específica por su ID.

### POST /api/payrolls

Crea una nueva liquidación de sueldo.

El sistema calcula el total tomando el sueldo base del empleado y aplicando las variaciones salariales correspondientes.

### DELETE /api/payrolls/{id}

Elimina una liquidación de sueldo.

Luego de eliminarla, devuelve los datos de la liquidación eliminada.

---

## Vacation

### GET /api/vacation

Lista las vacaciones registradas.

Permite aplicar filtros según los parámetros disponibles.

### POST /api/vacation

Registra vacaciones para un empleado activo.

El sistema valida que las fechas sean correctas y que no haya superposición con otros períodos.

### PUT /api/vacation/{id_vacation}

Actualiza un registro de vacaciones existente.

### DELETE /api/vacation/{id_vacation}

Elimina el registro de vacaciones indicado.

---

## License

### GET /api/license

Lista las licencias registradas en el sistema.

Permite aplicar filtros según los parámetros disponibles.

### GET /api/license/{id_license}

Consulta una licencia específica por su ID.

### POST /api/license

Crea una nueva licencia asociada a un empleado.

### PATCH /api/license

Actualiza parcialmente una licencia.

Solo modifica los campos enviados en la solicitud.

### DELETE /api/license/{id_license}

Elimina una licencia.

---

## Certificate

### POST /api/certificate

Carga un certificado PDF asociado a una licencia.

Este endpoint utiliza multipart/form-data para permitir el envío de archivos.

### GET /api/certificate/{id_certificate}

Descarga el archivo PDF almacenado correspondiente al certificado.

### GET /api/certificate-info?id={id}

Consulta la información del certificado sin descargar el archivo PDF.

### DELETE /api/certificate/{id_certificate}

Elimina el certificado indicado.

---

## Complaint

### GET /api/complaint

Lista las denuncias registradas en el sistema.

Permite aplicar filtros según los parámetros disponibles.

### POST /api/complaint

Crea una nueva denuncia en estado PENDING.

Esto indica que la denuncia queda pendiente de revisión.

### PUT /api/complaint/{id_complaint}

Actualiza el estado de una denuncia.

Permite cambiar el estado a PENDING o REVIEWED, según corresponda.

---

## Suspension

### GET /api/suspension

Lista las suspensiones registradas en el sistema.

Permite aplicar filtros según los parámetros disponibles.

### POST /api/suspension

Registra una nueva suspensión para un empleado.

Al crear la suspensión, el sistema cambia automáticamente el estado del empleado a SUSPENDED.

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
status_enum
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

Este endpoint está disponible para usuarios con rol ADMIN o RRHH.

Body:

```json
{
  "user": "rrhh1",
  "email": "rrhh1@hiverh.com",
  "password": "123456",
  "rol": "RRHH"
}
```

---

## Crear sucursal

Método: POST

Endpoint: /api/Branch

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

Endpoint: /api/department

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

Endpoint: /api/position

Permite crear un nuevo puesto de trabajo.

Body:

```json
{
  "name": "Analista de RRHH"
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

Endpoint: /api/employees/1

Permite actualizar parcialmente los datos de un empleado.

Solo se modifican los campos enviados en el body.

Body:

```json
{
  "phoneNumber": "2235552222",
  "base_salary": 900000.0
}
```

---

## Crear variación positiva

Método: POST

Endpoint: /api/variations

Permite crear una variación salarial positiva.

Este tipo de variación suma al sueldo del empleado.

Body:

```json
{
  "title": "Bono por presentismo",
  "description": "Bono mensual por asistencia perfecta",
  "total": 50000.0
}
```

---

## Crear variación negativa

Método: POST

Endpoint: /api/variations

Permite crear una variación salarial negativa.

Este tipo de variación descuenta del sueldo del empleado.

Body:

```json
{
  "title": "Descuento por adelanto",
  "description": "Descuento aplicado por adelanto de sueldo",
  "total": -25000.0
}
```

---

## Crear liquidación

Método: POST

Endpoint: /api/payrolls

Permite crear una liquidación de sueldo.

El sistema calcula el total usando el sueldo base del empleado y las variaciones indicadas.

Body:

```json
{
  "payrollDate": "2026-06-30",
  "idEmployee": 1,
  "idVariations": [
    1,
    2
  ]
}
```

---

## Registrar vacaciones

Método: POST

Endpoint: /api/vacation

Permite registrar vacaciones para un empleado activo.

Body:

```json
{
  "requestDate": "2026-06-11",
  "accepted": true,
  "startDate": "2026-07-01",
  "endDate": "2026-07-10",
  "paid": true,
  "idEmployee": 1
}
```

---

## Registrar licencia

Método: POST

Endpoint: /api/license

Permite registrar una licencia asociada a un empleado.

Body:

```json
{
  "requestDate": "2026-06-11",
  "isAccepted": false,
  "startDate": "2026-06-15",
  "endDate": "2026-06-17",
  "isPaid": true,
  "motive": "Licencia médica",
  "description": "Reposo indicado por profesional",
  "idCertificates": [],
  "idEmployee": 1
}
```

---

## Actualizar licencia

Método: PATCH

Endpoint: /api/license

Permite actualizar parcialmente una licencia.

Solo se modifican los campos enviados en el body.

Body:

```json
{
  "id": 1,
  "isAccepted": true,
  "description": "Licencia médica aprobada"
}
```

---

## Cargar certificado

Método: POST

Endpoint: /api/certificate

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

## Crear denuncia

Método: POST

Endpoint: /api/complaint

Permite crear una denuncia.

La denuncia queda inicialmente en estado PENDING.

Body:

```json
{
  "title": "Incidente interno",
  "description": "Se registra una situación para revisión de RRHH",
  "idEmployee": 1
}
```

---

## Actualizar denuncia

Método: PUT

Endpoint: /api/complaint/1

Permite actualizar el estado de una denuncia.

Body:

```json
{
  "status": "REVIEWED"
}
```

---

## Crear suspensión

Método: POST

Endpoint: /api/suspensión

Permite registrar una suspensión para un empleado.

Al crear la suspensión, el estado del empleado cambia a SUSPENDED.

Body:

```json
{
  "id_employee": 1,
  "motive": "Incumplimiento de normas internas",
  "start_date": "2026-06-20",
  "end_date": "2026-06-22"
}
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

Endpoint: /api/accounts/2/role

Permite que un usuario ADMIN cambie el rol de otra cuenta.

Body:

```json
{
  "rol": "EMPLOYEE"
}
```
