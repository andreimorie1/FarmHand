package com.example.farmhand.security

import java.security.MessageDigest

class PasswordHash {

    //Password hashing using SHA-256
    fun hashPassword(password: String): String {
        // Convert the password to bytes
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())

        // Convert the bytes to a hexadecimal string
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    // Verify the password
    fun verifyPassword(password: String, storedPassword: String): Boolean {

        // hash input the password
        val hashedPassword = (hashPassword(password))

        // Compare the hashed password with the stored password
        return hashedPassword == storedPassword
    }
}