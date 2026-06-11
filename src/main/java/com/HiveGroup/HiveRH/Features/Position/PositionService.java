package com.HiveGroup.HiveRH.Features.Position;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionFilterDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionRequestDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;

    public List<PositionResponseDTO> findAllByFilter(PositionFilterDTO filters) {
        List<PositionEntity> positions = filters.id_department() == null
                ? positionRepository.findAll()
                : positionRepository.findDistinctByDepartmentId(filters.id_department());

        return positions.stream()
                .filter(position -> filterByName(position, filters.name()))
                .filter(position -> filterByActive(position, filters.active()))
                .map(this::toDTO)
                .toList();
    }

    public PositionResponseDTO create(PositionRequestDTO request) {
        validateName(request.name());

        PositionEntity position = PositionEntity.builder()
                .positionName(request.name())
                .isActive(true)
                .build();

        return toDTO(positionRepository.save(position));
    }

    public PositionResponseDTO deleteById(Long id) {
        PositionEntity position = findPositionById(id);

        position.setActive(false);

        return toDTO(positionRepository.save(position));
    }

    private PositionEntity findPositionById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Puesto no encontrado", "Position"));
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del puesto es obligatorio");
        }
    }

    private boolean filterByName(PositionEntity position, String name) {
        if (name == null || name.isBlank()) {
            return true;
        }

        return position.getPositionName().toLowerCase().contains(name.toLowerCase());
    }

    private boolean filterByActive(PositionEntity position, Boolean active) {
        return active == null || position.isActive() == active;
    }

    private PositionResponseDTO toDTO(PositionEntity position) {
        return new PositionResponseDTO(
                position.getId_position(),
                position.getPositionName(),
                position.isActive()
        );
    }
}
