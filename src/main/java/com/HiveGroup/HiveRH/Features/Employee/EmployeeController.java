package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.Employee.DTO.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/employees")
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/me")
    public ResponseEntity<EmployeeResponseDTO> getCurrentEmployee(){
        return ResponseEntity.ok(employeeService.findCurrentEmployee());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityAuthorizationService.canAccessEmployee(#id)")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@P("id") @NonNull @PathVariable Long id){
        EmployeeResponseDTO employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployees(EmployeeFilterDTO filters)
    {
        return ResponseEntity.ok(employeeService.findAllbyFilter(filters));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDTO<EmployeeResponseDTO>> getAllPageable(Pageable pageable){
        return ResponseEntity.ok(employeeService.getAllPage(pageable));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@NonNull @RequestBody EmployeeCreateDTO employeeCreateDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(employeeCreateDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> patchEmployee(@NonNull @PathVariable Long id,@NonNull @RequestBody EmployeeUpdateDTO employeeUpdateDTO){
        return  ResponseEntity.ok(employeeService.patchById(id, employeeUpdateDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> putEmployee(@NonNull @PathVariable Long id, @NonNull @RequestBody EmployeeUpdateDTO employeeUpdateDTO)
    {
        return ResponseEntity.ok(employeeService.patchById(id, employeeUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> deleteEmployee(@NonNull @PathVariable Long id){
        return ResponseEntity.ok(employeeService.deleteById(id));
    }

}
