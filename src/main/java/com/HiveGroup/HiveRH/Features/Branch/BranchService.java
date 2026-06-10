package com.HiveGroup.HiveRH.Features.Branch;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchCreateDTO;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchResponseDTO;
import com.HiveGroup.HiveRH.Features.Branch.DTO.BranchUpdateDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;

    public List<BranchResponseDTO> findAll() {
        return branchRepository.findAllByIsActiveTrue().stream()
                .map(this::toDTO)
                .toList();
    }

    public BranchResponseDTO create(BranchCreateDTO branchCreateDTO) {
        BranchEntity branch = BranchEntity.builder()
                .branchName(branchCreateDTO.name())
                .city(branchCreateDTO.city())
                .address(branchCreateDTO.address())
                .isActive(true)
                .build();

        return toDTO(branchRepository.save(branch));
    }

    public BranchResponseDTO putById(Long id, BranchUpdateDTO branchUpdateDTO) {
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada", "Branch"));

        branch.setBranchName(branchUpdateDTO.name());
        branch.setCity(branchUpdateDTO.city());
        branch.setAddress(branchUpdateDTO.address());
        branch.setActive(branchUpdateDTO.active() != null ? branchUpdateDTO.active() : branch.isActive());

        return toDTO(branchRepository.save(branch));
    }

    public BranchResponseDTO deleteById(Long id) {
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada", "Branch"));

        branch.setActive(false);

        return toDTO(branchRepository.save(branch));
    }

    private BranchResponseDTO toDTO(BranchEntity branch) {
        return new BranchResponseDTO(
                branch.getId_branch(),
                branch.getBranchName(),
                branch.getCity(),
                branch.getAddress(),
                branch.isActive()
        );
    }
}
