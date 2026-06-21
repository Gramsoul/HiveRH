package com.HiveGroup.HiveRH.Features.Branch;

import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchCreateDTO;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchResponseDTO;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/branch")
@AllArgsConstructor
@Tag(name = "Branches", description = "Administracion de sucursales de la empresa.")
public class BranchController {
    private final BranchService branchService;

    @GetMapping
    @Operation(summary = "Listar sucursales", description = "Obtiene las sucursales activas registradas en la empresa.")
    public ResponseEntity<List<BranchResponseDTO>> getBranches() {
        return ResponseEntity.ok(branchService.findAll());
    }

    @PostMapping
    @Operation(summary = "Crear sucursal", description = "Registra una nueva sucursal con nombre, ciudad y direccion.")
    public ResponseEntity<BranchResponseDTO> createBranch(@Valid @RequestBody BranchCreateDTO branchCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.create(branchCreateDTO));
    }

    @PutMapping("/{id_branch}")
    @Operation(summary = "Actualizar sucursal", description = "Modifica los datos de una sucursal existente.")
    public ResponseEntity<BranchResponseDTO> updateBranch(
            @PathVariable("id_branch") @Positive(message = "El ID de la sucursal debe ser mayor que cero") Long idBranch,
            @Valid @RequestBody BranchUpdateDTO branchUpdateDTO) {
        return ResponseEntity.ok(branchService.putById(idBranch, branchUpdateDTO));
    }

    @DeleteMapping("/{id_branch}")
    @Operation(summary = "Dar de baja sucursal", description = "Realiza una baja logica de la sucursal, marcandola como inactiva sin eliminarla fisicamente.")
    public ResponseEntity<BranchResponseDTO> deleteBranch(
            @NonNull @PathVariable("id_branch") @Positive(message = "El ID de la sucursal debe ser mayor que cero") Long idBranch) {
        return ResponseEntity.ok(branchService.deleteById(idBranch));
    }
}
