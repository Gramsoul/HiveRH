package com.HiveGroup.HiveRH.Features.Variation;

import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationRequest;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationResponse;
import org.springframework.stereotype.Component;

@Component
public class VariationMapper {

    public VariationEntity toEntity(VariationRequest request) {
        return VariationEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .total(request.getTotal())
                .build();
    }

    public VariationResponse toResponse(VariationEntity variation) {
        return VariationResponse.builder()
                .idVariation(variation.getId_variation())
                .title(variation.getTitle())
                .description(variation.getDescription())
                .total(variation.getTotal())
                .build();
    }
}