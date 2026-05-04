package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Features.Position.PositionEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<@NonNull PositionEntity, @NonNull Long> {
}
