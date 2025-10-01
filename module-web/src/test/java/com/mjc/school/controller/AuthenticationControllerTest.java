package com.mjc.school.controller;

import com.mjc.school.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.dtoForUser.SignInRequest;
import com.mjc.school.dtoForUser.SignUpRequest;
import com.mjc.school.security.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authService;

    @Test
    void signUp_shouldReturnJwtResponse() throws Exception {
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("jwt-signup-token");
        Mockito.when(authService.signUp(any(SignUpRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Mirabbos7777\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("jwt-signup-token")));

        verify(authService).signUp(any(SignUpRequest.class));
    }

    @Test
    void signIn_shouldReturnJwtResponse() throws Exception {
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("jwt-signin-token");
        Mockito.when(authService.signIn(any(SignInRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Mirabbos7777\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("jwt-signin-token")));

        verify(authService).signIn(any(SignInRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToAdmin_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/{id}/promote", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("User promoted to admin successfully.")));

        verify(authService).promoteToAdmin(eq(1L));
    }
}
