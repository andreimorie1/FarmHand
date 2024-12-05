package com.example.farmhand.module_user.api

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val password: String
)

data class LoginRequest(
    val firstname: String,
    val password: String
)

data class UpdateRequest(
    val id: Int,
    val firstname: String?,
    val lastname: String?,
    val password: String?
)

data class IdRequest(
    val id: Int
)

data class ApiResponse(
    val register: Boolean?, // For registration
    val login: Boolean?,    // For login
    val update: Boolean?,   // For update
    val delete: Boolean?,   // For deletion
    val message: String,     // Status message
    val firstname: String,
    val lastname: String,
    val farmerId: Int?
)

