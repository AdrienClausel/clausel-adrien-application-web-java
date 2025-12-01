package com.payMyBuddy.app.dto;

public record UserChangePasswordDto(
        Long UserId,
        String password
) {
}
