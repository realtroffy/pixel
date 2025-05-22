package com.example.pixel.config.secutiry;

import com.example.pixel.exception.JwtValidationException;
import com.example.pixel.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final Key SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final long EXPIRATION_TIME = 86400000;

    private Map<String, Object> generateClaims(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            return claims;
        }
        return null;
    }

    public String createToken(UserDetails userDetails) {
        return Jwts.builder()
                .claims(generateClaims(userDetails))
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (userDetails instanceof User customUserDetails) {
            Long userId = extractUserId(token);
            return (Objects.equals(userId, customUserDetails.getId())) && !isTokenExpired(token);
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private   <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        if (!claims.containsKey("id")) {
            throw new JwtValidationException("Token does not contain 'id' claim");
        }
        return claims.get("id", Long.class);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("Token is expired", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtValidationException("Token is unsupported", e);
        } catch (MalformedJwtException e) {
            throw new JwtValidationException("Token is malformed", e);
        } catch (SignatureException e) {
            throw new JwtValidationException("Invalid token signature", e);
        } catch (JwtException e) {
            throw new JwtValidationException("Token is invalid", e);
        } catch (Exception e) {
            throw new JwtValidationException("Unexpected error parsing token", e);
        }
    }
}