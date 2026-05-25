package com.finance.manager.security;

import com.finance.manager.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class CustomUserDetailsTest {

    private CustomUserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("encodedPass")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();
        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("getUser returns the underlying User entity")
    void getUser() {
        assertThat(userDetails.getUser()).isSameAs(user);
    }

    @Test
    @DisplayName("getUsername returns the user's email")
    void getUsername() {
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("getPassword returns the encoded password")
    void getPassword() {
        assertThat(userDetails.getPassword()).isEqualTo("encodedPass");
    }

    @Test
    @DisplayName("getAuthorities returns an empty collection")
    void getAuthorities() {
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("account is non-expired, non-locked, credentials non-expired, enabled")
    void accountFlags() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }
}
