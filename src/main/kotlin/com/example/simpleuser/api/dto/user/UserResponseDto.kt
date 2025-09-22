package com.example.simpleuser.api.dto.user

import java.util.UUID

data class UserResponseDto(
    val id: UUID,
    val email: String,
)