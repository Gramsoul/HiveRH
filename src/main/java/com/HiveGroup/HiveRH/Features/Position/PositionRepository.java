package com.HiveGroup.HiveRH.Features.Position;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<@NonNull PositionEntity, @NonNull Long> {
}
