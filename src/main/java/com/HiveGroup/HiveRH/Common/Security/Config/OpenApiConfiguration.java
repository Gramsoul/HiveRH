package com.HiveGroup.HiveRH.Common.Security.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI hiveRhOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HiveRH API")
                        .description("""
                                HiveRH es una API REST para la gestion interna de Recursos Humanos.

                                El sistema permite administrar empleados, cuentas de usuario, roles,
                                sucursales, departamentos, puestos, liquidaciones de sueldo, licencias,
                                vacaciones, suspensiones, denuncias internas, certificados y variaciones salariales.

                                Flujo recomendado de uso:
                                1. Iniciar sesion con POST /api/auth/login.
                                2. Copiar el token JWT recibido.
                                3. Autorizar Swagger con el boton Authorize usando: Bearer <token>.
                                4. Consumir los endpoints protegidos segun el rol de la cuenta autenticada.

                                Roles principales:
                                - ADMIN: administra cuentas, estructura organizacional y recursos del sistema.
                                - RRHH: gestiona empleados, licencias, vacaciones, suspensiones, denuncias y liquidaciones.
                                - EMPLOYEE: consulta y opera sobre recursos propios cuando la regla de negocio lo permite.

                                Los endpoints GET con filtros reciben los criterios por query params. No es obligatorio
                                enviar todos los filtros; se puede enviar uno, varios o ninguno.
                                """)
                        .version("v1")
                        .contact(new Contact()
                                .name("HiveRH Team"))
                        .license(new License()
                                .name("Uso interno")))
                .tags(List.of(
                        new Tag().name("Auth").description("Autenticacion y registro de cuentas para obtener acceso al sistema."),
                        new Tag().name("Accounts").description("Operacion sobre la cuenta autenticada y administracion de roles."),
                        new Tag().name("Branches").description("Administracion de sucursales de la empresa."),
                        new Tag().name("Departments").description("Administracion de departamentos internos."),
                        new Tag().name("Positions").description("Administracion de puestos de trabajo."),
                        new Tag().name("Employees").description("Gestion de empleados, perfiles y bajas logicas."),
                        new Tag().name("Variations").description("Conceptos salariales que suman o descuentan en liquidaciones."),
                        new Tag().name("Payrolls").description("Liquidaciones de sueldo y consultas por empleado."),
                        new Tag().name("Vacations").description("Solicitudes y registros de vacaciones."),
                        new Tag().name("Licenses").description("Licencias de empleados y su estado de aprobacion."),
                        new Tag().name("Certificates").description("Carga, consulta y descarga de certificados PDF."),
                        new Tag().name("Complaints").description("Denuncias internas y seguimiento de revision."),
                        new Tag().name("Suspensions").description("Suspensiones de empleados y cambio de estado asociado.")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT obtenido desde /api/auth/login")));
    }
}
