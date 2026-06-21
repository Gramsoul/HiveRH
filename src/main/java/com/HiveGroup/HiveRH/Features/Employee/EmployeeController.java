package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.Employee.DTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Employees", description = "Gestion de empleados, perfiles y bajas logicas.")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/me")
    @Operation(summary = "Consultar mi empleado", description = "Devuelve el empleado asociado a la cuenta autenticada.")
    public ResponseEntity<EmployeeResponseDTO> getCurrentEmployee(){
        return ResponseEntity.ok(employeeService.findCurrentEmployee());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityAuthorizationService.canAccessEmployee(#id)")
    @Operation(summary = "Consultar empleado por ID", description = "Obtiene el detalle de un empleado especifico. La autorizacion valida si el usuario puede acceder a ese empleado.")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@P("id") @NonNull @PathVariable Long id){
        EmployeeResponseDTO employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    @Operation(summary = "Listar empleados", description = "Lista empleados y permite filtrar por datos personales, sucursal, fechas, estado, puesto, departamento o rango salarial.")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployees(EmployeeFilterDTO filters)
    {
        return ResponseEntity.ok(employeeService.findAllbyFilter(filters));
    }

    @GetMapping("/page")
    @Operation(summary = "Listar empleados paginados", description = "Devuelve empleados en formato paginado utilizando los parametros Pageable de Spring.")
    public ResponseEntity<PageResponseDTO<EmployeeResponseDTO>> getAllPageable(Pageable pageable){
        return ResponseEntity.ok(employeeService.getAllPage(pageable));
    }

    @PostMapping
    @Operation(summary = "Crear empleado", description = "Registra un empleado activo y crea automaticamente una cuenta EMPLOYEE asociada con credenciales iniciales.")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@NonNull @RequestBody EmployeeCreateDTO employeeCreateDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(employeeCreateDTO));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar empleado parcialmente", description = "Modifica solo los campos enviados en el request.")
    public ResponseEntity<EmployeeResponseDTO> patchEmployee(@NonNull @PathVariable Long id,@NonNull @RequestBody EmployeeUpdateDTO employeeUpdateDTO){
        return  ResponseEntity.ok(employeeService.patchById(id, employeeUpdateDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar empleado", description = "Actualiza datos del empleado usando la misma logica parcial del PATCH actual.")
    public ResponseEntity<EmployeeResponseDTO> putEmployee(@NonNull @PathVariable Long id, @NonNull @RequestBody EmployeeUpdateDTO employeeUpdateDTO)
    {
        return ResponseEntity.ok(employeeService.patchById(id, employeeUpdateDTO));
    }

    @DeleteMapping("/{dni}")
    @Operation(summary = "Dar de baja empleado", description = "Realiza una baja logica del empleado por DNI, cambiando su estado a TERMINATED.")
    public ResponseEntity<EmployeeResponseDTO> deleteEmployee(@NonNull @PathVariable String dni){
        return ResponseEntity.ok(employeeService.deleteByDni(dni));
    }

}
