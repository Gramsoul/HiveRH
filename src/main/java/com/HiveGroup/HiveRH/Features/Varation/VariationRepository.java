package com.HiveGroup.HiveRH.Features.Varation;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariationRepository extends JpaRepository<@NonNull VariationEntity, @NonNull Long> {
}
