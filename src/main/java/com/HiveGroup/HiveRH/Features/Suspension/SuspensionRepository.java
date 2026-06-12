package com.HiveGroup.HiveRH.Features.Suspension;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuspensionRepository extends JpaRepository<@NonNull SuspensionEntity, @NonNull Long> {
}
