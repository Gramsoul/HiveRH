package com.HiveGroup.HiveRH.Features.Position.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

public record PositionRequestDTO(
        @JsonAlias("positionName")
        String name
) {
}
