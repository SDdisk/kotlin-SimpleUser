package com.example.simpleuser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleUserApplication

fun main(args: Array<String>) {
    runApplication<SimpleUserApplication>(*args)
}
