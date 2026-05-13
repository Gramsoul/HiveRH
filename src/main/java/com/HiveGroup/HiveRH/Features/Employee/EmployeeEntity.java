package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Branch.BranchEntity;
import com.HiveGroup.HiveRH.Features.Complaint.ComplaintEntity;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.HiveGroup.HiveRH.Features.Payroll.PayrollEntity;
import com.HiveGroup.HiveRH.Features.Vacation.VacationEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employee")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_employee;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "lastname", nullable = false, length = 100)
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

    // Assignment
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeAssignmentEntity> assignments;

    @OneToMany(mappedBy = "employee")
    private List<PayrollEntity> payrolls;

    @OneToMany(mappedBy = "employee")
    private List<LicenseEntity> licenses;

    @OneToMany(mappedBy = "employee")
    private List<VacationEntity> vacations;

    @OneToMany(mappedBy = "employee")
    private List<ComplaintEntity> complaints;
}
