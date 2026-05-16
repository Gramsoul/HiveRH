package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Features.Employee.DTO.EmployeeFilterDTO;
import com.HiveGroup.HiveRH.Features.Employee.DTO.EmployeeCreateDTO;
import com.HiveGroup.HiveRH.Features.Employee.DTO.EmployeeResponseDTO;
import com.HiveGroup.HiveRH.Features.Employee.DTO.EmployeeUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@NonNull @PathVariable Long id){
        EmployeeResponseDTO employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployees(EmployeeFilterDTO filters)
    {
        return ResponseEntity.ok(employeeService.findAllbyFilter(filters));
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
