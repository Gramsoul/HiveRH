package com.HiveGroup.HiveRH.Features.Vacation;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRepository extends JpaRepository<@NonNull VacationEntity, @NonNull Long> {
}
