package com.HiveGroup.HiveRH.Features.Variation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariationRepository extends JpaRepository<VariationEntity, Long> {
}