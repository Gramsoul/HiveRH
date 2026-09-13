package com.HiveGroup.HiveRH.Features.Certificate.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record RequestCertificateDTO(

        @NotNull(message = "El ID de la licencia es obligatorio")
        @Positive(message = "El ID de la licencia debe ser mayor que cero")
        Long idLicense,

        @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
        String description,

        @NotNull(message = "El archivo del certificado es obligatorio")
        MultipartFile file
) {
}
