package com.dexwin.notes.auth;

import com.dexwin.notes.user.User;
import com.dexwin.notes.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest req) {
        if (users.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole("USER");
        users.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                new HashMap<>() {{
                    put("role", user.getRole());
                }}
        );

        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest req) {

        // Get user
        User user = users.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        // Validate password manually (no AuthenticationManager)
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Generate JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                new HashMap<>() {{
                    put("role", user.getRole());
                }}
        );

        return new AuthResponse(token);
    }
}
