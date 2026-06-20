package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Security.Config.JwtService;
import com.HiveGroup.HiveRH.Features.Account.DTO.AccountDTO;
import com.HiveGroup.HiveRH.Features.Account.AccountService;
import com.HiveGroup.HiveRH.Features.Account.DTO.NewAccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AccountService accountService;
    private final JwtService jwtService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<AuthResponse> authenticateUser(
            @RequestBody AuthRequest authRequest
    ) {
        AccountEntity account = authService.authenticate(authRequest);

        String token = jwtService.generateToken(account);

        return ResponseEntity.ok(
                new AuthResponse(token, account.getUsername(), account.isMustChangePassword() )
        );
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<AccountDTO> registerUser(@RequestBody
                                                   NewAccountDTO newAccountDTO) {
        return new ResponseEntity<>(accountService.save(newAccountDTO),HttpStatus.CREATED);
    }
}
