package com.HiveGroup.HiveRH.Features.Suspension;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Common.Utils.TextSearchUtils;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionFilterDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionRequestDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionResponseDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SuspensionService {
    private final SuspensionRepository suspensionRepository;
    private final EmployeeRepository employeeRepository;

    public List<SuspensionResponseDTO> findAllByFilter(SuspensionFilterDTO filters) {
        SuspensionFilterDTO activeFilters = filters != null
                ? filters
                : new SuspensionFilterDTO(null, null, null, null);

        return suspensionRepository.findAll().stream()
                .filter(suspension -> activeFilters.id_employee() == null || suspension.getEmployee().getId_employee().equals(activeFilters.id_employee()))
                .filter(suspension -> matchesDateRange(suspension, activeFilters))
                .filter(suspension -> TextSearchUtils.matchesFullName(
                        suspension.getEmployee().getName(),
                        suspension.getEmployee().getLastName(),
                        activeFilters.fullName()
                ))
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public SuspensionResponseDTO create(SuspensionRequestDTO request) {
        if (request.id_employee() == null) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }
        if (request.motive() == null || request.motive().isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio");
        }
        if (request.start_date() == null || request.end_date() == null) {
            throw new IllegalArgumentException("Las fechas de suspensión son obligatorias");
        }
        if (request.end_date().isBefore(request.start_date())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        EmployeeEntity employee = employeeRepository.findById(request.id_employee())
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado", "Employee"));

        SuspensionEntity suspension = SuspensionEntity.builder()
                .employee(employee)
                .motive(request.motive())
                .startDate(request.start_date())
                .endDate(request.end_date())
                .build();

        employee.setStatus(StatusEnum.SUSPENDED);
        employeeRepository.save(employee);

        return toDTO(suspensionRepository.save(suspension));
    }

    private boolean matchesDateRange(SuspensionEntity suspension, SuspensionFilterDTO filters) {
        if (filters.start_date() == null && filters.end_date() == null) {
            return true;
        }

        boolean startsBeforeFilterEnd = filters.end_date() == null || !suspension.getStartDate().isAfter(filters.end_date());
        boolean endsAfterFilterStart = filters.start_date() == null || !suspension.getEndDate().isBefore(filters.start_date());

        return startsBeforeFilterEnd && endsAfterFilterStart;
    }

    private SuspensionResponseDTO toDTO(SuspensionEntity suspension) {
        return new SuspensionResponseDTO(
                suspension.getId_suspension(),
                suspension.getEmployee().getId_employee(),
                suspension.getEmployee().getName(),
                suspension.getEmployee().getLastName(),
                suspension.getMotive(),
                suspension.getStartDate(),
                suspension.getEndDate()
        );
    }
}
