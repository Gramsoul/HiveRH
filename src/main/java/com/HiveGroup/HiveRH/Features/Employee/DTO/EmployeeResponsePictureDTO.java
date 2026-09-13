package com.HiveGroup.HiveRH.Features.Employee.DTO;

import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

public record EmployeeResponsePictureDTO(
        @NonNull
        String dni,
        @NonNull
        byte[] file
) {
}
