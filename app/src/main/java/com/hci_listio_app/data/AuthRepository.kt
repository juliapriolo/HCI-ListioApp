package com.hci_listio_app.data

import com.hci_listio_app.data.remote.AuthRemoteDataSource
import com.hci_listio_app.data.remote.NetworkModule
import com.hci_listio_app.data.remote.dto.RegisterRequest
import com.hci_listio_app.data.remote.dto.UserProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthRepository(
    private val remoteDataSource: AuthRemoteDataSource
) {

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    suspend fun register(name: String, surname: String, email: String, password: String): Result<Unit> {
        val request = RegisterRequest(
            name = name,
            surname = surname,
            email = email,
            password = password,
            metadata = mapOf()
        )
        return remoteDataSource.register(request).map { Unit }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return remoteDataSource.login(email, password).map { response ->
            response.token.also { token ->
                _authToken.value = token
            }
        }
    }

    suspend fun logout(): Result<Unit> {
        val token = _authToken.value ?: return Result.success(Unit)
        val result = remoteDataSource.logout(token)
        if (result.isSuccess) {
            _authToken.value = null
        }
        return result
    }

    suspend fun sendVerification(email: String): Result<Unit> {
        return remoteDataSource.sendVerification(email)
    }

    suspend fun verifyAccount(code: String): Result<UserProfileResponse> {
        return remoteDataSource.verifyAccount(code)
    }

    suspend fun getProfile(): Result<UserProfileResponse> {
        val token = _authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.getProfile(token)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        val token = _authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.changePassword(token, currentPassword, newPassword)
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return remoteDataSource.forgotPassword(email)
    }

    suspend fun resetPassword(code: String, newPassword: String): Result<Unit> {
        return remoteDataSource.resetPassword(code, newPassword)
    }

    fun setToken(token: String?) {
        _authToken.update { token }
    }
}

object AuthRepositoryProvider {
    val instance: AuthRepository by lazy {
        val dataSource = AuthRemoteDataSource(NetworkModule.authApiService)
        AuthRepository(dataSource)
    }
}

