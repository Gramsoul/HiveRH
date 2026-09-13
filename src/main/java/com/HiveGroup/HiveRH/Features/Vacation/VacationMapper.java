package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", imports = {AbsenceStatus.class, LocalDate.class})
public interface VacationMapper {

    @Mapping(target = "id_vacation", ignore = true)
    @Mapping(target = "requestDate", expression = "java(request.requestDate() != null ? request.requestDate() : LocalDate.now())")
    @Mapping(target = "status", expression = "java(request.status() != null ? request.status() : AbsenceStatus.PENDING)")
    @Mapping(source = "request.startDate", target = "startDate")
    @Mapping(source = "request.endDate", target = "endDate")
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(source = "request.reviewComment", target = "reviewComment")
    @Mapping(source = "employee", target = "employee")
    VacationEntity toEntity(VacationRequest request, EmployeeEntity employee);

    @Mapping(source = "id_vacation", target = "idVacation")
    @Mapping(source = "employee.dni", target = "dniEmployee")
    @Mapping(source = "reviewedBy.id_account", target = "reviewedByAccountId")
    @Mapping(target = "employeeName", expression = "java(getEmployeeName(vacation))")
    VacationResponse toResponse(VacationEntity vacation);

    List<VacationResponse> toResponseList(List<VacationEntity> vacations);

    default String getEmployeeName(VacationEntity vacation) {
        EmployeeEntity employee = vacation.getEmployee();
        return employee.getName() + " " + employee.getLastName();
    }
}
