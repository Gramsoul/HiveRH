package com.HiveGroup.HiveRH.Features.Department;

import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "department")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_department;

    @Column(name = "name", nullable = false, length = 100)
    private String departmentName;

    @Column(name = "active")
    private boolean isActive;

    //Assignment
    @OneToMany(mappedBy = "department")
    private List<EmployeeAssignmentEntity> assignments;

}
