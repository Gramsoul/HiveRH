package com.HiveGroup.HiveRH.Features.Branch.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BranchUpdateDTO(

        @JsonAlias("branchName")
        @NotBlank(message = "El nombre de la sucursal es obligatorio")
        @Size(
                max = 100,
                message = "El nombre de la sucursal no puede superar los 100 caracteres"
        )
        String name,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(
                max = 100,
                message = "La ciudad no puede superar los 100 caracteres"
        )
        @Pattern(
                regexp = "^[\\p{L}0-9]+(?:[ .,'\\-][\\p{L}0-9]+)*$",
                message = "La ciudad contiene caracteres inválidos"
        )
        String city,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(
                min = 3,
                max = 100,
                message = "La dirección debe tener entre 3 y 100 caracteres"
        )
        String address,

        @NotNull(message = "El estado de la sucursal es obligatorio")
        Boolean active
) {
}