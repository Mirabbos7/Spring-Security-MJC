package com.mjc.school.controller;

import com.mjc.school.repository.implementation.UserRepository;
import com.mjc.school.repository.model.Role;
import com.mjc.school.repository.model.UserModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@ComponentScan(value = {"com.mjc.school", "com.mjc.school.repository", "com.mjc.school.service", "com.mjc.school.controller"})
@EntityScan(basePackages = {"com.mjc.school.repository"})
@EnableWebMvc
@EnableSwagger2
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.mjc.school.repository.implementation")
@EnableTransactionManagement

public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("ADMIN").isEmpty()) {
                UserModel admin = new UserModel();
                admin.setUsername("ADMIN");
                admin.setPassword(passwordEncoder.encode("PASSWORD"));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);
            }
        };
    }
}


