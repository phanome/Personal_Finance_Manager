package com.finance.manager.service;

import com.finance.manager.dto.request.LoginRequest;
import com.finance.manager.dto.request.RegisterRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.RegisterResponse;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ConflictException;
import com.finance.manager.exception.UnauthorizedException;
import com.finance.manager.repository.UserRepository;
import com.finance.manager.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user registration, login, and logout.
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user after validating that the email is not already taken.
     *
     * @param request registration payload
     * @return response containing the new user's id
     * @throws ConflictException if a user with the same email already exists
     */
    @SuppressWarnings("null")
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("A user with email '" + request.getUsername() + "' already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User saved = userRepository.save(user);
        return new RegisterResponse("User registered successfully", saved.getId());
    }

    /**
     * Authenticates a user with username/password and establishes a server-side session.
     *
     * @param request     login credentials
     * @param httpRequest the current HTTP request used to bind the session
     * @return success message
     * @throws UnauthorizedException if credentials are invalid
     */
    public MessageResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Bind the SecurityContext to the HTTP session so subsequent requests stay authenticated
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            return new MessageResponse("Login successful");
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    /**
     * Invalidates the current session and clears the security context.
     *
     * @param httpRequest the current HTTP request
     * @return success message
     */
    public MessageResponse logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return new MessageResponse("Logout successful");
    }

    /**
     * Extracts the {@link User} entity from the given {@link CustomUserDetails} principal.
     *
     * @param userDetails the authenticated principal
     * @return the underlying {@link User}
     */
    public User getUserFromPrincipal(CustomUserDetails userDetails) {
        return userDetails.getUser();
    }
}
