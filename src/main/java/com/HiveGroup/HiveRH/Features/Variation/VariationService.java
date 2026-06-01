package com.HiveGroup.HiveRH.Features.Variation;

import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationFilterDTO;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationRequest;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VariationService {

    private final VariationRepository variationRepository;
    private final VariationMapper variationMapper;

    public VariationResponse create(VariationRequest request) {

        validateVariation(request.getTotal());

        VariationEntity variation = variationMapper.toEntity(request);

        VariationEntity savedVariation = variationRepository.save(variation);

        return variationMapper.toResponse(savedVariation);
    }

    public VariationResponse findById(Long id) {
        VariationEntity variation = variationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variación no encontrada"));

        return variationMapper.toResponse(variation);
    }

    public List<VariationResponse> findAllByFilter(VariationFilterDTO filters) {
        return variationRepository.findAll()
                .stream()
                .filter(variation -> filters.getTitle() == null ||
                        variation.getTitle().toLowerCase().contains(filters.getTitle().toLowerCase()))
                .filter(variation -> filters.getMinTotal() == null ||
                        variation.getTotal() >= filters.getMinTotal())
                .filter(variation -> filters.getMaxTotal() == null ||
                        variation.getTotal() <= filters.getMaxTotal())
                .map(variationMapper::toResponse)
                .toList();
    }

    public VariationResponse patchById(Long id, VariationRequest request) {
        VariationEntity variation = variationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variación no encontrada"));

        if (request.getTitle() != null) {
            variation.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            variation.setDescription(request.getDescription());
        }

        if (request.getTotal() != null) {
            validateVariation(request.getTotal());
            variation.setTotal(request.getTotal());
        }

        VariationEntity updatedVariation = variationRepository.save(variation);

        return variationMapper.toResponse(updatedVariation);
    }

    public VariationResponse putById(Long id, VariationRequest request) {
        VariationEntity variation = variationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variación no encontrada"));

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("El título es obligatorio");
        }

        if (request.getTotal() == null) {
            throw new RuntimeException("El total es obligatorio");
        }

        validateVariation(request.getTotal());

        variation.setTitle(request.getTitle());
        variation.setDescription(request.getDescription());
        variation.setTotal(request.getTotal());

        VariationEntity updatedVariation = variationRepository.save(variation);

        return variationMapper.toResponse(updatedVariation);
    }

    public VariationResponse deleteById(Long id) {
        VariationEntity variation = variationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variación no encontrada"));

        variationRepository.delete(variation);

        return variationMapper.toResponse(variation);
    }

    private void validateVariation(Double total) {
        if (total == null) {
            throw new RuntimeException("El total es obligatorio");
        }

        if (total == 0) {
            throw new RuntimeException("El total de la variación no puede ser cero");
        }
    }
}