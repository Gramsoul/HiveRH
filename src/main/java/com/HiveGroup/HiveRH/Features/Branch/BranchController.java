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
@RequestMapping("/api/branches")
@AllArgsConstructor
@Tag(name = "03 Branches", description = "Company branch management.")
public class BranchController {
    private final BranchService branchService;

    @GetMapping
    @Operation(summary = "List branches", description = "Returns the active branches registered in the company.")
    public ResponseEntity<List<BranchResponseDTO>> getBranches() {
        return ResponseEntity.ok(branchService.findAll());
    }

    @PostMapping
    @Operation(summary = "Create branch", description = "Registers a new branch with name, city, and address.")
    public ResponseEntity<BranchResponseDTO> createBranch(@Valid @RequestBody BranchCreateDTO branchCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.create(branchCreateDTO));
    }

    @PutMapping("/{id_branch}")
    @Operation(summary = "Update branch", description = "Updates the data of an existing branch.")
    public ResponseEntity<BranchResponseDTO> updateBranch(
            @PathVariable("id_branch") @Positive(message = "El ID de la sucursal debe ser mayor que cero") Long idBranch,
            @Valid @RequestBody BranchUpdateDTO branchUpdateDTO) {
        return ResponseEntity.ok(branchService.putById(idBranch, branchUpdateDTO));
    }

    @DeleteMapping("/{id_branch}")
    @Operation(summary = "Deactivate branch", description = "Soft-deletes the branch by marking it as inactive without physically deleting it.")
    public ResponseEntity<BranchResponseDTO> deleteBranch(
            @NonNull @PathVariable("id_branch") @Positive(message = "El ID de la sucursal debe ser mayor que cero") Long idBranch) {
        return ResponseEntity.ok(branchService.deleteById(idBranch));
    }
}
