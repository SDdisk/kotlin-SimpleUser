package com.example.simpleuser.service.auth

import com.example.simpleuser.api.dto.auth.AuthRequest
import com.example.simpleuser.api.dto.auth.AuthResponse
import com.example.simpleuser.config.jwt.JwtProperties
import com.example.simpleuser.service.jwt.JwtService
import com.example.simpleuser.service.user.CustomUserDetailsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AuthService(
    private val authManager: AuthenticationManager,

    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,

    private val userDetailsService: CustomUserDetailsService,
) {
    fun authentication(authRequest: AuthRequest): AuthResponse {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authRequest.email,
                authRequest.password
            )
        )

        val user = userDetailsService.loadUserByUsername(authRequest.email)

        return AuthResponse(
            jwtToken = jwtService.generateToken(
                userDetails = user,
                expirationDate = Date(System.currentTimeMillis() + jwtProperties.expiration)
            )
        )
    }
}