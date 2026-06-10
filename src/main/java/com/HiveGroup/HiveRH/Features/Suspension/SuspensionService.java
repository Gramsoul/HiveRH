package com.HiveGroup.HiveRH.Features.Suspension;

import com.HiveGroup.HiveRH.Common.Utils.TextSearchUtils;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionFilterDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SuspensionService {
    private final SuspensionRepository suspensionRepository;

    public List<SuspensionResponseDTO> findAllByFilter(SuspensionFilterDTO filters) {
        return suspensionRepository.findAll().stream()
                .filter(suspension -> filters.id_employee() == null || suspension.getEmployee().getId_employee().equals(filters.id_employee()))
                .filter(suspension -> matchesDateRange(suspension, filters))
                .filter(suspension -> TextSearchUtils.matchesFullName(
                        suspension.getEmployee().getName(),
                        suspension.getEmployee().getLastName(),
                        filters.fullName()
                ))
                .map(this::toDTO)
                .toList();
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
