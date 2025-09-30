package com.example.simpleuser.api.dto.auth

data class AuthRequest(
    val email: String,
    val password: String,
)
