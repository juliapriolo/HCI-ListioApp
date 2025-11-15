package com.hci_listio_app.data

import com.hci_listio_app.data.remote.ListRemoteDataSource
import com.hci_listio_app.data.remote.NetworkModule
import com.hci_listio_app.data.remote.dto.ShoppingListItemResponse
import com.hci_listio_app.data.remote.dto.ShoppingListResponse

class ListRepository(
    private val remoteDataSource: ListRemoteDataSource,
    private val authRepository: AuthRepository
) {

    // Obtener todas las listas del usuario
    suspend fun getLists(
        page: Int? = null,
        size: Int? = null
    ): Result<List<ShoppingListResponse>> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.getLists(token, page, size)
        }
    }

    // Obtener una lista específica
    suspend fun getList(listId: Long): Result<ShoppingListResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.getList(token, listId)
        }
    }

    // Crear una nueva lista
    suspend fun createList(
        name: String,
        description: String? = null,
        recurring: Boolean = false
    ): Result<ShoppingListResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.createList(token, name, description, recurring)
        }
    }

    // Actualizar una lista
    suspend fun updateList(listId: Long, name: String): Result<ShoppingListResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.updateList(token, listId, name)
        }
    }

    // Eliminar una lista
    suspend fun deleteList(listId: Long): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.deleteList(token, listId)
        }
    }

    // Agregar item a lista
    suspend fun addItem(
        listId: Long,
        productId: Long,
        quantity: Int? = 1,
        unit: String? = "kg"
    ): Result<ShoppingListItemResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.addItem(token, listId, productId, quantity, unit)
        }
    }

    // Obtener items de lista
    suspend fun getItems(
        listId: Long,
        purchased: Boolean? = null
    ): Result<List<ShoppingListItemResponse>> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.getItems(token, listId, purchased)
        }
    }

    // Actualizar item
    suspend fun updateItem(
        listId: Long,
        itemId: Long,
        quantity: Int? = 1,
        unit: String? = "kg"
    ): Result<ShoppingListItemResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.updateItem(token, listId, itemId, quantity, unit)
        }
    }

    // Toggle purchased status
    suspend fun togglePurchased(
        listId: Long,
        itemId: Long,
        purchased: Boolean
    ): Result<ShoppingListItemResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.togglePurchased(token, listId, itemId, purchased)
        }
    }

    // Eliminar item
    suspend fun deleteItem(listId: Long, itemId: Long): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.deleteItem(token, listId, itemId)
        }
    }

    // Añadir usuario a lista por ID (compatibilidad)
    suspend fun addUserToList(listId: Long, userId: Long): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.addUserToList(token, listId, userId = userId)
        }
    }

    // Añadir usuario a lista por email
    suspend fun shareListWithEmail(listId: Long, email: String): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.addUserToList(token, listId, email = email)
        }
    }

    // Remover usuario de lista
    suspend fun removeUserFromList(listId: Long, userId: Long): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.removeUserFromList(token, listId, userId)
        }
    }
}

object ListRepositoryProvider {
    val instance: ListRepository by lazy {
        val dataSource = ListRemoteDataSource(NetworkModule.listApiService)
        ListRepository(dataSource, AuthRepositoryProvider.instance)
    }
}