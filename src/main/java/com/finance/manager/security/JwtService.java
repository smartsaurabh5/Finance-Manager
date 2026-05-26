package com.finance.manager.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final String secret;
    private final long expirationSeconds;
    private final long rememberMeExpirationSeconds;

    public JwtService(@Value("${app.jwt.secret:change-this-dev-secret-change-this-dev-secret}") String secret,
                      @Value("${app.jwt.expiration-seconds:1800}") long expirationSeconds,
                      @Value("${app.jwt.remember-me-expiration-seconds:604800}") long rememberMeExpirationSeconds) {
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
        this.rememberMeExpirationSeconds = rememberMeExpirationSeconds;
    }

    public String generateToken(UserDetails userDetails, boolean rememberMe) {
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + getExpiresInSeconds(rememberMe);
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"" + escapeJson(userDetails.getUsername()) + "\","
                + "\"iat\":" + issuedAt + ","
                + "\"exp\":" + expiresAt + "}";
        String unsignedToken = base64Url(header) + "." + base64Url(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    public long getExpiresInSeconds(boolean rememberMe) {
        return rememberMe ? rememberMeExpirationSeconds : expirationSeconds;
    }

    public String extractUsername(String token) {
        return parseStringClaim(decodePayload(token), "sub");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return userDetails != null
                && userDetails.getUsername().equals(extractUsername(token))
                && !isExpired(token)
                && hasValidSignature(token);
    }

    private boolean isExpired(String token) {
        return parseLongClaim(decodePayload(token), "exp") <= Instant.now().getEpochSecond();
    }

    private boolean hasValidSignature(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        String expectedSignature = sign(parts[0] + "." + parts[1]);
        return MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8)
        );
    }

    private String decodePayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3 || !hasValidSignature(token)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        return new String(BASE64_URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign JWT token", ex);
        }
    }

    private String base64Url(String value) {
        return BASE64_URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String parseStringClaim(String json, String claimName) {
        Matcher matcher = Pattern.compile("\"" + claimName + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing JWT claim: " + claimName);
        }
        return matcher.group(1);
    }

    private long parseLongClaim(String json, String claimName) {
        Matcher matcher = Pattern.compile("\"" + claimName + "\"\\s*:\\s*(\\d+)").matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing JWT claim: " + claimName);
        }
        return Long.parseLong(matcher.group(1));
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
