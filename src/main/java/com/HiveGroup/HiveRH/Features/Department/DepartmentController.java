package com.HiveGroup.HiveRH.Features.Department;

import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentFilterDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentRequestDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/department")
@AllArgsConstructor
@Validated
@Tag(name = "Departments", description = "Administracion de departamentos internos.")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Listar departamentos", description = "Obtiene departamentos y permite filtrar por ID, nombre y estado activo.")
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartments(@Valid DepartmentFilterDTO filters) {
        return ResponseEntity.ok(departmentService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Crear departamento", description = "Registra un nuevo departamento interno de la empresa.")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @DeleteMapping("/{id_department}")
    @Operation(summary = "Dar de baja departamento", description = "Realiza una baja logica del departamento, manteniendo el registro en la base de datos.")
    public ResponseEntity<DepartmentResponseDTO> deleteDepartment(
            @NonNull @PathVariable("id_department") @Positive(message = "El ID del departamento debe ser mayor que cero") Long idDepartment) {
        return ResponseEntity.ok(departmentService.deleteById(idDepartment));
    }
}
