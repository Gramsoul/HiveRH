package com.HiveGroup.HiveRH.Features.PayrollPeriod;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayrollPeriodRepository extends JpaRepository<@NonNull PayrollPeriodEntity, @NonNull Long> {
    Optional<PayrollPeriodEntity> findByMonthAndYear(Integer month, Integer year);
}
