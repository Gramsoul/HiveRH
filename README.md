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

Autors: Gallego Romero Gonzalo N., Herrera Victor M., Molina Cristian N., Romero Rajoy Jose L. 
