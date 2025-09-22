package com.example.simpleuser.service.user

import com.example.simpleuser.api.dto.user.UserRequestDto
import com.example.simpleuser.api.dto.user.UserResponseDto
import com.example.simpleuser.store.entity.user.User
import com.example.simpleuser.store.repository.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun getUsers(): List<UserResponseDto> {
        log.info("Get users")

        return userRepository.findAll().map { entity ->
            entity.toResponse()
        }
    }

    override fun getUserById(id: UUID): UserResponseDto {
        log.info("Get user by id=$id")

        return userRepository.findByIdOrNull(id)
            ?.toResponse()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id=$id")
    }

    override fun getUserByEmail(email: String): UserResponseDto {
        log.info("Get user by email=$email")

        return userRepository.findByEmail(email)
            ?.toResponse()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email=$email")
    }

    override fun createUser(user: UserRequestDto): UserResponseDto {
        log.info("Create user=$user")

        if (userRepository.existsByEmail(user.email))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists with email=${user.email}")

        val newUser = user.toEntity()
        val savedUser = userRepository.save(newUser)

        return savedUser.toResponse()
    }

    override fun deleteUserById(id: UUID) {
        log.info("Delete user by id=$id")

        val foundUser = userRepository.findByIdOrNull(id)

        return foundUser?.let {
            userRepository.deleteById(id)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id=$id")
    }

    override fun deleteUserByEmail(email: String) {
        log.info("Delete user by email=$email")

        val foundUser = userRepository.findByEmail(email)

        return foundUser?.let {
            userRepository.deleteById(it.id!!)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email=$email")
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    // request -> entity
    private fun UserRequestDto.toEntity() =
        User(
            email = this.email,
            password = this.password,
        )

    // entity -> response
    private fun User.toResponse() =
        UserResponseDto(
            id = this.id!!,
            email = this.email
        )
}