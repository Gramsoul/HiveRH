package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-15T12:00:00Z");
    private static final long EXPIRATION_MS = 8 * 60 * 60 * 1000L;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void issuePersistsOnlyTheHashAndReturnsTheRawToken() {
        RefreshTokenService service = service();
        AccountEntity account = activeAccount();
        when(refreshTokenRepository.save(any(RefreshTokenEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IssuedRefreshToken issued = service.issue(account);

        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository).save(captor.capture());
        RefreshTokenEntity stored = captor.getValue();

        assertFalse(issued.token().isBlank());
        assertNotEquals(issued.token(), stored.getTokenHash());
        assertEquals(RefreshTokenService.hash(issued.token()), stored.getTokenHash());
        assertEquals(account, stored.getAccount());
        assertEquals(NOW, stored.getCreatedAt());
        assertEquals(NOW.plusMillis(EXPIRATION_MS), issued.expiresAt());
        assertNotNull(stored.getFamilyId());
    }

    @Test
    void rotateRevokesThePreviousTokenAndKeepsTheFamilyExpiry() {
        RefreshTokenService service = service();
        AccountEntity account = activeAccount();
        String rawToken = "previous-refresh-token";
        Instant familyExpiry = NOW.plusMillis(EXPIRATION_MS);
        RefreshTokenEntity current = token(account, rawToken, "family-1", familyExpiry);

        when(refreshTokenRepository.findByTokenHash(RefreshTokenService.hash(rawToken)))
                .thenReturn(Optional.of(current));
        when(refreshTokenRepository.save(any(RefreshTokenEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IssuedRefreshToken rotated = service.rotate(rawToken);

        assertTrue(current.isRevoked());
        assertEquals(NOW, current.getLastUsedAt());
        assertEquals(RefreshTokenService.hash(rotated.token()), current.getReplacedByTokenHash());
        assertEquals(familyExpiry, rotated.expiresAt());
        assertEquals(account, rotated.account());

        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        RefreshTokenEntity replacement = captor.getAllValues().get(1);
        assertEquals("family-1", replacement.getFamilyId());
        assertEquals(familyExpiry, replacement.getExpiresAt());
        assertFalse(replacement.isRevoked());
    }

    @Test
    void reuseOfRevokedTokenRevokesTheActiveFamily() {
        RefreshTokenService service = service();
        AccountEntity account = activeAccount();
        String rawToken = "already-used-token";
        RefreshTokenEntity reused = token(account, rawToken, "family-2", NOW.plusSeconds(3600));
        reused.setRevokedAt(NOW.minusSeconds(30));
        RefreshTokenEntity activeReplacement = token(
                account,
                "replacement-token",
                "family-2",
                NOW.plusSeconds(3600)
        );

        when(refreshTokenRepository.findByTokenHash(RefreshTokenService.hash(rawToken)))
                .thenReturn(Optional.of(reused));
        when(refreshTokenRepository.findAllByFamilyIdAndRevokedAtIsNull("family-2"))
                .thenReturn(List.of(activeReplacement));

        assertThrows(BadCredentialsException.class, () -> service.rotate(rawToken));

        assertEquals(NOW, activeReplacement.getRevokedAt());
        verify(refreshTokenRepository).saveAll(List.of(activeReplacement));
    }

    @Test
    void inactiveAccountCannotRefresh() {
        RefreshTokenService service = service();
        AccountEntity account = activeAccount();
        account.setStatus(AccountStatus.INACTIVE);
        String rawToken = "inactive-account-token";
        RefreshTokenEntity current = token(account, rawToken, "family-3", NOW.plusSeconds(3600));

        when(refreshTokenRepository.findByTokenHash(RefreshTokenService.hash(rawToken)))
                .thenReturn(Optional.of(current));
        when(refreshTokenRepository.findAllByFamilyIdAndRevokedAtIsNull("family-3"))
                .thenReturn(List.of(current));

        assertThrows(BadCredentialsException.class, () -> service.rotate(rawToken));

        assertEquals(NOW, current.getRevokedAt());
    }

    private RefreshTokenService service() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        return new RefreshTokenService(refreshTokenRepository, EXPIRATION_MS, clock, new SecureRandom());
    }

    private AccountEntity activeAccount() {
        return AccountEntity.builder()
                .id_account(1L)
                .user("40111222")
                .email("40111222@hiverh.com")
                .password("encoded")
                .rol(RolEnum.EMPLOYEE)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private RefreshTokenEntity token(
            AccountEntity account,
            String rawToken,
            String familyId,
            Instant expiresAt
    ) {
        return RefreshTokenEntity.builder()
                .account(account)
                .tokenHash(RefreshTokenService.hash(rawToken))
                .familyId(familyId)
                .createdAt(NOW.minusSeconds(60))
                .expiresAt(expiresAt)
                .build();
    }
}
