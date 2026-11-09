package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Common.Security.Auth.RefreshTokenService;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Features.Account.DTO.NewAccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.ResponseAccountDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AccountService accountService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAllReturnsAccountResponses() {
        AccountEntity admin = account("admin.demo", RolEnum.ADMIN);
        AccountEntity staff = account("staff.demo", RolEnum.STAFF);
        ResponseAccountDTO adminResponse = new ResponseAccountDTO(
                "admin.demo@hiverh.com",
                "admin.demo",
                RolEnum.ADMIN
        );
        ResponseAccountDTO staffResponse = new ResponseAccountDTO(
                "staff.demo@hiverh.com",
                "staff.demo",
                RolEnum.STAFF
        );

        when(accountRepository.findAll()).thenReturn(List.of(admin, staff));
        when(accountMapper.toResponse(admin)).thenReturn(adminResponse);
        when(accountMapper.toResponse(staff)).thenReturn(staffResponse);

        List<ResponseAccountDTO> response = accountService.findAll();

        assertEquals(List.of(adminResponse, staffResponse), response);
        verify(accountRepository).findAll();
    }

    @Test
    void findCurrentReturnsAuthenticatedAccountResponse() {
        AccountEntity currentAccount = authenticateAs(RolEnum.EMPLOYEE);
        ResponseAccountDTO expected = new ResponseAccountDTO(
                "employee.demo@hiverh.com",
                "employee.demo",
                RolEnum.EMPLOYEE
        );

        when(accountMapper.toResponse(currentAccount)).thenReturn(expected);

        ResponseAccountDTO response = accountService.findCurrent();

        assertEquals(expected, response);
        verify(accountMapper).toResponse(currentAccount);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void findByUserReturnsMatchingAccount() {
        AccountEntity account = account("40111222", RolEnum.EMPLOYEE);
        ResponseAccountDTO expected = new ResponseAccountDTO(
                "40111222@hiverh.com",
                "40111222",
                RolEnum.EMPLOYEE
        );

        when(accountRepository.findByUser("40111222")).thenReturn(Optional.of(account));
        when(accountMapper.toResponse(account)).thenReturn(expected);

        assertEquals(expected, accountService.findByUser("40111222"));
        verify(accountRepository).findByUser("40111222");
    }

    @Test
    void staffCannotAssignAdminRole() {
        authenticateAs(RolEnum.STAFF);

        assertThrows(
                AccessDeniedException.class,
                () -> accountService.updateRole("employee.demo", RolEnum.ADMIN)
        );

        verifyNoInteractions(accountRepository);
    }

    @Test
    void staffCanAssignEmployeeRole() {
        authenticateAs(RolEnum.STAFF);
        AccountEntity target = account("employee.demo", RolEnum.STAFF);
        ResponseAccountDTO expected = new ResponseAccountDTO(
                "employee.demo@hiverh.com",
                "employee.demo",
                RolEnum.EMPLOYEE
        );

        when(accountRepository.findByUserOrEmail("employee.demo", "employee.demo"))
                .thenReturn(Optional.of(target));
        when(accountMapper.toResponse(target)).thenReturn(expected);

        ResponseAccountDTO response = accountService.updateRole("employee.demo", RolEnum.EMPLOYEE);

        assertEquals(RolEnum.EMPLOYEE, target.getRol());
        assertEquals(expected, response);
        verify(accountRepository).save(target);
        verify(refreshTokenService).revokeAllForAccount(target);
    }

    @Test
    void adminCanAssignAdminRole() {
        authenticateAs(RolEnum.ADMIN);
        AccountEntity target = account("staff.demo", RolEnum.STAFF);
        target.setMustChangePassword(true);
        ResponseAccountDTO expected = new ResponseAccountDTO(
                "staff.demo@hiverh.com",
                "staff.demo",
                RolEnum.ADMIN
        );

        when(accountRepository.findByUserOrEmail("staff.demo", "staff.demo"))
                .thenReturn(Optional.of(target));
        when(accountMapper.toResponse(target)).thenReturn(expected);

        ResponseAccountDTO response = accountService.updateRole("staff.demo", RolEnum.ADMIN);

        assertEquals(RolEnum.ADMIN, target.getRol());
        assertTrue(target.getMustChangePassword());
        assertEquals(expected, response);
        verify(accountRepository).save(target);
        verify(refreshTokenService).revokeAllForAccount(target);
    }

    @Test
    void changingPasswordClearsMustChangePasswordFlag() {
        AccountEntity currentAccount = authenticateAs(RolEnum.STAFF);
        currentAccount.setMustChangePassword(true);
        ResponseAccountDTO expected = new ResponseAccountDTO(
                currentAccount.getEmail(),
                currentAccount.getUser(),
                currentAccount.getRol()
        );

        when(passwordEncoder.matches("current-password", currentAccount.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("NewPassword123", currentAccount.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encoded-new-password");
        when(accountMapper.toResponse(currentAccount)).thenReturn(expected);

        accountService.updateCurrentPassword("current-password", "NewPassword123");

        assertEquals("encoded-new-password", currentAccount.getPassword());
        assertEquals(false, currentAccount.getMustChangePassword());
        verify(accountRepository).save(currentAccount);
        verify(refreshTokenService).revokeAllForAccount(currentAccount);
    }

    @Test
    void employeeCannotAssignAnyRole() {
        authenticateAs(RolEnum.EMPLOYEE);

        assertThrows(
                AccessDeniedException.class,
                () -> accountService.updateRole("employee.demo", RolEnum.STAFF)
        );

        verify(accountRepository, never()).findByUserOrEmail("employee.demo", "employee.demo");
    }

    @Test
    void staffCannotRegisterAccounts() {
        authenticateAs(RolEnum.STAFF);
        NewAccountDTO request = new NewAccountDTO(
                "new.staff",
                "new.staff@hiverh.com",
                "staff123",
                RolEnum.STAFF
        );

        assertThrows(
                AccessDeniedException.class,
                () -> accountService.save(request)
        );

        verifyNoInteractions(accountRepository, accountMapper, passwordEncoder);
    }

    @Test
    void adminCanRegisterAdminAccountWithoutEmployee() {
        authenticateAs(RolEnum.ADMIN);
        NewAccountDTO request = new NewAccountDTO(
                "admin.two",
                "admin.two@hiverh.com",
                "admin123",
                RolEnum.ADMIN
        );
        AccountEntity entity = AccountEntity.builder()
                .user("admin.two")
                .email("admin.two@hiverh.com")
                .password("admin123")
                .rol(RolEnum.ADMIN)
                .status(AccountStatus.ACTIVE)
                .build();
        ResponseAccountDTO expected = new ResponseAccountDTO(
                "admin.two@hiverh.com",
                "admin.two",
                RolEnum.ADMIN
        );

        when(accountMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded-admin123");
        when(accountMapper.toResponse(entity)).thenReturn(expected);

        ResponseAccountDTO response = accountService.save(request);

        assertEquals("encoded-admin123", entity.getPassword());
        assertEquals(expected, response);
        verify(accountRepository).save(entity);
    }

    private AccountEntity authenticateAs(RolEnum rol) {
        AccountEntity currentAccount = account(rol.name().toLowerCase() + ".demo", rol);
        var authentication = new UsernamePasswordAuthenticationToken(
                currentAccount,
                null,
                currentAccount.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return currentAccount;
    }

    private AccountEntity account(String user, RolEnum rol) {
        return AccountEntity.builder()
                .id_account(1L)
                .user(user)
                .email(user + "@hiverh.com")
                .password("encoded")
                .rol(rol)
                .status(AccountStatus.ACTIVE)
                .build();
    }
}
