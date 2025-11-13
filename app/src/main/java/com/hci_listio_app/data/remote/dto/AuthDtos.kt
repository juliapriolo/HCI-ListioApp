package com.hci_listio_app.data.remote.dto

data class RegisterRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val metadata: Map<String, Any?> = emptyMap()
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class UserProfileResponse(
    val id: Long,
    val name: String,
    val surname: String,
    val email: String,
    val metadata: Map<String, Any?>,
    val updatedAt: String,
    val createdAt: String
)

data class UpdateProfileRequest(
    val name: String,
    val surname: String,
    val metadata: Map<String, Any?> = emptyMap()
)

data class VerifyAccountRequest(
    val code: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val code: String,
    val password: String
)

data class SendVerificationRequest(
    val email: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

