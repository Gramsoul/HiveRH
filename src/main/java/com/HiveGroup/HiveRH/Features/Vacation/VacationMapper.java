package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class VacationMapper {

    public VacationEntity toEntity(VacationRequest request, EmployeeEntity employee) {

        return VacationEntity.builder()
                .requestDate(
                        request.requestDate() != null
                                ? request.requestDate()
                                : LocalDate.now()
                )
                .isAccepted(request.accepted())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .isPaid(request.paid())
                .employee(employee)
                .build();
    }

    public VacationResponse toResponse(VacationEntity vacation) {

        return new VacationResponse(
                vacation.getId_vacation(),
                vacation.getRequestDate(),
                vacation.isAccepted(),
                vacation.getStartDate(),
                vacation.getEndDate(),
                vacation.isPaid(),
                vacation.getEmployee().getId_employee(),
                vacation.getEmployee().getName() + " " + vacation.getEmployee().getLastName()
        );
    }

    public List<VacationResponse> toResponseList(List<VacationEntity> vacations) {

        return vacations.stream()
                .map(this::toResponse)
                .toList();
    }
}