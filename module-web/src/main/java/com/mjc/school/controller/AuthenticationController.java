package com.mjc.school.controller;

import com.mjc.school.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.dtoForUser.SignInRequest;
import com.mjc.school.dtoForUser.SignUpRequest;
import com.mjc.school.security.service.AuthenticationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Component
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    //Mirabbos7777 username + password

    @ApiOperation(value = "User registration", response = JwtAuthenticationResponse.class)
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authService.signUp(request);
    }

    @ApiOperation(value = "User authorisation", response = JwtAuthenticationResponse.class)
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authService.signIn(request);
    }

    @PatchMapping("/{id}/promote")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Promote user to admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User promoted to admin"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User doesn’t have permission"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<String> promoteToAdmin(@PathVariable Long id) {
        authService.promoteToAdmin(id);
        return ResponseEntity.ok("User promoted to admin successfully.");
    }
}
