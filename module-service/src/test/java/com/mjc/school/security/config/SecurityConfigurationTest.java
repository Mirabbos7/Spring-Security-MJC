package com.mjc.school.security.config;

import com.mjc.school.security.filter.JwtAuthenticationFilter;
import com.mjc.school.security.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityConfigurationTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private SecurityConfiguration securityConfiguration;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = mock(JwtAuthenticationFilter.class);
        securityConfiguration = new SecurityConfiguration(jwtAuthenticationFilter);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfiguration.passwordEncoder();
        assertNotNull(encoder);
        String raw = "1234";
        String encoded = encoder.encode(raw);
        assertTrue(encoder.matches(raw, encoded));
    }


    @Test
    void authenticationProvider_ShouldContainUserServiceAndPasswordEncoder() throws Exception {
        UserService userService = mock(UserService.class);
        DaoAuthenticationProvider provider = securityConfiguration.authenticationProvider(userService);

        Field userDetailsServiceField = DaoAuthenticationProvider.class.getDeclaredField("userDetailsService");
        userDetailsServiceField.setAccessible(true);
        Object actualUserService = userDetailsServiceField.get(provider);

        Field passwordEncoderField = DaoAuthenticationProvider.class.getDeclaredField("passwordEncoder");
        passwordEncoderField.setAccessible(true);
        Object actualPasswordEncoder = passwordEncoderField.get(provider);

        assertSame(userService, actualUserService);
        assertTrue(actualPasswordEncoder instanceof org.springframework.security.crypto.password.PasswordEncoder);
    }



    @Test
    void authenticationManager_ShouldReturnFromAuthConfig() throws Exception {
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        Mockito.when(authConfig.getAuthenticationManager()).thenReturn(expectedManager);

        AuthenticationManager result = securityConfiguration.authenticationManager(authConfig);

        assertSame(expectedManager, result);
    }
}
