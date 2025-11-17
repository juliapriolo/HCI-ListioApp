package com.hci_listio_app.data.remote

import com.hci_listio_app.data.remote.api.AuthApiService
import com.hci_listio_app.data.remote.dto.ChangePasswordRequest
import com.hci_listio_app.data.remote.dto.LoginRequest
import com.hci_listio_app.data.remote.dto.LoginResponse
import com.hci_listio_app.data.remote.dto.RegisterRequest
import com.hci_listio_app.data.remote.dto.ResetPasswordRequest
import com.hci_listio_app.data.remote.dto.UpdateProfileRequest
import com.hci_listio_app.data.remote.dto.UserProfileResponse
import com.hci_listio_app.data.remote.dto.VerifyAccountRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRemoteDataSource(
    private val api: AuthApiService
) {

    suspend fun register(payload: RegisterRequest): Result<UserProfileResponse> =
        safeApiCall { api.register(payload) }

    suspend fun login(email: String, password: String): Result<LoginResponse> =
        safeApiCall(email) { api.login(LoginRequest(email, password)) }

    suspend fun getProfile(token: String): Result<UserProfileResponse> =
        safeApiCall { api.getProfile(bearer(token)) }

    suspend fun updateProfile(token: String, payload: UpdateProfileRequest): Result<UserProfileResponse> =
        safeApiCall { api.updateProfile(bearer(token), payload) }

    suspend fun verifyAccount(code: String): Result<UserProfileResponse> =
        safeApiCall { api.verifyAccount(VerifyAccountRequest(code)) }

    suspend fun forgotPassword(email: String): Result<Unit> =
        safeApiCall { api.forgotPassword(email) }

    suspend fun resetPassword(code: String, newPassword: String): Result<Unit> =
        safeApiCall { api.resetPassword(ResetPasswordRequest(code, newPassword)) }

    suspend fun sendVerification(email: String): Result<Unit> =
        safeApiCall { api.sendVerification(email) }

    suspend fun changePassword(token: String, currentPassword: String, newPassword: String): Result<Unit> =
        safeApiCall { api.changePassword(bearer(token), ChangePasswordRequest(currentPassword, newPassword)) }

    suspend fun logout(token: String): Result<Unit> =
        safeApiCall { api.logout(bearer(token)) }

    private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> =
        safeApiCall(null, block)

    private suspend fun <T> safeApiCall(email: String?, block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (error: Throwable) {
                Result.failure(mapError(error, email))
            }
        }

    private fun mapError(error: Throwable, email: String? = null): Throwable {
        return when (error) {
            is HttpException -> {
                val errorMessage = extractErrorMessage(error) ?: getDefaultHttpErrorMessage(error.code())
                
                if (error.code() == 401 && email != null) {
                    val lowerMessage = errorMessage.lowercase()
                    val isUnverified = lowerMessage.contains("verific") || 
                                      lowerMessage.contains("verificada") || 
                                      lowerMessage.contains("verificar") || 
                                      lowerMessage.contains("verification") ||
                                      lowerMessage.contains("no verificada") ||
                                      lowerMessage.contains("not verified") ||
                                      lowerMessage.contains("unverified")
                    
                    if (isUnverified) {
                        return UnverifiedAccountException(
                            email = email,
                            message = "Tu cuenta no está verificada. Por favor, verifica tu cuenta para poder iniciar sesión.",
                            cause = error
                        )
                    }
                }
                
                ApiException(
                    statusCode = error.code(),
                    message = errorMessage,
                    cause = error
                )
            }
            is IOException -> NetworkException(
                error.message ?: "Error de red: verifica tu conexión a internet",
                error
            )
            else -> Exception(
                error.message ?: "Error desconocido: ${error.javaClass.simpleName}",
                error
            )
        }
    }

    private fun extractErrorMessage(httpException: HttpException): String? {
        return try {
            val errorBody = httpException.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val gson = Gson()
                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                jsonObject.get("message")?.asString
                    ?: jsonObject.get("error")?.asString
                    ?: errorBody
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getDefaultHttpErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Solicitud inválida. Verifica los datos ingresados."
            401 -> "Credenciales inválidas. Verifica tu email y contraseña."
            403 -> "No tienes permisos para realizar esta acción."
            404 -> "Recurso no encontrado."
            409 -> "Conflicto: el recurso ya existe o hay un problema con el estado actual."
            500 -> "Error del servidor. Intenta más tarde."
            else -> "Error HTTP $statusCode"
        }
    }

    private fun bearer(token: String): String = "Bearer $token"
}

class ApiException(
    val statusCode: Int,
    override val message: String?,
    override val cause: Throwable? = null
) : Exception(message, cause)

class NetworkException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause)

class UnverifiedAccountException(
    val email: String,
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause)

