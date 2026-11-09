package com.HiveGroup.HiveRH.Common.Security.Auth;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 32;

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpirationMs;
    private final Clock clock;
    private final SecureRandom secureRandom;

    @Autowired
    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${hiverh.auth.refresh-token-expiration-ms}") long refreshTokenExpirationMs
    ) {
        this(refreshTokenRepository, refreshTokenExpirationMs, Clock.systemUTC(), new SecureRandom());
    }

    RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            long refreshTokenExpirationMs,
            Clock clock,
            SecureRandom secureRandom
    ) {
        if (refreshTokenExpirationMs <= 0) {
            throw new IllegalArgumentException("La expiracion del refresh token debe ser mayor que cero");
        }
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.clock = clock;
        this.secureRandom = secureRandom;
    }

    @Transactional
    public IssuedRefreshToken issue(AccountEntity account) {
        Instant now = clock.instant();
        Instant expiresAt = now.plusMillis(refreshTokenExpirationMs);
        return createAndSave(account, UUID.randomUUID().toString(), now, expiresAt);
    }

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public IssuedRefreshToken rotate(String rawToken) {
        RefreshTokenEntity current = findByRawToken(rawToken);
        Instant now = clock.instant();

        if (current.isRevoked()) {
            revokeFamily(current.getFamilyId(), now);
            throw new BadCredentialsException("Se detecto la reutilizacion de un refresh token");
        }

        if (current.isExpired(now) || current.getAccount().getStatus() != AccountStatus.ACTIVE) {
            revokeFamily(current.getFamilyId(), now);
            throw new BadCredentialsException("El refresh token expiro o la cuenta esta inactiva");
        }

        String replacementRawToken = generateRawToken();
        String replacementHash = hash(replacementRawToken);

        current.setLastUsedAt(now);
        current.setRevokedAt(now);
        current.setReplacedByTokenHash(replacementHash);
        refreshTokenRepository.save(current);

        RefreshTokenEntity replacement = RefreshTokenEntity.builder()
                .account(current.getAccount())
                .tokenHash(replacementHash)
                .familyId(current.getFamilyId())
                .createdAt(now)
                .expiresAt(current.getExpiresAt())
                .build();
        refreshTokenRepository.save(replacement);

        return new IssuedRefreshToken(replacementRawToken, current.getAccount(), current.getExpiresAt());
    }

    @Transactional
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(token -> revokeFamily(token.getFamilyId(), clock.instant()));
    }

    @Transactional
    public void revokeAllForAccount(AccountEntity account) {
        revoke(refreshTokenRepository.findAllByAccountAndRevokedAtIsNull(account), clock.instant());
    }

    private IssuedRefreshToken createAndSave(
            AccountEntity account,
            String familyId,
            Instant createdAt,
            Instant expiresAt
    ) {
        String rawToken = generateRawToken();
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .account(account)
                .tokenHash(hash(rawToken))
                .familyId(familyId)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();
        refreshTokenRepository.save(entity);
        return new IssuedRefreshToken(rawToken, account, expiresAt);
    }

    private RefreshTokenEntity findByRawToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new BadCredentialsException("No se encontro la cookie de refresh token");
        }
        return refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BadCredentialsException("Refresh token invalido"));
    }

    private void revokeFamily(String familyId, Instant revokedAt) {
        revoke(refreshTokenRepository.findAllByFamilyIdAndRevokedAtIsNull(familyId), revokedAt);
    }

    private void revoke(List<RefreshTokenEntity> tokens, Instant revokedAt) {
        tokens.forEach(token -> token.setRevokedAt(revokedAt));
        refreshTokenRepository.saveAll(tokens);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no esta disponible", e);
        }
    }
}
