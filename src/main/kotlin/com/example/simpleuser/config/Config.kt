package com.example.simpleuser.config

import com.example.simpleuser.config.jwt.JwtProperties
import io.bloco.faker.Faker
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    value = [JwtProperties::class]
)
class Config {

    @Bean
    fun faker(): Faker = Faker()
}