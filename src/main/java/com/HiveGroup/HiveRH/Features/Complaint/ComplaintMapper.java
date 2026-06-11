package com.HiveGroup.HiveRH.Features.Complaint;

import com.HiveGroup.HiveRH.Features.Complaint.ComplaintStatusEnum;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintRequest;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintResponse;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ComplaintMapper {

    public ComplaintEntity toEntity(ComplaintRequest request, EmployeeEntity employee) {

        return ComplaintEntity.builder()
                .title(request.title())
                .description(request.description())
                .date(LocalDate.now())
                .status(ComplaintStatusEnum.PENDING)
                .employee(employee)
                .build();
    }

    public ComplaintResponse toResponse(ComplaintEntity complaint) {

        return new ComplaintResponse(
                complaint.getId_complaint(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getDate(),
                complaint.getStatus(),
                complaint.getEmployee().getId_employee(),
                complaint.getEmployee().getName() + " " + complaint.getEmployee().getLastName()
        );
    }

    public List<ComplaintResponse> toResponseList(List<ComplaintEntity> complaints) {

        return complaints.stream()
                .map(this::toResponse)
                .toList();
    }
}