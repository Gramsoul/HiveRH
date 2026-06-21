package com.HiveGroup.HiveRH.Features.Branch.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BranchUpdateDTO(

        @JsonAlias("branchName")
        @NotBlank(message = "El nombre de la sucursal es obligatorio")
        @Size(max = 100, message = "El nombre de la sucursal no puede superar los 100 caracteres")
        String name,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
        String city,

        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 100, message = "La direcciÃ³n no puede superar los 100 caracteres")
        String address,

        Boolean active
) {
}