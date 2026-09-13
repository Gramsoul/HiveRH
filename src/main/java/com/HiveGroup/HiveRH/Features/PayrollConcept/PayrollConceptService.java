package com.HiveGroup.HiveRH.Features.PayrollConcept;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.PayrollConcept.DTO.PayrollConceptFilterDTO;
import com.HiveGroup.HiveRH.Features.PayrollConcept.DTO.PayrollConceptPatchDTO;
import com.HiveGroup.HiveRH.Features.PayrollConcept.DTO.PayrollConceptRequestDTO;
import com.HiveGroup.HiveRH.Features.PayrollConcept.DTO.PayrollConceptResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PayrollConceptService {

    private final PayrollConceptRepository payrollConceptRepository;

    @Transactional(readOnly = true)
    public List<PayrollConceptResponseDTO> findAll(PayrollConceptFilterDTO filters) {
        PayrollConceptFilterDTO activeFilters = filters != null
                ? filters
                : new PayrollConceptFilterDTO(null, null, null);

        return payrollConceptRepository.findAll()
                .stream()
                .filter(concept -> activeFilters.name() == null || containsIgnoreCase(concept.getName(), activeFilters.name()))
                .filter(concept -> activeFilters.type() == null || concept.getType() == activeFilters.type())
                .filter(concept -> activeFilters.active() == null || concept.isActive() == activeFilters.active())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PayrollConceptResponseDTO findById(Long id) {
        return toResponse(findConceptById(id));
    }

    @Transactional
    public PayrollConceptResponseDTO create(PayrollConceptRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de concepto de liquidación es obligatoria");
        }

        validateName(request.name());

        if (request.type() == null) {
            throw new IllegalArgumentException("El tipo de concepto es obligatorio");
        }

        PayrollConceptEntity concept = PayrollConceptEntity.builder()
                .name(request.name().trim())
                .description(normalizeDescription(request.description()))
                .type(request.type())
                .active(request.active() == null || request.active())
                .build();

        return toResponse(payrollConceptRepository.save(concept));
    }

    @Transactional
    public PayrollConceptResponseDTO patchById(Long id, PayrollConceptPatchDTO request) {
        validatePatchRequest(request);

        PayrollConceptEntity concept = findConceptById(id);

        if (request.name() != null) {
            validateName(request.name());
            concept.setName(request.name().trim());
        }

        if (request.description() != null) {
            concept.setDescription(normalizeDescription(request.description()));
        }

        if (request.type() != null) {
            concept.setType(request.type());
        }

        if (request.active() != null) {
            concept.setActive(request.active());
        }

        return toResponse(payrollConceptRepository.save(concept));
    }

    @Transactional
    public PayrollConceptResponseDTO deactivate(Long id) {
        PayrollConceptEntity concept = findConceptById(id);
        concept.setActive(false);

        return toResponse(payrollConceptRepository.save(concept));
    }

    private PayrollConceptEntity findConceptById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El concepto es obligatorio");
        }

        return payrollConceptRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Concepto de liquidación no encontrado", "PayrollConcept"));
    }

    private PayrollConceptResponseDTO toResponse(PayrollConceptEntity concept) {
        return new PayrollConceptResponseDTO(
                concept.getId_payroll_concept(),
                concept.getName(),
                concept.getDescription(),
                concept.getType(),
                concept.isActive()
        );
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del concepto es obligatorio");
        }
    }

    private void validatePatchRequest(PayrollConceptPatchDTO request) {
        if (request == null || !request.isAnyFieldPresent()) {
            throw new IllegalArgumentException("Debe enviar al menos un campo para actualizar");
        }
    }

    private String normalizeDescription(String description) {
        return description == null || description.isBlank()
                ? null
                : description.trim();
    }

    private boolean containsIgnoreCase(String value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }

        return value != null && value.toLowerCase().contains(filter.trim().toLowerCase());
    }
}
