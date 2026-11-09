package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Common.Security.Auth.RefreshTokenService;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Account.DTO.NewAccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.ResponseAccountDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountService {
    private AccountRepository accountRepository;
    private AccountMapper accountMapper;
    private PasswordEncoder passwordEncoder;
    private RefreshTokenService refreshTokenService;


    @Transactional(readOnly = true)
    public List<ResponseAccountDTO> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResponseAccountDTO findCurrent() {
        return accountMapper.toResponse(getCurrentAccount());
    }

    @Transactional(readOnly = true)
    public ResponseAccountDTO findByUser(String user) {
        if (user == null || user.isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        AccountEntity account = accountRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta inexistente", "AccountEntity"));

        return accountMapper.toResponse(account);
    }

    public ResponseAccountDTO save(NewAccountDTO newAccountDTO){
        validateCanCreateRole(newAccountDTO.rol());

        AccountEntity entity = accountMapper.toEntity(newAccountDTO);
        entity.setPassword(
                passwordEncoder.encode(entity.getPassword())
        );
        entity.setMustChangePassword(false);
        accountRepository.save(entity);

        return accountMapper.toResponse(entity);
    }

    @Transactional
    public ResponseAccountDTO updateRole(String identifier, RolEnum rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        validateCanAssignRole(rol);

        AccountEntity account = accountRepository.findByUserOrEmail(identifier, identifier)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta inexistente", "AccountEntity"));
        account.setRol(rol);
        accountRepository.save(account);
        refreshTokenService.revokeAllForAccount(account);

        return accountMapper.toResponse(account);
    }

    @Transactional
    public ResponseAccountDTO updateCurrentEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        AccountEntity account = getCurrentAccount();
        account.setEmail(email);
        accountRepository.save(account);
        refreshTokenService.revokeAllForAccount(account);

        return accountMapper.toResponse(account);
    }

    @Transactional
    public ResponseAccountDTO updateCurrentPassword(String currentPassword, String newPassword) {

        AccountEntity account = getCurrentAccount();

        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new AccessDeniedException("La contraseña actual no es correcta");
        }

        if (newPassword.equals(account.getUsername())) {
            throw new IllegalArgumentException(
                    "La nueva contraseña no puede ser igual al DNI"
            );
        }

        if (passwordEncoder.matches(newPassword, account.getPassword())) {
            throw new IllegalArgumentException(
                    "La nueva contraseña no puede ser igual a la contraseña actual"
            );
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        account.setMustChangePassword(false);

        accountRepository.save(account);
        refreshTokenService.revokeAllForAccount(account);

        return accountMapper.toResponse(account);
    }

    private void validateCanCreateRole(RolEnum rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        if (hasRole("ROLE_ADMIN")) {
            return;
        }

        throw new AccessDeniedException("Solo un ADMIN puede registrar cuentas");
    }

    private void validateCanAssignRole(RolEnum rol) {
        if (hasRole("ROLE_ADMIN")) {
            return;
        }

        if (hasRole("ROLE_STAFF") && rol != RolEnum.ADMIN) {
            return;
        }

        throw new AccessDeniedException("No tenés permisos para asignar ese rol");
    }

    private AccountEntity getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AccountEntity account) {
            return account;
        }

        String username = authentication.getName();
        return accountRepository.findByUserOrEmail(username, username)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta inexistente", "AccountEntity"));
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
