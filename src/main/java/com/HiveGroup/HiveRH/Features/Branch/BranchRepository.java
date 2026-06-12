package com.HiveGroup.HiveRH.Features.Branch;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<@NonNull BranchEntity, @NonNull Long> {
    List<BranchEntity> findAllByIsActiveTrue();
}
