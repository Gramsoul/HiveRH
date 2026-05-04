package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_account;

    @Column(name = "user", nullable = false, length = 100)
    private String user;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @OneToOne(mappedBy = "account")
    private EmployeeEntity eployee;
}
