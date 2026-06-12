package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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
        return
                accountRepository.findByUserOrEmail(input.identifier(), input.identifier()).orElseThrow(
                        () -> new EntityNotFoundException("Usuario o email no encontrado", "Account"));
    }

}
