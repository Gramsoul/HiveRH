package com.HiveGroup.HiveRH.Features.Department;

import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentFilterDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentRequestDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
@AllArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartments(DepartmentFilterDTO filters) {
        return ResponseEntity.ok(departmentService.findAllByFilter(filters));
    }

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@NonNull @Valid @RequestBody DepartmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @DeleteMapping("/{id_department}")
    public ResponseEntity<DepartmentResponseDTO> deleteDepartment(@NonNull @PathVariable("id_department") Long idDepartment) {
        return ResponseEntity.ok(departmentService.deleteById(idDepartment));
    }
}
