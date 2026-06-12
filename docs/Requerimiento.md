# HiveRH - Requerimientos del Sistema

HiveRH es un sistema interno de gestión de empleados pensado para empresas que necesitan organizar y administrar su área de Recursos Humanos.

El sistema permite gestionar empleados, cuentas de usuario, roles, sucursales, departamentos, puestos, liquidaciones de sueldo, vacaciones, licencias, certificados, denuncias y suspensiones.

---

## Objetivo del sistema

El objetivo principal de HiveRH es brindar una herramienta interna para que la empresa pueda administrar de forma ordenada la información de sus empleados.

El sistema está pensado para que cada tipo de usuario tenga permisos diferentes según su rol:

- **ADMIN**: control total del sistema.
- **RRHH**: gestión operativa de empleados y solicitudes.
- **EMPLOYEE**: acceso limitado a su propia información y solicitudes.

De esta forma, el sistema permite separar responsabilidades y proteger la información sensible de la empresa.

---

## Convenciones del proyecto

Para mantener el proyecto ordenado y consistente, se utilizan las siguientes convenciones:

| Elemento | Convención |
|---|---|
| Idioma del código | Inglés |
| Java | camelCase |
| Base de datos | snake_case |
| Organización de paquetes | Feature/nombre_feature |
| Rama principal | main |
| Rama de desarrollo | dev |

---

## Roles del sistema

### ADMIN

El usuario con rol **ADMIN** tiene control total sobre el sistema.

Representa al usuario principal de la empresa o dueño del sistema. Puede administrar la configuración general y acceder a todas las funcionalidades.

Funciones principales:

- Crear sucursales.
- Crear departamentos.
- Crear puestos.
- Crear y modificar empleados.
- Asignar roles.
- Gestionar cuentas de usuario.
- Acceder a todos los módulos del sistema.

---

### RRHH

El usuario con rol **RRHH** representa al área de Recursos Humanos.

Tiene permisos para gestionar empleados, revisar solicitudes, administrar licencias, vacaciones, denuncias y suspensiones.

Funciones principales:

- Crear empleados.
- Actualizar información de empleados.
- Dar de baja empleados mediante borrado lógico.
- Consultar listados de empleados.
- Gestionar vacaciones.
- Gestionar licencias.
- Revisar denuncias.
- Registrar suspensiones.
- Administrar liquidaciones y variaciones salariales.

---

### EMPLOYEE

El usuario con rol **EMPLOYEE** representa a un empleado común de la empresa.

Tiene acceso limitado únicamente a su propia información y a las solicitudes que puede realizar dentro del sistema.

Funciones principales:

- Ver sus datos personales.
- Ver su historial de sueldos.
- Solicitar vacaciones.
- Solicitar licencias.
- Realizar denuncias.

---

## Funcionalidades para RRHH

El área de Recursos Humanos puede gestionar la información laboral y administrativa de los empleados.

### Crear empleado

RRHH puede registrar un nuevo empleado cargando sus datos principales:

- Nombre.
- Apellido.
- Número de teléfono.
- Género.
- DNI.
- Ciudad.
- Domicilio.
- Fecha de nacimiento.
- Fecha de contratación.
- Salario base.
- Sucursal.
- Departamento.
- Puesto.

Al crear un empleado, el sistema lo registra en estado **ACTIVE** y genera una cuenta de usuario con rol **EMPLOYEE** por defecto.

---

### Dar de baja empleado

RRHH puede dar de baja a un empleado mediante una eliminación lógica.

Esto significa que el empleado no se borra físicamente de la base de datos, sino que su estado cambia de **ACTIVE** a **TERMINATED**.

Este comportamiento permite conservar el historial del empleado dentro del sistema.

---

### Ver lista de empleados

RRHH puede consultar el listado de empleados registrados en la empresa.

El sistema permite aplicar filtros para facilitar la búsqueda:

- Sucursal.
- Fecha.
- Estado.
- Posición.
- Departamento.
- Rango de sueldo.

---

### Ver información de un empleado

RRHH puede consultar la ficha completa de un empleado.

Desde esta sección se puede revisar:

- Información personal.
- Información laboral.
- Sucursal asignada.
- Departamento.
- Puesto.
- Estado actual.
- Historial de sueldos.
- Historial de licencias y vacaciones.

---

### Modificar información del empleado

RRHH puede actualizar los datos personales y laborales de un empleado.

Entre los datos modificables se incluyen:

- Nombre.
- Apellido.
- DNI.
- Celular.
- Ciudad.
- Domicilio.
- Departamento.
- Puesto.
- Sucursal.
- Rol asociado.
- Salario base.

---

### Gestionar sueldos

RRHH puede calcular y asignar las liquidaciones mensuales de los empleados.

El sistema utiliza el sueldo base del empleado y permite agregar variaciones salariales.

Las variaciones pueden ser:

- Bonos.
- Descuentos.
- Horas extra.
- Ajustes salariales.

Una variación positiva suma al sueldo y una variación negativa descuenta del total.

---

### Gestionar vacaciones

RRHH puede ver y administrar las solicitudes de vacaciones realizadas por los empleados.

El sistema permite:

- Ver solicitudes de vacaciones.
- Filtrar por empleado.
- Filtrar por fecha.
- Filtrar por estado.
- Actualizar el estado de aceptación de las solicitudes.

---

### Gestionar licencias

RRHH puede revisar las solicitudes de licencia cargadas por los empleados.

El sistema permite:

- Ver solicitudes de licencias.
- Filtrar por empleado.
- Filtrar por fecha.
- Filtrar por estado.
- Ver certificados adjuntos.
- Aceptar licencias.
- Rechazar licencias.

---

### Gestionar denuncias

RRHH puede consultar las denuncias realizadas dentro del sistema.

El sistema permite:

- Ver denuncias pendientes.
- Ver denuncias revisadas.
- Filtrar denuncias según su estado.
- Consultar el contenido de cada denuncia.
- Marcar denuncias como revisadas.

---

### Suspender empleado

RRHH puede registrar una suspensión para un empleado.

Al crear una suspensión, se debe indicar:

- Empleado suspendido.
- Motivo de la suspensión.
- Fecha de inicio.
- Fecha de finalización.

Cuando se registra la suspensión, el estado del empleado cambia automáticamente a **SUSPENDED**.

---

## Funcionalidades para empleados

El empleado común tiene acceso limitado dentro del sistema.

Su rol está pensado para que pueda consultar su propia información y realizar solicitudes internas.

### Revisar información personal

El empleado puede ver sus propios datos personales y laborales.

Puede consultar:

- Datos personales.
- Sucursal asignada.
- Departamento.
- Puesto.
- Estado laboral.
- Historial de sueldos.

---

### Solicitar vacaciones

El empleado puede solicitar vacaciones indicando el período correspondiente.

Debe cargar:

- Fecha de inicio.
- Fecha de finalización.

La solicitud queda pendiente hasta que RRHH la revise.

---

### Solicitar licencia

El empleado puede solicitar una licencia indicando las fechas correspondientes.

También puede adjuntar certificados cuando sea necesario.

La solicitud queda pendiente de aprobación por parte de RRHH.

---

### Realizar denuncias

El empleado puede realizar denuncias relacionadas con situaciones internas de la empresa.

Estas denuncias quedan registradas en el sistema para ser revisadas por RRHH.

---

## Alcance general

HiveRH permite administrar las principales tareas del área de Recursos Humanos:

- Alta, baja y modificación de empleados.
- Gestión de roles y permisos.
- Administración de sucursales, departamentos y puestos.
- Gestión de sueldos, bonos y descuentos.
- Registro de vacaciones.
- Registro de licencias y certificados.
- Gestión de denuncias internas.
- Registro de suspensiones.
- Consulta de información personal y laboral por parte del empleado.
