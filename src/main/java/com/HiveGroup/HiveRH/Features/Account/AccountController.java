package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Features.Account.DTO.AccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountEmailDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountPasswordDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountRoleDTO;
import jakarta.validation.constraints.Positive;
import com.HiveGroup.HiveRH.Features.Account.DTO.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;

    @PatchMapping("/{id}/role")
    public ResponseEntity<ResponseAccountDTO> updateRole(@PathVariable @Positive(message = "El ID de la cuenta debe ser mayor que cero") Long id,
                                                 @Valid @RequestBody UpdateAccountRoleDTO request) {
        return ResponseEntity.ok(accountService.updateRole(id, request.rol()));
    }

    @PatchMapping("/me/email")
    public ResponseEntity<ResponseAccountDTO> updateMyEmail(@Valid @RequestBody UpdateAccountEmailDTO request) {
        return ResponseEntity.ok(accountService.updateCurrentEmail(request.email())); //@NonNull de Lombok no valida los campos internos del JSON. Para eso se utiliza:
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ResponseAccountDTO> updateMyPassword(@Valid @RequestBody UpdateAccountPasswordDTO request) { //@NonNull de Lombok no valida los campos internos del JSON. Para eso se utiliza:
        return ResponseEntity.ok(accountService.updateCurrentPassword(
                request.currentPassword(),
                request.newPassword()
        ));
    }
}
