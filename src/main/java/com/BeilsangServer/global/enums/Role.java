package com.BeilsangServer.global.enums;

public enum Role {
    USER("USER"),
    ADMIN("USER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}