package com.example.simpleuser.service.user

import com.example.simpleuser.api.dto.user.UserRequestDto
import com.example.simpleuser.api.dto.user.UserResponseDto
import java.util.UUID

interface UserService {
    fun getUsers(): List<UserResponseDto>
    fun getUserById(id: UUID): UserResponseDto
    fun getUserByEmail(email: String): UserResponseDto

    fun createUser(user: UserRequestDto): UserResponseDto
    // fun updateUser(id: UUID, newUser: UserRequestDto): UserResponseDto // temporary disabled

    fun deleteUserById(id: UUID)
    fun deleteUserByEmail(email: String)
}