package com.HiveGroup.HiveRH.Features.Certificate;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<@NonNull Certificate, @NonNull Long> {
}
