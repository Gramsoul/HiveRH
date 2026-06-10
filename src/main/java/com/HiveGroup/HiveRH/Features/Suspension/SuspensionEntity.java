package com.HiveGroup.HiveRH.Features.Suspension;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "suspension")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SuspensionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_suspension;

    @Column(name = "motive", nullable = false, length = 300)
    private String motive;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;
}
