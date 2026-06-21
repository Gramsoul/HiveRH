package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<@NonNull EmployeeEntity, @NonNull Long> {

    Optional<EmployeeEntity> findByDni(String dni);

    Optional<EmployeeEntity> findByAccount(AccountEntity account);
}