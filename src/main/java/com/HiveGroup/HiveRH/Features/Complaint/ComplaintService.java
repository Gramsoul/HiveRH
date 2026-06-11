package com.HiveGroup.HiveRH.Features.Complaint;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintRequest;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintResponse;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintStatusRequest;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final EmployeeRepository employeeRepository;
    private final ComplaintMapper complaintMapper;


    @Transactional
    public ComplaintResponse create(ComplaintRequest request) {

        validateRequest(request);

        EmployeeEntity employee = findEmployeeById(request.idEmployee());

        validateEmployeeCanHaveComplaint(employee);

        ComplaintEntity complaint = complaintMapper.toEntity(request, employee);

        ComplaintEntity savedComplaint = complaintRepository.save(complaint);

        return complaintMapper.toResponse(savedComplaint);
    }


    @Transactional(readOnly = true)
    public ComplaintResponse findById(Long idComplaint) {

        ComplaintEntity complaint = findComplaintById(idComplaint);

        return complaintMapper.toResponse(complaint);
    }


    @Transactional(readOnly = true)
    public List<ComplaintResponse> findAll() {

        List<ComplaintEntity> complaints = complaintRepository.findAll();

        return complaintMapper.toResponseList(complaints);
    }


    @Transactional
    public ComplaintResponse updateStatus(Long idComplaint, ComplaintStatusRequest request) {

        validateStatus(request.status());

        ComplaintEntity complaint = findComplaintById(idComplaint);

        complaint.setStatus(request.status());

        ComplaintEntity updatedComplaint = complaintRepository.save(complaint);

        return complaintMapper.toResponse(updatedComplaint);
    }


    private ComplaintEntity findComplaintById(Long idComplaint) {

        return complaintRepository.findById(idComplaint)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Denuncia no encontrada",
                        "Complaint"
                ));
    }


    private EmployeeEntity findEmployeeById(Long idEmployee) {

        return employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Empleado no encontrado",
                        "Employee"
                ));
    }


    private void validateRequest(ComplaintRequest request) {

        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }

        if (request.description() == null || request.description().isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }

        if (request.idEmployee() == null) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }
    }


    private void validateEmployeeCanHaveComplaint(EmployeeEntity employee) {

        if (employee.getStatus() != StatusEnum.ACTIVE) {
            throw new IllegalArgumentException("No se puede registrar una denuncia para un empleado que no está activo");
        }
    }


    private void validateStatus(ComplaintStatusEnum status) {

        if (status == null) {
            throw new IllegalArgumentException("El estado de la denuncia es obligatorio");
        }
    }
}