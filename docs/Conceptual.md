```mermaid

erDiagram
%% Entidades y Atributos
Account {
varchar user
varchar email
varchar password
}

    Rol {
        varchar Type "Admin, RRHH"
    }

    Branch {
        varchar name
        varchar city
        varchar address
        boolean active
    }

    Variation {
        varchar name
        varchar description
        decimal total
        boolean fixed
    }

    Payroll {
        decimal total
        date payroll_date
    }

    Department {
        varchar name
        boolean active
    }

    Position {
        varchar name
        boolean active
    }

    Employee {
        varchar name
        varchar lastname
        varchar phoneNumber
        varchar genre
        varchar dni
        varchar city
        varchar address
        date birth_date
        date hire_date
        date termination_date
        decimal base_salary
        varchar status
    }

    Vacation {
        date request_date
        boolean accepted
        date start_date
        date end_date
    }

    License {
        date request_date
        boolean accepted
        date start_date
        date end_date
        boolean paid
        varchar motive
        varchar description
    }

    Certificate {
        varchar file
        varchar description
    }

    Complaint {
        varchar file
        varchar description
    }

    %% Relaciones de Rol (Admin, RRHH)
    Rol ||--o{ Account : "TIENE"
    Rol ||--o{ Branch : "CREA / ELIMINA"
    Rol ||--o{ Department : "CREA / ELIMINA"
    Rol ||--o{ Position : "CREA / ELIMINA"
    Rol ||--o{ Employee : "CREA / ELIMINA / CONSULTA / MODIFICA"
    Rol ||--o{ Vacation : "ACEPTA / CONSULTA"
    Rol ||--o{ License : "ACEPTA / CONSULTA"
    Rol ||--o{ Payroll : "CONSULTA"

    %% Relaciones de Employee
    Employee ||--|| Account : "TIENE"
    Branch ||--o{ Employee : "TIENE"
    Department ||--o{ Employee : "PERTENECE"
    Position ||--o{ Employee : "TIENE"
    Employee ||--o{ Vacation : "PIDE / CONSULTA / ELIMINA"
    Employee ||--o{ License : "PIDE / CONSULTA / ELIMINA"
    Employee ||--o{ Complaint : "REALIZA"
    Employee ||--o{ Payroll : "TIENE"

    %% Otras Relaciones
    License ||--o| Certificate : "Tiene"
    Payroll ||--o{ Variation : "TIENE"
```