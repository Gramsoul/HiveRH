package com.HiveGroup.HiveRH.Features.WorkRequest.DTO;

import jakarta.validation.constraints.Size;

public record WorkRequestReviewDTO(
        @Size(max = 500, message = "El comentario de revision no puede superar los 500 caracteres")
        String reviewComment
) {
}
