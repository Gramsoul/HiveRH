package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Features.Account.DTO.ResponseAccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountEmailDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountPasswordDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountRoleDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Validated
@Tag(name = "02 Accounts", description = "Authenticated account operations and role management.")
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "List accounts", description = "Returns all user accounts without exposing passwords. Requires an ADMIN or STAFF token.")
    public ResponseEntity<List<ResponseAccountDTO>> getAccounts() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/me")
    @Operation(summary = "Get my account", description = "Returns the email, username, and role of the account associated with the authenticated token.")
    public ResponseEntity<ResponseAccountDTO> getMyAccount() {
        return ResponseEntity.ok(accountService.findCurrent());
    }

    @GetMapping("/{user}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get account by user", description = "Returns an account by username. Employee accounts use their synchronized DNI as username.")
    public ResponseEntity<ResponseAccountDTO> getAccountByUser(@PathVariable String user) {
        return ResponseEntity.ok(accountService.findByUser(user));
    }

    @PatchMapping("/{identifier}/rol")
    @Operation(summary = "Update account role", description = "Changes the role of an existing account by username, email, or employee DNI when the account username is the employee DNI.")
    public ResponseEntity<ResponseAccountDTO> updateRole(
            @PathVariable String identifier,
            @Valid @RequestBody UpdateAccountRoleDTO request) {
        return ResponseEntity.ok(accountService.updateRole(identifier, request.rol()));
    }

    @PatchMapping("/me/email")
    @Operation(summary = "Update my email", description = "Updates the email address of the account associated with the authenticated token.")
    public ResponseEntity<ResponseAccountDTO> updateMyEmail(@Valid @RequestBody UpdateAccountEmailDTO request) {
        return ResponseEntity.ok(accountService.updateCurrentEmail(request.email()));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Update my password", description = "Updates the authenticated account password after validating the current password.")
    public ResponseEntity<ResponseAccountDTO> updateMyPassword(@Valid @RequestBody UpdateAccountPasswordDTO request) {
        return ResponseEntity.ok(accountService.updateCurrentPassword(
                request.currentPassword(),
                request.newPassword()
        ));
    }
}
