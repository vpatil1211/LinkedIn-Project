package com.linkedInProject.connectionsService.auth;

public class AuthContextHolder {

    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public static Long getUserIdFromContextHolder() {
        return userIdHolder.get();
    }

    static void setUserIdIntContextHolder(Long userId) {
        userIdHolder.set(userId);
    }

    static void removeUserIdFromContextHolder() {
        userIdHolder.remove();
    }
}
