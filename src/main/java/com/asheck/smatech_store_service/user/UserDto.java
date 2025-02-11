package com.asheck.smatech_store_service.user;

import java.time.LocalDateTime;
import java.util.List;

public record UserDto(
        int id,
        String userCode,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        boolean isActive,
        boolean isDeleted,
        String role,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean enabled,
        String username,
        List<AuthorityDto> authorities,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        boolean accountNonExpired
) {

}
