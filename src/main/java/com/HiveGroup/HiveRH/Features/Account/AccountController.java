package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Features.Account.DTO.AccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountEmailDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountPasswordDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountRoleDTO;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PatchMapping("/{id}/role")
    public ResponseEntity<AccountDTO> updateRole(@NonNull @PathVariable Long id,
                                                 @NonNull @RequestBody UpdateAccountRoleDTO request) {
        return ResponseEntity.ok(accountService.updateRole(id, request.rol()));
    }

    @PatchMapping("/me/email")
    public ResponseEntity<AccountDTO> updateMyEmail(@NonNull @RequestBody UpdateAccountEmailDTO request) {
        return ResponseEntity.ok(accountService.updateCurrentEmail(request.email()));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(
            @Valid @RequestBody UpdateAccountPasswordDTO request
    ) {
        accountService.updateCurrentPassword(request.currentPassword(), request.newPassword() );

        return ResponseEntity.noContent().build();
    }
}
