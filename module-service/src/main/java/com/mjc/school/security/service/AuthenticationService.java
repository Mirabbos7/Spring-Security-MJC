package com.mjc.school.security.service;

import com.mjc.school.exception.UsernameAlreadyExistsException;
import com.mjc.school.model.Role;
import com.mjc.school.model.User;
import com.mjc.school.repository.UserRepository;
import com.mjc.school.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.dtoForUser.SignInRequest;
import com.mjc.school.dtoForUser.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        String rawPassword = request.password();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException("User " + request.username() + " already exist");
        }
        var userModel = User.builder()
                .username(request.username())
                .password(encodedPassword)
                .role(Role.ROLE_USER)
                .build();
        this.create(userModel);
        var jwt = jwtTokenService.generateToken(userModel);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        String rawPassword = request.password();
        System.out.println("Raw Password: " + rawPassword);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(), request.password()));
        System.out.println("Authenticating user: " + request.username());
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var jwt = jwtTokenService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    @Transactional
    public void promoteToAdmin(Long id){
        var user = userRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("User with such id was not found!"));
        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);
    }

    @Transactional
    public User save(User userModel) {
        return userRepository.save(userModel);
    }

    @Transactional
    public User create(User userModel) {
        if (userRepository.existsByUsername(userModel.getUsername())) {
            throw new IllegalArgumentException("User with the same name already exists");
        }
        return save(userModel);
    }
}
