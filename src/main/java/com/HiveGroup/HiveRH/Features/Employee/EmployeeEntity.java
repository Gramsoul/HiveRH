package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.HiveGroup.HiveRH.Features.Payroll.PayrollEntity;
import com.HiveGroup.HiveRH.Features.Vacation.VacationEntity;
import com.HiveGroup.HiveRH.Features.WorkRequest.WorkRequestEntity;
import com.HiveGroup.HiveRH.Features.WorkSchedule.WorkScheduleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Column(name = "last_name", nullable = false, length = 100)
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

    @Column(name = "termination_date")
    private LocalDate terminationDate = null;

    @Column(name = "base_salary")
    private Double baseSalary;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    @OneToOne(optional = true)
    @JoinColumn(name = "id_account", nullable = true)
    private AccountEntity account = null;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeAssignmentEntity> assignments;

    @OneToMany(mappedBy = "employee")
    private List<PayrollEntity> payrolls = null;

    @OneToMany(mappedBy = "employee")
    private List<LicenseEntity> licenses = null;

    @OneToMany(mappedBy = "employee")
    private List<VacationEntity> vacations = null;

    @OneToMany(mappedBy = "employee")
    private List<WorkScheduleEntity> workSchedules = null;

    @OneToMany(mappedBy = "employee")
    private List<WorkRequestEntity> workRequests = null;
}
