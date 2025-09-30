package com.example.simpleuser.config.security

import com.example.simpleuser.service.user.CustomUserDetailsService
import com.example.simpleuser.store.entity.user.Role
import com.example.simpleuser.store.repository.user.UserRepository
import io.bloco.faker.Faker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val userDetailsService: CustomUserDetailsService
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityFilterChain =
        http
            .csrf {
                it.disable()
            }
            .cors {
                it.disable()
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth**")
                    .permitAll()

                    .requestMatchers(HttpMethod.GET,"/api/users**")
                    .hasAnyRole(Role.ADMIN.name, Role.USER.name)

                    .requestMatchers("/api/users**")
                    .hasRole(Role.ADMIN.name)

                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
//            .authenticationProvider(
//                authenticationProvider()
//            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()

    /*
        2025-09-30T11:36:05.408+04:00  WARN 20460 --- [SimpleUser] [           main] r$InitializeUserDetailsManagerConfigurer :
        Global AuthenticationManager configured with an AuthenticationProvider bean.
        UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login.
        Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider.

        If the current configuration is intentional, to turn off this warning,
        increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
     */
//    @Bean
//    fun authenticationProvider(): AuthenticationProvider =
//        DaoAuthenticationProvider(userDetailsService)

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authCfg: AuthenticationConfiguration): AuthenticationManager =
        authCfg.authenticationManager
}