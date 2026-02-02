package com.shirt.pod.security;

import com.shirt.pod.config.JwtProperties;
import com.shirt.pod.model.entity.Role;
import com.shirt.pod.model.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private static final String PERMISSIONS_CLAIMS = "permissions";
    private static final String USER_ID_CLAIMS = "userid";
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Instant now = Instant.now();
        Instant expiryDate = now.plusSeconds(jwtProperties.getAccessTokenExpiration());

        Set<String> permissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(userDetails.getEmail())
                .claim(USER_ID_CLAIMS, userDetails.getId())
                .claim(PERMISSIONS_CLAIMS, permissions)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateAccessTokenFromEmail(String email, Long userId, Set<Role> roles) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusSeconds(jwtProperties.getAccessTokenExpiration());

        Set<String> permissions = new HashSet<>();
        if (roles != null) {
            roles.forEach(role -> {
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(permission -> permissions.add(permission.getName()));
                }
            });
        }

        return Jwts.builder()
                .subject(email)
                .claim(USER_ID_CLAIMS, userId)
                .claim(PERMISSIONS_CLAIMS, permissions)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get(USER_ID_CLAIMS, Long.class);
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<String> permissions = claims.get(PERMISSIONS_CLAIMS, List.class);
        if (permissions == null) {
            return Collections.emptyList();
        }

        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    private SecretKey getRefreshSigningKey() {
        byte[] keyBytes = jwtProperties.getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusSeconds(jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim(USER_ID_CLAIMS, user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getRefreshSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getRefreshSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid Refresh Token signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid Refresh Token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired Refresh Token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported Refresh Token");
        } catch (IllegalArgumentException ex) {
            log.error("Refresh Token claims string is empty");
        }
        return false;
    }

    public String getEmailFromRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getRefreshSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}
