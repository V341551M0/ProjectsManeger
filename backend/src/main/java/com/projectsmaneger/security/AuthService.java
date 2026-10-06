package com.projectsmaneger.security;

import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;
import com.projectsmaneger.user.UserResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  public AuthService(
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  public String authenticate(String username, String password) {

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(username, password);

    Authentication authentication = authenticationManager.authenticate(authenticationToken);

    return jwtService.generateToken(authentication.getName());
  }

  public UserResponse findAuthenticatedUser(String username) {

    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.isEnabled(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}
