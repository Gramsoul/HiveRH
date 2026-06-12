package com.HiveGroup.HiveRH.Features.Department;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentFilterDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentRequestDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartmentService {
    private final DepartamentRepository departamentRepository;

    public List<DepartmentResponseDTO> findAllByFilter(DepartmentFilterDTO filters) {
        return departamentRepository.findAll().stream()
                .filter(department -> filterById(department, filters.id_department()))
                .filter(department -> filterByName(department, filters.name()))
                .filter(department -> filterByActive(department, filters.active()))
                .map(this::toDTO)
                .toList();
    }

    public DepartmentResponseDTO create(DepartmentRequestDTO request) {
        validateName(request.name());

        DepartmentEntity department = DepartmentEntity.builder()
                .departmentName(request.name())
                .isActive(true)
                .build();

        return toDTO(departamentRepository.save(department));
    }

    public DepartmentResponseDTO deleteById(Long id) {
        DepartmentEntity department = findDepartmentById(id);

        department.setActive(false);

        return toDTO(departamentRepository.save(department));
    }

    private DepartmentEntity findDepartmentById(Long id) {
        return departamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado", "Department"));
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio");
        }
    }

    private boolean filterById(DepartmentEntity department, Long idDepartment) {
        return idDepartment == null || department.getId_department().equals(idDepartment);
    }

    private boolean filterByName(DepartmentEntity department, String name) {
        if (name == null || name.isBlank()) {
            return true;
        }

        return department.getDepartmentName().toLowerCase().contains(name.toLowerCase());
    }

    private boolean filterByActive(DepartmentEntity department, Boolean active) {
        return active == null || department.isActive() == active;
    }

    private DepartmentResponseDTO toDTO(DepartmentEntity department) {
        return new DepartmentResponseDTO(
                department.getId_department(),
                department.getDepartmentName(),
                department.isActive()
        );
    }
}
