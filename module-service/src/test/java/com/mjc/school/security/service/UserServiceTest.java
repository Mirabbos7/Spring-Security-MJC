package com.mjc.school.security.service;

import com.mjc.school.model.Role;
import com.mjc.school.model.User;
import com.mjc.school.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // given
        User user = User.builder()
                .id(1L)
                .username("john")
                .password("encodedPwd")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        // when
        UserDetails result = userService.loadUserByUsername("john");

        // then
        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals("encodedPwd", result.getPassword());
        verify(userRepository).findByUsername("john");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("ghost"));

        verify(userRepository).findByUsername("ghost");
    }
}
