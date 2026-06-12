RRHH

ingles
camelCase -> java
snake_case -> bdd
Feature/<nombre_feature>
main -> dev
-------------------------------------------

Es un programa interno de la empresa que lo contrata. Este le va a dar permisos  a cierto tipo de usuario (El RRHH) para gestionar los datos de los empleados de la empresa.

El rrhh va a poder:

Crear un empleado:
-Cargar datos del empleado (ingresar nombre, apellido, numero de telefono, genero, dni, ciudad, domicilio, fecha de nacimiento, fecha de contratación, salario base)

Dar de baja al empleado (borrado lógico, estado pasa de ACTIVE a TERMINATED).

Ver lista de empleados:
-Filtrar (Sucursal/Fechas/Estado/Posicion/Departamento/Rango sueldo)

Ver info del empleado:
-acceso directo a enviar mail (desde la cuenta del rrhh logueado)
-ver ficha empleado
-ver historial de sueldos del empleado
-ver historial de cambios

Modificar información personal del empleado:
-Cambiar datos (nombre, apellido, dni, celular, etc...)
-Cambiar departamentos y posiciones
-Calcular/asignar sueldos cada mes:
-Asignar variaciones(bonos y descuentos)
-Asignar rol
-Cambiar de sucursal

Ver lista de solicitudes de Vacaciones:
-Filtrar (por empleado, fecha, estado(aceptada, rechazada))
-Aceptar/rechazar solicitudes de vacaciones

Ver lista de solicitudes de licencias
-Filtrar (por empleado, fecha, estado(aceptada, rechazada))
-Ver archivos adjuntos a la solicitud de licencia
-Aceptar/rechazar solicitudes de licencia

Ver lista de denuncias:
-Filtrar (Por denuncias revisadas y no revisadas)
-ver contenido de las denuncias

Suspender empleado x:
-asignar cantidad de tiempo
-ingresar motivo
-email automatico

Crear postulante:
-Nombre completo
-email(importante)
-CV
-puesto generic
-anotaciones
Enviar mail a los postulantes por un nuevo puesto que matchea postulaciones anteriores (con datos genericos, por ejemplo desarrollador IT)


Automatizaciones:
-comprobante de pago por email
-revisar horarios por wpp
-importar y exportar empleados por excel/pdf/csv

---------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------

El Empleado normal va a poder:

Revisar su información:
-Ver sus datos personales
-Ver su historial de sueldos
-Ver su presentismo
-Ver sus horarios

Solicitar licencia, vacaciones:
-Asignar Fechas

Solicitar traslado de sucursal-departamento-área-puesto

Realizar denuncias a otros empleados


roles:
admin -> Usuario con el permiso para hacer todo, y de paso tiene también el permiso de crear departamentos, puestos, sucursales, asignar Roles (básicamente el dueño, que carga su empresa).
gestión -> rol de rrhh.
empleado -> empleado de cualquier otra cosa que no sea RRHH.
postulante -> no tiene interacción con el sistema.

----------
empleado
tiene acceso limitado solo a su información personal, a realizar denuncias y a pedir licencias/vacaciones.
----------
rrhh
alta/baja/update empleados
----------
admin
control total del programa (todos los roles)





SISTEMA DE GESTION DE EMPLEADOS(ORGANIZA TU EMPRESA) (SISTEMA RRHH)
-SUELDOS, HORARIOS, CARGOS, HORAS EXTRA/VACACIONES, DIAS DE ENFERMEDAD/MATERNIDAD, ANTIGUEDAD.
-ASIGNAR ROLES, CARGAR/ELIMINAR/MODIFICAR EMPLEADOS
-DASHBOARD DE LA ACTIVIDAD DEL EMPLEADO




