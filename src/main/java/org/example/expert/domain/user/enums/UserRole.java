package org.example.expert.domain.user.enums;

import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.InvalidRequestException;

import java.util.Arrays;

@Slf4j
public enum UserRole {
    ADMIN, USER;

    public static UserRole of(String role) {
        if (role == null) {
            throw new InvalidRequestException("UserRole이 null입니다.");
        }
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UserRole"));
    }
}