package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Services.FileLectorService;
import com.HiveGroup.HiveRH.Features.Employee.DTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/employees")
@AllArgsConstructor
@Tag(name = "06 Employees", description = "Employee records, profiles, and status management.")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final FileLectorService fileLectorService;

    @GetMapping("/me")
    @Operation(summary = "Get my employee profile", description = "Returns the employee profile associated with the authenticated account.")
    public ResponseEntity<EmployeeResponseDTO> getCurrentEmployee() {
        return ResponseEntity.ok(employeeService.findCurrentEmployee());
    }

    @GetMapping("/{dni}")
    @PreAuthorize("@securityAuthorizationService.canAccessEmployeeDni(#dni)")
    @Operation(summary = "Get employee by DNI", description = "Returns the details of a specific employee. Authorization validates whether the user can access that employee.")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeByDni(@P("dni") @NonNull @PathVariable String dni) {
        EmployeeResponseDTO employee = employeeService.findByDni(dni);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    @Operation(summary = "List employees", description = "Returns paginated employees and supports filtering by personal data, branch, dates, status, position, department, or salary range.")
    public ResponseEntity<PageResponseDTO<EmployeeResponseDTO>> getEmployees(
            @ParameterObject EmployeeFilterDTO filters,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(employeeService.findAllByFilter(filters, pageable));
    }

    @PostMapping
    @Operation(summary = "Create employee", description = "Registers an active employee, creates the first active labor assignment, and automatically creates a linked EMPLOYEE account with initial credentials.")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeCreateDTO employeeCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(employeeCreateDTO));
    }

    @PatchMapping("/{dni}")
    @Operation(summary = "Partially update employee", description = "Updates only the fields provided in the request. Assignment changes close the current active assignment and create a new one.")
    public ResponseEntity<EmployeeResponseDTO> patchEmployee(@PathVariable String dni, @Valid @RequestBody EmployeePatchDTO employeePatchDTO) {
        return ResponseEntity.ok(employeeService.patchByDni(dni, employeePatchDTO));
    }

    @PutMapping("/{dni}")
    @Operation(summary = "Update employee", description = "Updates employee data and keeps the current labor assignment in sync.")
    public ResponseEntity<EmployeeResponseDTO> putEmployee(@NonNull @PathVariable String dni, @Valid @RequestBody EmployeeUpdateDTO employeeUpdateDTO) {
        return ResponseEntity.ok(employeeService.putByDni(dni, employeeUpdateDTO));
    }

    @DeleteMapping("/{dni}")
    @Operation(summary = "Terminate employee", description = "Soft-deletes the employee by DNI, changing their status to TERMINATED and closing active assignments.")
    public ResponseEntity<EmployeeResponseDTO> deleteEmployee(@NonNull @PathVariable String dni) {
        return ResponseEntity.ok(employeeService.deleteByDni(dni));
    }

    @PatchMapping(  value = "/picture",
                    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PreAuthorize("@securityAuthorizationService.canAccessEmployeeDni(#dni)")
    public ResponseEntity<EmployeeResponsePictureDTO> loadProfilePicture(
            @ModelAttribute EmployeePictureDTO data) {
        return ResponseEntity.ok()
                .body(employeeService.savePicture(data.file(), data.dni()));
    }

    @GetMapping("/picture/{dni}")
    public ResponseEntity<byte[]> getPicture(@PathVariable @NonNull String dni) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(employeeService.loadPicture(dni));
    }
}
