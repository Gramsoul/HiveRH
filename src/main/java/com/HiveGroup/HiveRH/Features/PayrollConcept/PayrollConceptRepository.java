package com.HiveGroup.HiveRH.Features.PayrollConcept;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollConceptRepository extends JpaRepository<@NonNull PayrollConceptEntity, @NonNull Long> {
}
