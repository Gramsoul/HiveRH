package com.HiveGroup.HiveRH.Features.License;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<@NonNull LicenseEntity, @NonNull Long> {
}
