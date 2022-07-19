package com.mpnp.baechelin.login.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {

    @Getter
    private final String token;
    private final Key key;

    private static final String AUTHORITIES_KEY = "role";

    // refresh token 용
    AuthToken(String id, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, expiry);
    }

    // access token 용
    AuthToken(String id, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, role, expiry);
    }

    private String createAuthToken(String id, Date expiry) {
        return Jwts.builder()
                .setSubject(id) // 토큰 이름 설정
                .signWith(key, SignatureAlgorithm.HS256) // 256비트 서명
                .setExpiration(expiry) // 유효 기간 설정
                .compact();
    }

    private String createAuthToken(String id, String role, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY, role) // jwt payload에 private claims를 담는다. claim -> payload에 들어가는 일련의 정보
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean tokenValidate() {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return !claimsJws.getBody().isEmpty();
    }


    // 토큰의 claims, payload 값 가져오기
    public Claims getTokenClaims() {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 만료된 토큰인지 확인하는 용도
    public Claims getExpiredTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            log.info("유효하지 않은 구성의 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 형식이나 구성의 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info(e.toString().split(":")[1].trim());
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return e.getClaims();
        }
        return null;
    }
}
