package com.hci_listio_app.data

import com.hci_listio_app.data.remote.ListRemoteDataSource
import com.hci_listio_app.data.remote.NetworkModule
import com.hci_listio_app.data.remote.dto.ShoppingListItemResponse
import com.hci_listio_app.data.remote.dto.ShoppingListResponse

class ListRepository(
    private val remoteDataSource: ListRemoteDataSource,
    private val authRepository: AuthRepository
) {

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

    suspend fun getList(listId: Long): Result<ShoppingListResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.getList(token, listId)
        }
    }

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

    suspend fun updateList(
        listId: Long,
        name: String,
        recurring: Boolean? = null,
    ): Result<ShoppingListResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.updateList(token, listId, name, recurring)
        }
    }

    suspend fun toggleFavorite(listId: Long, isFavorite: Boolean): Result<ShoppingListResponse> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa."))
        } else {
            val listResult = getList(listId)
            if (listResult.isFailure) return Result.failure(listResult.exceptionOrNull()!!)

            val currentList = listResult.getOrNull()!!
            remoteDataSource.updateList(
                token,
                listId,
                currentList.name,
                recurring = isFavorite,
            )
        }
    }

    suspend fun deleteList(listId: Long): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.deleteList(token, listId)
        }
    }

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

    suspend fun getItems(
        listId: Long,
        purchased: Boolean? = null,
        categoryId: Long? = null,
        search: String? = null,
        sortBy: String? = null,
        order: String? = null
    ): Result<List<ShoppingListItemResponse>> {
        val token = authRepository.authToken.value
            ?: return Result.failure(Exception("No hay sesión activa."))

        return remoteDataSource.getItems(
            token = token,
            listId = listId,
            purchased = purchased,
            categoryId = categoryId,
            search = search,
            sortBy = sortBy,
            order = order
        )
    }

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

    suspend fun deleteItem(listId: Long, itemId: Long): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.deleteItem(token, listId, itemId)
        }
    }

    suspend fun addUserToList(listId: Long, userId: Long): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.addUserToList(token, listId, userId = userId)
        }
    }

    suspend fun shareListWithEmail(listId: Long, email: String): Result<Unit> {
        val token = authRepository.authToken.value
        return if (token == null) {
            Result.failure(Exception("No hay sesión activa. Por favor, inicia sesión."))
        } else {
            remoteDataSource.addUserToList(token, listId, email = email)
        }
    }

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