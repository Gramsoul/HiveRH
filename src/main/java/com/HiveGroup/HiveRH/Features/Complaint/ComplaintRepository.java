package com.HiveGroup.HiveRH.Features.Complaint;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<@NonNull ComplaintEntity, @NonNull Long> {
}
