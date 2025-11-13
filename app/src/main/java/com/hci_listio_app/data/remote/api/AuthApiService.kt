package com.hci_listio_app.data.remote.api

import com.hci_listio_app.data.remote.dto.ChangePasswordRequest
import com.hci_listio_app.data.remote.dto.ForgotPasswordRequest
import com.hci_listio_app.data.remote.dto.LoginRequest
import com.hci_listio_app.data.remote.dto.LoginResponse
import com.hci_listio_app.data.remote.dto.RegisterRequest
import com.hci_listio_app.data.remote.dto.ResetPasswordRequest
import com.hci_listio_app.data.remote.dto.SendVerificationRequest
import com.hci_listio_app.data.remote.dto.UpdateProfileRequest
import com.hci_listio_app.data.remote.dto.UserProfileResponse
import com.hci_listio_app.data.remote.dto.VerifyAccountRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthApiService {

    @POST("users/register")
    suspend fun register(
        @Body payload: RegisterRequest
    ): UserProfileResponse

    @POST("users/login")
    suspend fun login(
        @Body payload: LoginRequest
    ): LoginResponse

    @GET("users/profile")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): UserProfileResponse

    @PUT("users/profile")
    suspend fun updateProfile(
        @Header("Authorization") authorization: String,
        @Body payload: UpdateProfileRequest
    ): UserProfileResponse

    @POST("users/verify-account")
    suspend fun verifyAccount(
        @Body payload: VerifyAccountRequest
    ): UserProfileResponse

    @POST("users/forgot-password")
    suspend fun forgotPassword(
        @Query("email") email: String
    )

    @POST("users/reset-password")
    suspend fun resetPassword(
        @Body payload: ResetPasswordRequest
    )

    @POST("users/send-verification")
    suspend fun sendVerification(
        @Query("email") email: String
    )

    @POST("users/change-password")
    suspend fun changePassword(
        @Header("Authorization") authorization: String,
        @Body payload: ChangePasswordRequest
    )

    @POST("users/logout")
    suspend fun logout(
        @Header("Authorization") authorization: String
    )
}

