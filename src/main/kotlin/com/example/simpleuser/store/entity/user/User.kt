package com.example.simpleuser.store.entity.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId
import java.util.UUID

@Entity
@Table(name = "user_table")
open class User(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    open var id: UUID? = null,
    @NaturalId
    open var email: String = "",
    open var password: String = "",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return this.email == other.email
    }

    override fun hashCode(): Int = email.hashCode()

    override fun toString(): String = "User(id=$id, email=$email, password=$password)"
}