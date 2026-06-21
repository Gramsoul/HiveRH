package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    public UserDetails authenticate(AuthRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.identifier(),
                        input.password()
                )
        );
        AccountEntity account = accountRepository.findByUserOrEmail(input.identifier(), input.identifier()).orElseThrow(
                () -> new EntityNotFoundException("Usuario o email no encontrado", "Account"));
        if (account.getStatusEnum() != StatusEnum.ACTIVE) {
            throw new DisabledException("Cuenta deshabilitada");
        }
        return account;
    }

}
