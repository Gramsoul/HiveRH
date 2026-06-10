package com.HiveGroup.HiveRH.Features.Variation;

import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationFilterDTO;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationRequest;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariationService {

    private final VariationRepository variationRepository;
    private final VariationMapper variationMapper;

    // Crear una variación
    @Transactional
    public VariationResponse create(VariationRequest request) {

        validateTitle(request.getTitle());
        validateVariation(request.getTotal());

        VariationEntity variation = variationMapper.toEntity(request);

        VariationEntity savedVariation = variationRepository.save(variation);

        return variationMapper.toResponse(savedVariation);
    }

    // Buscar una variación por ID
    @Transactional(readOnly = true)
    public VariationResponse findById(Long id) {

        VariationEntity variation = findVariationById(id);

        return variationMapper.toResponse(variation);
    }

    // Listar variaciones con filtros
    @Transactional(readOnly = true)
    public List<VariationResponse> findAllByFilter(VariationFilterDTO filters) {

        return variationRepository.findAll()
                .stream()
                .filter(variation -> filterByTitle(variation, filters.getTitle()))
                .filter(variation -> filterByMinTotal(variation, filters.getMinTotal()))
                .filter(variation -> filterByMaxTotal(variation, filters.getMaxTotal()))
                .map(variationMapper::toResponse)
                .toList();
    }

    // Actualizar parcialmente una variación
    @Transactional
    public VariationResponse patchById(Long id, VariationRequest request) {

        VariationEntity variation = findVariationById(id);

        if (request.getTitle() != null) {
            validateTitle(request.getTitle());
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

    // Reemplazar completamente una variación
    @Transactional
    public VariationResponse putById(Long id, VariationRequest request) {

        VariationEntity variation = findVariationById(id);

        validateTitle(request.getTitle());
        validateVariation(request.getTotal());

        variation.setTitle(request.getTitle());
        variation.setDescription(request.getDescription());
        variation.setTotal(request.getTotal());

        VariationEntity updatedVariation = variationRepository.save(variation);

        return variationMapper.toResponse(updatedVariation);
    }

    // Eliminar una variación
    @Transactional
    public VariationResponse deleteById(Long id) {

        VariationEntity variation = findVariationById(id);

        VariationResponse response = variationMapper.toResponse(variation);

        variationRepository.delete(variation);

        return response;
    }

    // Buscar entidad o lanzar error
    private VariationEntity findVariationById(Long id) {

        return variationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variación no encontrada"));
    }

    // Validar título
    private void validateTitle(String title) {

        if (title == null || title.isBlank()) {
            throw new RuntimeException("El título es obligatorio");
        }
    }

    // Validar total
    private void validateVariation(Double total) {

        if (total == null) {
            throw new RuntimeException("El total es obligatorio");
        }

        if (total == 0) {
            throw new RuntimeException("El total de la variación no puede ser cero");
        }
    }

    // Filtrar por título
    private boolean filterByTitle(VariationEntity variation, String title) {

        if (title == null || title.isBlank()) {
            return true;
        }

        return variation.getTitle()
                .toLowerCase()
                .contains(title.toLowerCase());
    }

    // Filtrar por total mínimo
    private boolean filterByMinTotal(VariationEntity variation, Double minTotal) {

        if (minTotal == null) {
            return true;
        }

        return variation.getTotal() >= minTotal;
    }

    // Filtrar por total máximo
    private boolean filterByMaxTotal(VariationEntity variation, Double maxTotal) {

        if (maxTotal == null) {
            return true;
        }

        return variation.getTotal() <= maxTotal;
    }
}