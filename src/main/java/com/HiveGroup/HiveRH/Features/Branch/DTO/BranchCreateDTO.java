package com.HiveGroup.HiveRH.Features.Branch.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BranchCreateDTO(

        @JsonAlias("branchName")
        @NotBlank(message = "El nombre de la sucursal es obligatorio")
        @Size(
                max = 100,
                message = "El nombre de la sucursal no puede superar los 100 caracteres"
        )
        @Schema(description = "Branch display name.", example = "Downtown Branch")
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
        @Schema(description = "City where the branch is located.", example = "Cordoba")
        String city,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(
                min = 3,
                max = 100,
                message = "La dirección debe tener entre 3 y 100 caracteres"
        )
        @Schema(description = "Branch street address.", example = "Av. Colon 123")
        String address
) {
}
