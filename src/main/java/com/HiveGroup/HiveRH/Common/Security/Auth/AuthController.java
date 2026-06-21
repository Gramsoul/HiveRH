package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Security.Config.JwtService;
import com.HiveGroup.HiveRH.Features.Account.AccountService;
import com.HiveGroup.HiveRH.Features.Account.DTO.NewAccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.ResponseAccountDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticacion y registro de cuentas para obtener acceso al sistema.")
public class AuthController {
    private final AuthService authService;
    private final AccountService accountService;
    private final JwtService jwtService;

    @PostMapping("/api/auth/login")
    @SecurityRequirements
    @Operation(summary = "Iniciar sesion", description = "Valida usuario o email junto con la password. Si las credenciales son correctas devuelve un token JWT para consumir endpoints protegidos.")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        UserDetails user = authService.authenticate(authRequest);
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }

    @PostMapping("/api/auth/register")
    @SecurityRequirements
    @Operation(summary = "Registrar cuenta", description = "Crea una cuenta de usuario. La password se almacena encriptada y el rol define los permisos iniciales de acceso.")
    public ResponseEntity<ResponseAccountDTO> registerUser(@Valid @RequestBody NewAccountDTO newAccountDTO) {
        return new ResponseEntity<>(accountService.save(newAccountDTO), HttpStatus.CREATED);
    }
}
