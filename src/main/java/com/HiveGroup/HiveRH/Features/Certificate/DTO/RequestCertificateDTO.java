package com.HiveGroup.HiveRH.Features.Certificate.DTO;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record RequestCertificateDTO(
        Long id,
        String description,
        MultipartFile file
) {
}
