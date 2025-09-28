package com.mjc.school.security.service;

import com.mjc.school.model.Role;
import com.mjc.school.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService();
    }

    @Test
    void generateToken_ShouldContainUsernameAndClaims() {
        // given
        User user = User.builder()
                .id(1L)
                .username("john")
                .password("secret")
                .role(Role.ROLE_USER)
                .build();

        // when
        String token = jwtTokenService.generateToken(user);

        // then
        assertNotNull(token);

        String username = jwtTokenService.getUsernameFromToken(token);
        assertEquals("john", username);

        Claims claims = jwtTokenService.getAllClaimsFromToken(token);
        assertEquals(1, claims.get("id"));
        assertEquals("ROLE_USER", claims.get("role"));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForCorrectUser() {
        User user = User.builder()
                .id(2L)
                .username("alice")
                .password("pwd")
                .role(Role.ROLE_ADMIN)
                .build();

        String token = jwtTokenService.generateToken(user);

        assertTrue(jwtTokenService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        User user = User.builder()
                .id(3L)
                .username("bob")
                .password("pwd")
                .role(Role.ROLE_USER)
                .build();

        String token = jwtTokenService.generateToken(user);

        // другой user
        UserDetails wrongUser = org.springframework.security.core.userdetails.User
                .withUsername("wrong")
                .password("pwd")
                .roles("USER")
                .build();

        assertFalse(jwtTokenService.isTokenValid(token, wrongUser));
    }

    @Test
    void isTokenExpired_ShouldReturnFalse_ForFreshToken() {
        User user = User.builder()
                .id(4L)
                .username("fresh")
                .password("pwd")
                .role(Role.ROLE_USER)
                .build();

        String token = jwtTokenService.generateToken(user);

        assertFalse(jwtTokenService.isTokenExpired(token));
    }

    @Test
    void getClaimsFromToken_ShouldExtractExpiration() {
        User user = User.builder()
                .id(5L)
                .username("bob")
                .password("pwd")
                .role(Role.ROLE_USER)
                .build();

        String token = jwtTokenService.generateToken(user);

        Date expiration = jwtTokenService.getClaimsFromToken(token, Claims::getExpiration);

        assertTrue(expiration.after(new Date()));
    }
}
