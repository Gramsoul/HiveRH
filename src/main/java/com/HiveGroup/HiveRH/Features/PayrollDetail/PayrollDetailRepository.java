package com.HiveGroup.HiveRH.Features.PayrollDetail;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollDetailRepository extends JpaRepository<@NonNull PayrollDetailEntity, @NonNull Long> {
}
