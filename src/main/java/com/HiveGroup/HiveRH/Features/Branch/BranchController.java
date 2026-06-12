package com.HiveGroup.HiveRH.Features.Branch;

import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchCreateDTO;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchResponseDTO;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branch")
@AllArgsConstructor
public class BranchController {
    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> getBranches() {
        return ResponseEntity.ok(branchService.findAll());
    }

    @PostMapping
    public ResponseEntity<BranchResponseDTO> createBranch(@NonNull @RequestBody BranchCreateDTO branchCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.create(branchCreateDTO));
    }

    @PutMapping("/{id_branch}")
    public ResponseEntity<BranchResponseDTO> updateBranch(@NonNull @PathVariable("id_branch") Long idBranch,
                                                          @NonNull @RequestBody BranchUpdateDTO branchUpdateDTO) {
        return ResponseEntity.ok(branchService.putById(idBranch, branchUpdateDTO));
    }

    @DeleteMapping("/{id_branch}")
    public ResponseEntity<BranchResponseDTO> deleteBranch(@NonNull @PathVariable("id_branch") Long idBranch) {
        return ResponseEntity.ok(branchService.deleteById(idBranch));
    }
}
