# HiveRH
This is a software solution designed for employee management, developed by the Human Resources department.

## Configuracion del entorno local

La aplicacion lee las credenciales de la base de datos desde un archivo `.env` ubicado en la raiz del proyecto.

1. Copiar el archivo de ejemplo:

```sh
cp .env.sample .env
```

2. Actualizar `.env` con la configuracion local de la base de datos:

```properties
DB_URL=jdbc:mysql://localhost:3306/hiverh
DB_USER=root
DB_PASSWORD=your_password
SECRET=secret_of_32_bytes
EXPIRATION=86400000
```

`DB_URL`, `DB_USER`, `DB_PASSWORD`, `SECRET` y `EXPIRATION` son utilizadas por `src/main/resources/application.yaml` y `JwtService` para configurar el datasource y la firma/vida del JWT.

El archivo `.env` es ignorado por Git porque puede contener credenciales locales. Mantener `.env.sample` actualizado cada vez que se agregue una nueva variable de entorno requerida.

## Configuracion de la base de datos local

El proyecto esta configurado para conectarse a MySQL. Si se utiliza una base de datos en la maquina host, MySQL debe estar instalado y corriendo localmente.

1. Ingresar a MySQL:

```sh
mysql -u root -p
```

2. Crear la base de datos:

```sql
CREATE DATABASE IF NOT EXISTS hiverh;
```

3. Verificar que el archivo `.env` apunte a esa base:

```properties
DB_URL=jdbc:mysql://localhost:3306/hiverh
DB_USER=root
DB_PASSWORD=your_password
SECRET=secret_of_32_bytes
EXPIRATION=86400000
```

La base de datos `hiverh` debe existir antes de iniciar la aplicacion. Hibernate no crea la base de datos MySQL automaticamente.

Con la configuracion actual:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

Hibernate crea o actualiza las tablas dentro de la base `hiverh` a partir de las entidades Java del proyecto.

## OpenAPI

El proyecto expone documentacion OpenAPI mediante Springdoc.

### Endpoints

1. Swagger UI: `http://localhost:8080/swagger-ui.html`
2. OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Uso

1. Levantar la aplicacion
2. Abrir `http://localhost:8080/swagger-ui.html`
3. Ejecutar `POST /api/auth/login`
4. Copiar el `token` recibido
5. Usar el boton `Authorize` en Swagger UI y pegar:

```text
Bearer <token>
```

### Alcance

1. Los endpoints `/api/auth/**` estan disponibles sin autenticacion
2. El resto de la API requiere JWT
3. Swagger UI y `/v3/api-docs` quedaron expuestos sin token para facilitar pruebas y consulta de contratos

## Validacion funcional con `HiveRH.http`

En la raiz del proyecto existe el archivo `HiveRH.http` para ejecutar una prueba funcional de la API desde IntelliJ IDEA HTTP Client o herramientas compatibles con archivos `.http`.

### Requisitos

1. Tener la aplicacion levantada en `http://localhost:8080`
2. Tener MySQL corriendo y la base `hiverh` configurada en `.env`
3. Reiniciar la aplicacion despues de cambios en el backend antes de volver a correr el flujo

### Que cubre

El archivo prueba un flujo completo de la API:

1. Registro y login
2. Creacion de departamento, sucursal y puesto
3. Creacion y actualizacion de empleado
4. Variaciones y liquidaciones
5. Vacaciones
6. Denuncias
7. Licencias
8. Certificados
9. Suspensiones
10. Limpieza opcional de datos de prueba

### Como usarlo

1. Abrir `HiveRH.http`
2. Ejecutar los requests en orden
3. Verificar que cada request que crea recursos capture correctamente sus IDs en variables globales

El archivo ya captura automaticamente variables como:

- `token`
- `departmentId`
- `branchId`
- `positionId`
- `employeeId`
- `variationId`
- `payrollId`
- `vacationId`
- `complaintId`
- `licenseId`
- `certificateId`

### Certificado de prueba

Para la carga multipart se incluye el archivo `testdata/certificate-sample.pdf` dentro del repositorio.

Esto permite que el flujo sea compartible entre integrantes del equipo sin depender de rutas absolutas locales.

En `HiveRH.http` la carga del archivo usa una ruta relativa literal:

```http
< ./testdata/certificate-sample.pdf
```

### Notas importantes

1. La API usa JWT en todos los endpoints salvo `/api/auth/**`
2. Si un request de alta falla, los requests siguientes que dependan de ese ID tambien van a fallar
3. Si se reutiliza la misma base entre corridas, pueden quedar datos anteriores que afecten filtros o validaciones de negocio
4. Si se modifica el contrato de un controlador, DTO o response, tambien debe actualizarse `HiveRH.http`

Autors: Gallego Romero Gonzalo N., Herrera Victor M., Molina Cristian N., Romero Rajoy Jose L. 
