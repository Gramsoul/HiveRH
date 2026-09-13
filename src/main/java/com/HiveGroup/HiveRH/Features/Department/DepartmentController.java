package com.HiveGroup.HiveRH.Features.Department;

import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentFilterDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentRequestDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentResponseDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentStatusRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@AllArgsConstructor
@Validated
@Tag(name = "04 Departments", description = "Internal department management.")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "List departments", description = "Returns departments and supports filtering by ID, name, and active status.")
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartments(@ParameterObject @Valid DepartmentFilterDTO filters) {
        return ResponseEntity.ok(departmentService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Create department", description = "Registers a new internal company department.")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @PutMapping("/{id_department}")
    @Operation(summary = "Update department", description = "Updates the main data of an existing department.")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(
            @NonNull @PathVariable("id_department") @Positive(message = "El ID del departamento debe ser mayor que cero") Long idDepartment,
            @Valid @RequestBody DepartmentRequestDTO request) {
        return ResponseEntity.ok(departmentService.updateById(idDepartment, request));
    }

    @PatchMapping("/{id_department}/status")
    @Operation(summary = "Update department status", description = "Activates or deactivates a department using logical status changes.")
    public ResponseEntity<DepartmentResponseDTO> updateDepartmentStatus(
            @NonNull @PathVariable("id_department") @Positive(message = "El ID del departamento debe ser mayor que cero") Long idDepartment,
            @Valid @RequestBody DepartmentStatusRequestDTO request) {
        return ResponseEntity.ok(departmentService.updateStatus(idDepartment, request.active()));
    }
}
