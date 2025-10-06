package com.example.simpleuser.service.user

import com.example.simpleuser.api.dto.user.UserRequestDto
import com.example.simpleuser.api.dto.user.UserResponseDto
import com.example.simpleuser.store.entity.user.Role
import com.example.simpleuser.store.entity.user.User
import com.example.simpleuser.store.repository.user.UserRepository
import io.bloco.faker.Faker
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class UserServiceImplTest {

    private var userRepository: UserRepository = mockk()
    private var passwordEncoder: PasswordEncoder = mockk()
    private var faker: Faker = mockk()

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserServiceImpl(userRepository, passwordEncoder, faker)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class `Get users` {

        @Test
        fun `should return not empty list of user dto`() {
            // given
            val userList = mutableListOf<User>()
            for (i in 0..<25) {
                userList.add(User(
                    id = UUID.randomUUID(),
                    email = "test$i@test.test",
                    password = "pass$i",
                    role = Role.USER
                ))
            }

            every { userRepository.findAll() } returns userList

            // when
            val result = userService.getUsers()

            // then
            assertNotNull(result)
            assertFalse { result.isEmpty() }

            for (userDto in result) {
                assertIs<UserResponseDto>(userDto)
            }

            assertEquals(25, result.size)

            verify(exactly = 1) { userRepository.findAll() }
        }

        @Test
        fun `should return empty list of user dto`() {
            // given
            every { userRepository.findAll() } returns listOf()

            // when
            val result = userService.getUsers()

            // then
            assertNotNull(result)
            assertTrue { result.isEmpty() }

            verify(exactly = 1) { userRepository.findAll() }
        }

    }


    @Nested
    inner class `Get user by id` {

        @Test
        fun `should return user dto when user exists`() {
            // given
            val userId = UUID.randomUUID()
            val userEmail = "test@test.test"

            val user = User(
                id = userId,
                email = userEmail,
                password = "some-encoded-pass",
                role = Role.USER
            )

            every { userRepository.findByIdOrNull(userId) } returns user

            // when
            val result = userService.getUserById(userId)

            // then
            assertNotNull(result)
            assertIs<UserResponseDto>(result)

            assertEquals(userId, result.id)
            assertEquals(userEmail, result.email)

            verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
        }

        @Test
        fun `should throw exception when user not found`() {
            // given
            val userId = UUID.randomUUID()

            every { userRepository.findByIdOrNull(userId) } returns null

            // when & then
            val exception = assertThrows<ResponseStatusException> {
                userService.getUserById(userId)
            }

            assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
            assertEquals("User not found with id=$userId", exception.reason)

            verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
        }

    }


    @Nested
    inner class `Get user by email` {

        @Test
        fun `should return user dto when user exists`() {
            // given
            val userEmail = "test@test.test"
            val userId = UUID.randomUUID()

            val user = User(
                id = userId,
                email = userEmail,
                password = "qwerty-encoded",
                role = Role.USER
            )

            every { userRepository.findByEmail(userEmail) } returns user

            // when
            val result = userService.getUserByEmail(userEmail)

            // then
            assertNotNull(result)
            assertIs<UserResponseDto>(result)

            assertEquals(userId, result.id)
            assertEquals(userEmail, result.email)

            verify(exactly = 1) { userRepository.findByEmail(userEmail) }
        }

        @Test
        fun `should throw exception when user not found`() {
            // given
            val userEmail = "test@test.test"

            every { userRepository.findByEmail(userEmail) } returns null

            // when & then
            val exception = assertThrows<ResponseStatusException> {
                userService.getUserByEmail(userEmail)
            }

            assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
            assertEquals("User not found with email=$userEmail", exception.reason)

            verify(exactly = 1) { userRepository.findByEmail(userEmail) }
        }

    }


    @Nested
    inner class `Create user` {

        @Test
        fun `should return user dto when user successfully created`() {
            // given
            val userId = UUID.randomUUID()
            val userEmail = "test@test.test"
            val userPassword = "pass-encoded"

            val user = User(
                id = userId,
                email = userEmail,
                password = userPassword,
                role = Role.USER
            )

            fun User.toDto() = UserRequestDto(
                email = this.email,
                password = this.password
            )

            every { userRepository.existsByEmail(userEmail) } returns false
            every { passwordEncoder.encode(userPassword) } returns userPassword
            every { userRepository.save(any()) } returns user

            // when
            val result = userService.createUser(user.toDto())

            // then
            assertNotNull(result)
            assertIs<UserResponseDto>(result)

            assertEquals(userId, result.id)
            assertEquals(userEmail, result.email)

            verify {
                userRepository.existsByEmail(userEmail)
                passwordEncoder.encode(userPassword)
                userRepository.save(any())
            }
        }

        @Test
        fun `should throw exception when user exists`() {
            // given
            val userEmail = "test@test.test"
            every { userRepository.existsByEmail(any()) } returns true

            // when
            val exception = assertThrows<ResponseStatusException> {
                userService.createUser(UserRequestDto(
                    email = userEmail,
                    password = "pass"
                ))
            }

            // then
            assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
            assertEquals("User already exists with email=$userEmail", exception.reason)

            verify(exactly = 1) { userRepository.existsByEmail(any()) }
        }

    }


    @Nested
    inner class `Delete user by id` {

        @Test
        fun `should delete user when user exists`() {
            // given
            val userId = UUID.randomUUID()
            val user = User(
                id = userId,
                email = "test@test.test",
                password = "some-encoded-pass",
                role = Role.USER
            )

            every { userRepository.findByIdOrNull(userId) } returns user
            every { userRepository.deleteById(userId) } just Runs

            // when
            userService.deleteUserById(userId)

            // then
            verify(exactly = 1) {
                userRepository.findByIdOrNull(userId)
                userRepository.deleteById(userId)
            }
        }

        @Test
        fun `should throw exception when user not exists`() {
            // given
            val userId = UUID.randomUUID()

            every { userRepository.findByIdOrNull(userId) } returns null
            every { userRepository.deleteById(any()) } just Runs

            // when
            val exception = assertThrows<ResponseStatusException> {
                userService.deleteUserById(userId)
            }

            // then
            assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
            assertEquals("User not found with id=$userId", exception.reason)

            verify(exactly = 1) { userRepository.findByIdOrNull(userId) }
            verify(exactly = 0) { userRepository.deleteById(any()) }
        }

    }


    @Nested
    inner class `Delete user by email` {

        @Test
        fun `should delete user when user exists`() {
            // given
            val userId = UUID.randomUUID()
            val userEmail = "test@test.test"
            val user = User(
                id = userId,
                email = userEmail,
                password = "some-encoded-pass",
                role = Role.USER
            )

            every { userRepository.findByEmail(userEmail) } returns user
            every { userRepository.deleteById(userId) } just Runs

            // when
            userService.deleteUserByEmail(userEmail)

            // then
            verify(exactly = 1) {
                userRepository.findByEmail(userEmail)
                userRepository.deleteById(userId)
            }
        }

        @Test
        fun `should throw exception when user not exists`() {
            // given
            val userEmail = "test@test.test"

            every { userRepository.findByEmail(userEmail) } returns null
            every { userRepository.deleteById(any()) } just Runs

            // when
            val exception = assertThrows<ResponseStatusException> {
                userService.deleteUserByEmail(userEmail)
            }

            // then
            assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
            assertEquals("User not found with email=$userEmail", exception.reason)

            verify(exactly = 1) { userRepository.findByEmail(userEmail) }
            verify(exactly = 0) { userRepository.deleteById(any()) }
        }

    }

}