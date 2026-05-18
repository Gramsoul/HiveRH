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
```

`DB_URL`, `DB_USER` y `DB_PASSWORD` son utilizadas por `src/main/resources/application.yaml` para configurar el datasource de Spring.

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

Autors: Gallego Romero Gonzalo N., Herrera Victor M., Molina Cristian N., Romero Rajoy Jose L. 
