package com.HiveGroup.HiveRH.Features.PayrollPeriod;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollPeriodStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollStatus;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Payroll.PayrollRepository;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.DTO.PayrollPeriodCreateDTO;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.DTO.PayrollPeriodFilterDTO;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.DTO.PayrollPeriodResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PayrollPeriodService {

    private final PayrollPeriodRepository payrollPeriodRepository;
    private final PayrollRepository payrollRepository;

    @Transactional(readOnly = true)
    public List<PayrollPeriodResponseDTO> findAll(PayrollPeriodFilterDTO filters) {
        PayrollPeriodFilterDTO activeFilters = filters != null
                ? filters
                : new PayrollPeriodFilterDTO(null, null, null);

        return payrollPeriodRepository.findAll()
                .stream()
                .filter(period -> activeFilters.month() == null || period.getMonth().equals(activeFilters.month()))
                .filter(period -> activeFilters.year() == null || period.getYear().equals(activeFilters.year()))
                .filter(period -> activeFilters.status() == null || period.getStatus() == activeFilters.status())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PayrollPeriodResponseDTO findById(Long id) {
        return toResponse(findPeriodById(id));
    }

    @Transactional
    public PayrollPeriodResponseDTO create(PayrollPeriodCreateDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de período de liquidación es obligatoria");
        }

        validateMonthAndYear(request.month(), request.year());

        payrollPeriodRepository.findByMonthAndYear(request.month(), request.year())
                .ifPresent(period -> {
                    throw new IllegalArgumentException("Ya existe un período de liquidación para ese mes y año");
                });

        PayrollPeriodEntity period = PayrollPeriodEntity.builder()
                .month(request.month())
                .year(request.year())
                .status(PayrollPeriodStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(payrollPeriodRepository.save(period));
    }

    @Transactional
    public PayrollPeriodResponseDTO close(Long id) {
        PayrollPeriodEntity period = findPeriodById(id);

        if (period.getStatus() == PayrollPeriodStatus.CLOSED) {
            throw new IllegalArgumentException("El período ya está cerrado");
        }

        if (payrollRepository.existsByPeriodAndStatus(period, PayrollStatus.DRAFT)) {
            throw new IllegalArgumentException("No se puede cerrar un período con liquidaciones en borrador");
        }

        period.setStatus(PayrollPeriodStatus.CLOSED);
        period.setClosedAt(LocalDateTime.now());

        return toResponse(payrollPeriodRepository.save(period));
    }

    private PayrollPeriodEntity findPeriodById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El período de liquidación es obligatorio");
        }

        return payrollPeriodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Período de liquidación no encontrado", "PayrollPeriod"));
    }

    private void validateMonthAndYear(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }

        if (year == null || year < 2000) {
            throw new IllegalArgumentException("El año debe ser mayor o igual a 2000");
        }
    }

    private PayrollPeriodResponseDTO toResponse(PayrollPeriodEntity period) {
        return new PayrollPeriodResponseDTO(
                period.getId_payroll_period(),
                period.getMonth(),
                period.getYear(),
                period.getStatus(),
                period.getCreatedAt(),
                period.getClosedAt()
        );
    }
}
