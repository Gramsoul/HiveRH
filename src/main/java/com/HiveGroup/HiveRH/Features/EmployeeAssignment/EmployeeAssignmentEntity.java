package com.HiveGroup.HiveRH.Features.EmployeeAssignment;

import com.HiveGroup.HiveRH.Features.Department.DepartmentEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Position.PositionEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_department_position")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne
    @JoinColumn(name = "id_department", nullable = false)
    private DepartmentEntity department;

    @ManyToOne
    @JoinColumn(name = "id_position", nullable = false)
    private PositionEntity position;


}
