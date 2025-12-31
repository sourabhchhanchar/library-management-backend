package com.sourabh.librarymanagement.controller;

import com.sourabh.librarymanagement.controller.dto.SignupRequest;
import com.sourabh.librarymanagement.model.Role;
import com.sourabh.librarymanagement.model.User;
import com.sourabh.librarymanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.sourabh.librarymanagement.security.JwtUtil;
import com.sourabh.librarymanagement.controller.dto.LoginRequest;
import com.sourabh.librarymanagement.controller.dto.LoginResponse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;


    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email already registered");
        }


        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(request.getEmail())),
                User.class
        );

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        String token = JwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(
                new LoginResponse(token, user.getRole().name())
        );
    }

}
