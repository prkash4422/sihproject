package com.procurepilot.auth;

import com.procurepilot.common.ApiResponse;
import com.procurepilot.common.BadRequestException;
import com.procurepilot.startup.Startup;
import com.procurepilot.startup.StartupRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StartupRepository startupRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          StartupRepository startupRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.startupRepository = startupRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> authenticateUser(@Valid @RequestBody AuthDtos.LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Set<String> roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        Long startupId = startupRepository.findByUserId(userPrincipal.getId())
                .map(Startup::getId)
                .orElse(null);

        AuthDtos.AuthResponse response = new AuthDtos.AuthResponse(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getFullName(),
                roles,
                startupId
        );

        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> registerUser(@Valid @RequestBody AuthDtos.RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email address is already in use!");
        }

        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        String roleName = "ROLE_STARTUP";
        if ("PROCUREMENT_ADMIN".equalsIgnoreCase(registerRequest.getRole())) {
            roleName = "ROLE_PROCUREMENT_ADMIN";
        }

        Role userRole = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STARTUP")));
        user.setRoles(Collections.singleton(userRole));

        User savedUser = userRepository.save(user);

        Long startupId = null;
        if ("ROLE_STARTUP".equals(roleName)) {
            Startup startup = new Startup();
            startup.setUser(savedUser);
            startup.setCompanyName(registerRequest.getCompanyName() != null ? registerRequest.getCompanyName() : registerRequest.getFullName() + "'s Startup");
            Startup savedStartup = startupRepository.save(startup);
            startupId = savedStartup.getId();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        Set<String> roles = Collections.singleton(roleName);
        AuthDtos.AuthResponse response = new AuthDtos.AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                roles,
                startupId
        );

        return ResponseEntity.ok(ApiResponse.ok("Registration successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthDtos.UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        Set<String> roles = currentUser.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        Long startupId = startupRepository.findByUserId(currentUser.getId())
                .map(Startup::getId)
                .orElse(null);

        AuthDtos.UserDto userDto = new AuthDtos.UserDto(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getFullName(),
                null,
                roles,
                startupId
        );

        return ResponseEntity.ok(ApiResponse.ok(userDto));
    }
}
