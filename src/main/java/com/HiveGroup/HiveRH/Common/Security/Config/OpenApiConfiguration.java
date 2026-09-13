package com.HiveGroup.HiveRH.Common.Security.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
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
                                REST API for Human Resources workflows: employees, accounts, roles,
                                organizational structure, work schedules, work requests, payroll periods,
                                payrolls, licenses, vacations, and certificates.

                                ### How to test this API

                                Swagger sends real requests to the deployed application. Any data created,
                                updated, or deleted from this page is persisted in the connected MySQL database.

                                1. Run `POST /api/auth/login` with an existing account.
                                
                                2. Copy the `token` value from the response.
                                
                                3. Click `Authorize`, paste only the JWT token, and confirm.

                                ### Available roles

                                - `ADMIN`: manages accounts, roles, branches, departments, positions, and general resources.
                                
                                - `STAFF`: manages employees, work schedules, work requests, licenses, vacations, and payrolls.
                                
                                - `EMPLOYEE`: reads and manages their own resources when business rules allow it.

                                ### Usage notes

                                - Protected endpoints require a JWT.
                                - GET filters are sent as query parameters.
                                - Filters are optional unless an endpoint states otherwise.
                                """)
                        .version("v1")
                        .contact(new Contact()
                                .name("HiveRH Team")))
                .tags(List.of(
                        new Tag().name("01 Auth").description("Authentication and account registration."),
                        new Tag().name("02 Accounts").description("Authenticated account operations and role management."),
                        new Tag().name("03 Branches").description("Company branch management."),
                        new Tag().name("04 Departments").description("Internal department management."),
                        new Tag().name("05 Positions").description("Job position management."),
                        new Tag().name("06 Employees").description("Employee records, profiles, and status management."),
                        new Tag().name("07 Work Schedules").description("Assigned employee work schedules and daily work blocks."),
                        new Tag().name("08 Work Requests").description("Employee daily work requests and administrative review."),
                        new Tag().name("09 Payroll Periods").description("Monthly payroll periods and closing workflow."),
                        new Tag().name("10 Payroll Concepts").description("Reusable payroll concepts for additions and deductions."),
                        new Tag().name("11 Payrolls").description("Payroll records, details, and employee payroll history."),
                        new Tag().name("12 Vacations").description("Vacation requests and records."),
                        new Tag().name("13 Licenses").description("Employee licenses and approval status."),
                        new Tag().name("14 Certificates").description("PDF certificate upload, lookup, and download.")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste only the JWT token returned by POST /api/auth/login. Swagger adds the Bearer prefix automatically.")));
    }
}
