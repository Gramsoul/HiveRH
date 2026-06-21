package com.HiveGroup.HiveRH.Features.Branch;

import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchCreateDTO;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchResponseDTO;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<BranchResponseDTO> createBranch(@NonNull @Valid @RequestBody BranchCreateDTO branchCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.create(branchCreateDTO));
    }

    @PutMapping("/{id_branch}")
    @Operation(summary = "Actualizar sucursal", description = "Modifica los datos de una sucursal existente.")
    public ResponseEntity<BranchResponseDTO> updateBranch(@NonNull @PathVariable("id_branch") Long idBranch,
                                                          @NonNull @Valid @RequestBody BranchUpdateDTO branchUpdateDTO) {
        return ResponseEntity.ok(branchService.putById(idBranch, branchUpdateDTO));
    }

    @DeleteMapping("/{id_branch}")
    @Operation(summary = "Dar de baja sucursal", description = "Realiza una baja logica de la sucursal, marcandola como inactiva sin eliminarla fisicamente.")
    public ResponseEntity<BranchResponseDTO> deleteBranch(@NonNull @PathVariable("id_branch") Long idBranch) {
        return ResponseEntity.ok(branchService.deleteById(idBranch));
    }
}
