package com.mjc.school.security.service;

import com.mjc.school.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.dtoForUser.SignInRequest;
import com.mjc.school.dtoForUser.SignUpRequest;
import com.mjc.school.model.Role;
import com.mjc.school.model.User;
import com.mjc.school.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_ShouldCreateUserAndReturnJwt() {
        // given
        SignUpRequest request = new SignUpRequest("john", "pass123");
        User savedUser = User.builder()
                .id(1L)
                .username("john")
                .password("encodedPass")
                .role(Role.ROLE_USER)
                .build();

        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenService.generateToken(any(User.class))).thenReturn("jwt-token");

        // when
        JwtAuthenticationResponse response = authenticationService.signUp(request);

        // then
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUp_ShouldThrowException_WhenUserAlreadyExists() {
        SignUpRequest request = new SignUpRequest("john", "pass123");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertThrows(UsernameNotFoundException.class,
                () -> authenticationService.signUp(request));
    }

    @Test
    void signIn_ShouldAuthenticateAndReturnJwt() {
        SignInRequest request = new SignInRequest("john", "pass123");
        User user = User.builder().id(1L).username("john").password("encodedPass").build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtTokenService.generateToken(user)).thenReturn("jwt-token");

        JwtAuthenticationResponse response = authenticationService.signIn(request);

        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("john", "pass123"));
    }

    @Test
    void signIn_ShouldThrowException_WhenUserNotFound() {
        SignInRequest request = new SignInRequest("notExist", "123");
        when(userRepository.findByUsername("notExist")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authenticationService.signIn(request));
    }

    @Test
    void promoteToAdmin_ShouldUpdateRole() {
        User user = User.builder().id(1L).username("john").role(Role.ROLE_USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        authenticationService.promoteToAdmin(1L);

        assertEquals(Role.ROLE_ADMIN, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void promoteToAdmin_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authenticationService.promoteToAdmin(99L));
    }

    @Test
    void save_ShouldCallRepositorySave() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User result = authenticationService.save(user);

        assertSame(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void create_ShouldSaveUser_WhenNotExists() {
        User user = User.builder().username("john").build();
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User result = authenticationService.create(user);

        assertSame(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void create_ShouldThrowException_WhenUserExists() {
        User user = User.builder().username("john").build();
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authenticationService.create(user));
    }
}
