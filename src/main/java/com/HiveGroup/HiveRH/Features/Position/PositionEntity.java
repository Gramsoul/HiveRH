package com.HiveGroup.HiveRH.Features.Position;

import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "position")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_position;

    @Column(name = "name", nullable = false, length = 100)
    private String positionName;

    @Column(name = "active")
    private boolean isActive;

    //Assginment
    @OneToMany(mappedBy = "position")
    private List<EmployeeAssignmentEntity> assignments;

}
