package com.HiveGroup.HiveRH.Features.Payroll;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PayrollRepository extends JpaRepository<@NonNull PayrollEntity, @NonNull Long> {

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM PayrollEntity p
            WHERE p.employee.id_employee = :idEmployee
            AND p.payrollDate BETWEEN :startDate AND :endDate
            """)
    boolean existsPayrollInMonth(
            @Param("idEmployee") Long idEmployee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}