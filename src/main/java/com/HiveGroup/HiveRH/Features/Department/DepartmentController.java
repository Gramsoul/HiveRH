package com.HiveGroup.HiveRH.Features.Department;

import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentFilterDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentRequestDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
@AllArgsConstructor
@Tag(name = "Departments", description = "Administracion de departamentos internos.")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Listar departamentos", description = "Obtiene departamentos y permite filtrar por ID, nombre y estado activo.")
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartments(DepartmentFilterDTO filters) {
        return ResponseEntity.ok(departmentService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Crear departamento", description = "Registra un nuevo departamento interno de la empresa.")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@NonNull @RequestBody DepartmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @DeleteMapping("/{id_department}")
    @Operation(summary = "Dar de baja departamento", description = "Realiza una baja logica del departamento, manteniendo el registro en la base de datos.")
    public ResponseEntity<DepartmentResponseDTO> deleteDepartment(@NonNull @PathVariable("id_department") Long idDepartment) {
        return ResponseEntity.ok(departmentService.deleteById(idDepartment));
    }
}
