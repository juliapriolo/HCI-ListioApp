package com.hci_listio_app.data.remote

import com.hci_listio_app.data.remote.api.ListApiService
import com.hci_listio_app.data.remote.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ListRemoteDataSource(
    private val api: ListApiService
) {

    // Obtener todas las listas
    suspend fun getLists(token: String): Result<List<ShoppingListResponse>> =
        safeApiCall { api.getLists(bearer(token)) }

    // Obtener una lista específica
    suspend fun getList(token: String, listId: Long): Result<ShoppingListResponse> =
        safeApiCall { api.getList(bearer(token), listId) }

    // Crear una nueva lista
    suspend fun createList(token: String, name: String): Result<ShoppingListResponse> =
        safeApiCall { api.createList(bearer(token), CreateListRequest(name)) }

    // Actualizar una lista
    suspend fun updateList(token: String, listId: Long, name: String): Result<ShoppingListResponse> =
        safeApiCall { api.updateList(bearer(token), listId, UpdateListRequest(name)) }

    // Eliminar una lista
    suspend fun deleteList(token: String, listId: Long): Result<Unit> =
        safeApiCall { api.deleteList(bearer(token), listId) }

    // Agregar item a lista
    suspend fun addItem(
        token: String,
        listId: Long,
        productName: String,
        quantity: Int? = null,
        productId: Long? = null,
        categoryId: Long? = null
    ): Result<ShoppingListItemResponse> =
        safeApiCall {
            api.addItem(
                bearer(token),
                listId,
                CreateItemRequest(productName, quantity, productId, categoryId)
            )
        }

    // Obtener items de lista
    suspend fun getItems(
        token: String,
        listId: Long,
        purchased: Boolean? = null
    ): Result<List<ShoppingListItemResponse>> =
        safeApiCall {
            api.getItems(
                bearer(token),
                listId,
                purchased = purchased
            )
        }

    // Actualizar item
    suspend fun updateItem(
        token: String,
        listId: Long,
        itemId: Long,
        productName: String,
        quantity: Int? = null,
        productId: Long? = null,
        categoryId: Long? = null
    ): Result<ShoppingListItemResponse> =
        safeApiCall {
            api.updateItem(
                bearer(token),
                listId,
                itemId,
                UpdateItemRequest(productName, quantity, productId, categoryId)
            )
        }

    // Toggle purchased status
    suspend fun togglePurchased(
        token: String,
        listId: Long,
        itemId: Long,
        purchased: Boolean
    ): Result<ShoppingListItemResponse> =
        safeApiCall {
            api.togglePurchased(
                bearer(token),
                listId,
                itemId,
                TogglePurchasedRequest(purchased)
            )
        }

    // Eliminar item
    suspend fun deleteItem(token: String, listId: Long, itemId: Long): Result<Unit> =
        safeApiCall { api.deleteItem(bearer(token), listId, itemId) }

    // Añadir usuario a lista
    suspend fun addUserToList(token: String, listId: Long, userId: Long): Result<Unit> =
        safeApiCall { api.addUserToList(bearer(token), listId, AddUserToListRequest(userId)) }

    // Remover usuario de lista
    suspend fun removeUserFromList(token: String, listId: Long, userId: Long): Result<Unit> =
        safeApiCall { api.removeUserFromList(bearer(token), listId, userId) }

    // Helper functions
    private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (error: Throwable) {
                Result.failure(mapError(error))
            }
        }

    private fun mapError(error: Throwable): Throwable {
        return when (error) {
            is HttpException -> {
                ApiException(
                    statusCode = error.code(),
                    message = getDefaultHttpErrorMessage(error.code()),
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

    private fun getDefaultHttpErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Solicitud inválida. Verifica los datos ingresados."
            401 -> "No autorizado. Por favor, inicia sesión."
            403 -> "No tienes permisos para realizar esta acción."
            404 -> "Lista o item no encontrado."
            409 -> "Conflicto: el recurso ya existe."
            500 -> "Error del servidor. Intenta más tarde."
            else -> "Error HTTP $statusCode"
        }
    }

    private fun bearer(token: String): String = "Bearer $token"
}