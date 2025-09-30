package com.example.simpleuser.api.controller.auth

import com.example.simpleuser.api.dto.auth.AuthRequest
import com.example.simpleuser.api.dto.auth.AuthResponse
import com.example.simpleuser.service.auth.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun login(@RequestBody authRequest: AuthRequest): AuthResponse =
        authService.authentication(authRequest)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun test() = mapOf(
        "message" to "welcome ept"
    )
}