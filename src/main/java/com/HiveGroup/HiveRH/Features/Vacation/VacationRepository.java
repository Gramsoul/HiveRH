package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<@NonNull VacationEntity, @NonNull Long> {

    // Busca las vacaciones de un empleado.
    // La validación de fechas superpuestas se hace en el service.
    List<VacationEntity> findByEmployee(EmployeeEntity employee);
}