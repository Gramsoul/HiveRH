package com.HiveGroup.HiveRH.Features.Payroll;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<@NonNull PayrollEntity, @NonNull Long> {
}
