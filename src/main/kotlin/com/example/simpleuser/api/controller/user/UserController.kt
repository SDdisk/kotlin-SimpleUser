package com.example.simpleuser.api.controller.user

import com.example.simpleuser.api.dto.user.UserRequestDto
import com.example.simpleuser.api.dto.user.UserResponseDto
import com.example.simpleuser.service.user.UserService
import com.example.simpleuser.store.entity.user.Role
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<UserResponseDto> =
        userService.getUsers()

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable id: UUID): UserResponseDto =
        userService.getUserById(id)

    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getByEmail(@PathVariable email: String): UserResponseDto =
        userService.getUserByEmail(email)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody userRequest: UserRequestDto): UserResponseDto =
        userService.createUser(userRequest)

    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: UUID) =
        userService.deleteUserById(id)

    @DeleteMapping("/email/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable email: String) =
        userService.deleteUserByEmail(email)


    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ADMIN')")
    fun adminInfo(): Map<String, String> = mapOf(
        "admin" to "best",
        "rest" to "api",
        "lol" to "kek",
        "chebu" to "rek"
    )
}