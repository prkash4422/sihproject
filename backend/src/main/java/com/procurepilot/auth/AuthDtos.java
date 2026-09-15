package com.procurepilot.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String phone;
        private String companyName;
        private String role; // "STARTUP" or "PROCUREMENT_ADMIN"

        public RegisterRequest() {}

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class AuthResponse {
        private String token;
        private String tokenType = "Bearer";
        private Long userId;
        private String email;
        private String fullName;
        private Set<String> roles;
        private Long startupId;

        public AuthResponse(String token, Long userId, String email, String fullName, Set<String> roles, Long startupId) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.fullName = fullName;
            this.roles = roles;
            this.startupId = startupId;
        }

        public String getToken() { return token; }
        public String getTokenType() { return tokenType; }
        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public Set<String> getRoles() { return roles; }
        public Long getStartupId() { return startupId; }
    }

    public static class UserDto {
        private Long id;
        private String email;
        private String fullName;
        private String phone;
        private Set<String> roles;
        private Long startupId;

        public UserDto(Long id, String email, String fullName, String phone, Set<String> roles, Long startupId) {
            this.id = id;
            this.email = email;
            this.fullName = fullName;
            this.phone = phone;
            this.roles = roles;
            this.startupId = startupId;
        }

        public Long getId() { return id; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public Set<String> getRoles() { return roles; }
        public Long getStartupId() { return startupId; }
    }
}
