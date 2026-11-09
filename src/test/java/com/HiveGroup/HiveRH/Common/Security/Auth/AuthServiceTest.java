package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void mustChangePasswordDoesNotDependOnCurrentRole() {
        AccountEntity promotedAccount = account(RolEnum.ADMIN);
        promotedAccount.setMustChangePassword(true);

        assertTrue(authService.mustChangePassword(promotedAccount));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void legacyAccountFallsBackToInitialPasswordCheck() {
        AccountEntity legacyAccount = account(RolEnum.STAFF);
        legacyAccount.setMustChangePassword(null);
        when(passwordEncoder.matches("40111222", "encoded-password")).thenReturn(true);

        assertTrue(authService.mustChangePassword(legacyAccount));
    }

    private AccountEntity account(RolEnum role) {
        return AccountEntity.builder()
                .id_account(1L)
                .user("40111222")
                .email("40111222@hiverh.com")
                .password("encoded-password")
                .rol(role)
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
