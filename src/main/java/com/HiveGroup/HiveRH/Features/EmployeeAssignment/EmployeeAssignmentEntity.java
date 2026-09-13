package com.HiveGroup.HiveRH.Features.EmployeeAssignment;

import com.HiveGroup.HiveRH.Features.Branch.BranchEntity;
import com.HiveGroup.HiveRH.Features.Department.DepartmentEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Position.PositionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_assignment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_assignment")
    private Long id_assignment;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne
    @JoinColumn(name = "id_branch", nullable = false)
    private BranchEntity branch;

    @ManyToOne
    @JoinColumn(name = "id_department", nullable = false)
    private DepartmentEntity department;

    @ManyToOne
    @JoinColumn(name = "id_position", nullable = false)
    private PositionEntity position;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @PrePersist
    private void prePersist() {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
    }
}
