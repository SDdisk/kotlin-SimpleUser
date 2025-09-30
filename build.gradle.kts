plugins {
    kotlin("jvm")                               version "1.9.25"
    kotlin("plugin.spring")                     version "1.9.25"
    id("org.springframework.boot")              version "3.5.6"
    id("io.spring.dependency-management")       version "1.1.7"
}

group       = "com.example"
version     = "0.0.1-SNAPSHOT"
description = "SimpleUser"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // kotlin reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // spring-web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // spring-jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // spring-security
    implementation("org.springframework.boot:spring-boot-starter-security")
    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")

    implementation("io.bloco:faker:2.0.4")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    runtimeOnly("com.h2database:h2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
