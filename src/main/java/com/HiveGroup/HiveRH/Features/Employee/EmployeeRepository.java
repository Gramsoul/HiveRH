package com.HiveGroup.HiveRH.Features.Employee;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<@NonNull EmployeeEntity, @NonNull Long>{

    boolean existsByDni(String dni);

    Optional<EmployeeEntity> findByDni(String dni);

}
