package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Branch.BranchEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_employee;

    @Column(name = "employee_name", nullable = false, length = 100)
    private String name;

    @Column(name = "employee_lastname", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", nullable = false, length = 100)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private GenreEnum genre;

    @Column(name = "dni", nullable = false, length = 100)
    private String dni;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthdate; //yyyy-mm-dd

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "termination_date", nullable = false)
    private LocalDate terminationDate;

    @Column(name = "net_salary", nullable = false)
    private double netSalary;

    @Column(name = "gross_salary", nullable = false)
    private double grossSalary;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @ManyToOne
    @JoinColumn(name = "id_branch", nullable = false)
    private BranchEntity branch;

    @OneToOne(optional = true)
    @JoinColumn(name = "id_account", nullable = true)
    private AccountEntity account;

}
