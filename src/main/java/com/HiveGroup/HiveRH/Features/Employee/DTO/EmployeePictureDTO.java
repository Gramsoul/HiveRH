package com.HiveGroup.HiveRH.Features.Employee.DTO;

import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

public record EmployeePictureDTO(
        @NonNull
        String dni,
        @NonNull
        MultipartFile file
) {
}
