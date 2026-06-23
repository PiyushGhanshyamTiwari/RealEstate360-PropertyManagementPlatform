package com.cts.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.cts.entity.User;
import com.cts.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    /**
     * Returns the currently logged-in User entity.
     * Returns null if unauthenticated.
     */
    public User currentOrNull() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                return userRepository.findUserByEmail(email);
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Returns true if the logged-in user's ID matches the given userId.
     */
    public boolean isCurrentUser(Integer userId) {
        User currentUser = currentOrNull();
        if (currentUser == null) return false;
        return currentUser.getUserId().equals(userId);
    }

    /**
     * Returns true if the logged-in user has ADMIN role.
     */
    public boolean isAdmin() {
        User currentUser = currentOrNull();
        if (currentUser == null) return false;
        return "admin".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Returns true if the logged-in user is ADMIN or the same user as userId.
     */
    public boolean isAdminOrCurrentUser(Integer userId) {
        return isAdmin() || isCurrentUser(userId);
    }

    /**
     * Throws AccessDeniedException if the logged-in user is not the same as userId.
     * Use this when only the owner themselves can perform the action.
     */
    public void checkCurrentUser(Integer userId, String message) {
        if (!isCurrentUser(userId)) {
            throw new AccessDeniedException(message);
        }
    }

    /**
     * Throws AccessDeniedException if the logged-in user is neither ADMIN nor the same user.
     * Use this when both admin and the owner can perform the action.
     */
    public void checkAdminOrCurrentUser(Integer userId, String message) {
        if (!isAdminOrCurrentUser(userId)) {
            throw new AccessDeniedException(message);
        }
    }
}
