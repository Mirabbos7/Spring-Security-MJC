package com.mjc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.dtoForUser.SignInRequest;
import com.mjc.school.dtoForUser.SignUpRequest;
import com.mjc.school.security.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private com.mjc.school.security.service.JwtTokenService jwtTokenService;

    private JwtAuthenticationResponse jwtResponse;

    @BeforeEach
    void setUp() {
        jwtResponse = new JwtAuthenticationResponse("fakeToken");
    }

    @Test
    void signUp_ShouldReturnJwtResponse() throws Exception {
        SignUpRequest request = new SignUpRequest("Mirabbos7777", "password123");

        Mockito.when(authService.signUp(any(SignUpRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("fakeToken")));

        verify(authService).signUp(any(SignUpRequest.class));
    }

    @Test
    void signIn_ShouldReturnJwtResponse() throws Exception {
        SignInRequest request = new SignInRequest("Mirabbos7777", "password123");

        Mockito.when(authService.signIn(any(SignInRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("fakeToken")));

        verify(authService).signIn(any(SignInRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToAdmin_ShouldReturnSuccessMessage() throws Exception {
        Long userId = 1L;
        doNothing().when(authService).promoteToAdmin(userId);

        mockMvc.perform(patch("/" + userId + "/promote"))
                .andExpect(status().isOk())
                .andExpect(content().string("User promoted to admin successfully."));

        verify(authService).promoteToAdmin(userId);
    }
}
