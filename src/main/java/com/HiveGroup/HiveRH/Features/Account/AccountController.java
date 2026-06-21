package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Features.Account.DTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Tag(name = "Accounts", description = "Operacion sobre la cuenta autenticada y administracion de roles.")
public class AccountController {
    private final AccountService accountService;

    @PatchMapping("/{id}/role")
    @Operation(summary = "Cambiar rol de cuenta", description = "Permite modificar el rol de una cuenta existente. Se utiliza para administrar permisos de acceso dentro del sistema.")
    public ResponseEntity<ResponseAccountDTO> updateRole(@NonNull @PathVariable Long id,
                                                         @NonNull @RequestBody UpdateAccountRoleDTO request) {
        return ResponseEntity.ok(accountService.updateRole(id, request.rol()));
    }

    @PatchMapping("/me/email")
    @Operation(summary = "Cambiar email propio", description = "Actualiza el email de la cuenta asociada al token autenticado.")
    public ResponseEntity<ResponseAccountDTO> updateMyEmail(@NonNull @RequestBody UpdateAccountEmailDTO request) {
        return ResponseEntity.ok(accountService.updateCurrentEmail(request.email()));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Cambiar password propia", description = "Actualiza la password de la cuenta autenticada, validando la password actual antes de guardar la nueva.")
    public ResponseEntity<ResponseAccountDTO> updateMyPassword(@NonNull @RequestBody UpdateAccountPasswordDTO request) {
        return ResponseEntity.ok(accountService.updateCurrentPassword(
                request.currentPassword(),
                request.newPassword()
        ));
    }
}
