package com.HiveGroup.HiveRH.Features.Branch;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "branch")
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_branch;

    @Column(name = "name", nullable = false, length = 100)
    private String branchName;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "active")
    private boolean isActive;

    @OneToMany(mappedBy = "branch")
    private List<EmployeeEntity> employees;
}
