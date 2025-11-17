package com.hci_listio_app.data.remote

import com.hci_listio_app.data.remote.api.ListApiService
import com.hci_listio_app.data.remote.dto.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ListRemoteDataSource(
    private val api: ListApiService
) {

    private val gson = Gson()
    private val listType = object : TypeToken<List<ShoppingListResponse>>() {}.type
    private val itemsType = object : TypeToken<List<ShoppingListItemResponse>>() {}.type

    suspend fun getLists(
        token: String,
        page: Int? = null,
        size: Int? = null
    ): Result<List<ShoppingListResponse>> =
        safeApiCall {
            val element = api.getLists(bearer(token), page, size)
            parseListsPayload(element)
        }

    private fun parseListsPayload(element: JsonElement): List<ShoppingListResponse> {
        return parsePayload(element, listType, arrayKeys = arrayOf("content", "data", "lists", "items", "results"))
    }

    private fun parseItemsPayload(element: JsonElement): List<ShoppingListItemResponse> {
        return parsePayload(element, itemsType, arrayKeys = arrayOf("items", "content", "data", "listItems", "results"))
    }

    private fun <T> parsePayload(element: JsonElement, type: Type, arrayKeys: Array<String>): List<T> {
        val arrayElement = when {
            element.isJsonArray -> element
            element.isJsonObject -> findArrayNode(element.asJsonObject, arrayKeys)
            else -> null
        } ?: throw ApiException(0, "Formato desconocido al obtener datos.")

        return gson.fromJson(arrayElement, type)
    }

    private fun findArrayNode(obj: JsonObject, keys: Array<String>): JsonElement? {
        keys.forEach { key ->
            if (obj.has(key)) {
                val candidate = obj.get(key)
                if (candidate.isJsonArray) return candidate
                if (candidate.isJsonObject) {
                    findArrayNode(candidate.asJsonObject, keys)?.let { return it }
                }
            }
        }
        return obj.entrySet().firstOrNull { it.value.isJsonArray }?.value
    }

    suspend fun getList(token: String, listId: Long): Result<ShoppingListResponse> =
        safeApiCall { api.getList(bearer(token), listId) }

    suspend fun createList(
        token: String,
        name: String,
        description: String? = null,
        recurring: Boolean = false
    ): Result<ShoppingListResponse> =
        safeApiCall {
            api.createList(
                bearer(token),
                CreateListRequest(name, description, recurring)
            )
        }

    suspend fun updateList(token: String, listId: Long, name: String, recurring: Boolean? = null): Result<ShoppingListResponse> =
        safeApiCall { api.updateList(bearer(token), listId, UpdateListRequest(name, recurring)) }

    suspend fun deleteList(token: String, listId: Long): Result<Unit> =
        safeApiCall { api.deleteList(bearer(token), listId) }

    suspend fun addItem(
        token: String,
        listId: Long,
        productId: Long,
        quantity: Int? = 1,
        unit: String? = "kg"
    ): Result<ShoppingListItemResponse> =
        safeApiCall {
            api.addItem(
                bearer(token),
                listId,
                CreateItemRequest(
                    product = ProductRef(id = productId),
                    quantity = quantity,
                    unit = unit,
                    metadata = emptyMap()
                )
            )
        }

    suspend fun getItems(
        token: String,
        listId: Long,
        purchased: Boolean? = null,
        page: Int? = null,
        perPage: Int? = null,
        sortBy: String? = null,
        order: String? = null,
        pantryId: Long? = null,
        categoryId: Long? = null,
        search: String? = null
    ): Result<List<ShoppingListItemResponse>> =
        safeApiCall {
            val response = api.getItems(
                authorization = bearer(token),
                listId = listId,
                purchased = purchased,
                page = page,
                perPage = perPage,
                sortBy = sortBy,
                order = order,
                pantryId = pantryId,
                categoryId = categoryId,
                search = search
            )
            response.data
        }

    suspend fun updateItem(
        token: String,
        listId: Long,
        itemId: Long,
        quantity: Int? = 1,
        unit: String? = "kg"
    ): Result<ShoppingListItemResponse> =
        safeApiCall {
            api.updateItem(
                bearer(token),
                listId,
                itemId,
                UpdateItemRequest(
                    quantity,
                    unit,
                    metadata = emptyMap()
                )
            )
        }

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

    suspend fun deleteItem(token: String, listId: Long, itemId: Long): Result<Unit> =
        safeApiCall { api.deleteItem(bearer(token), listId, itemId) }

    suspend fun addUserToList(
        token: String,
        listId: Long,
        userId: Long? = null,
        email: String? = null
    ): Result<Unit> =
        safeApiCall {
            api.addUserToList(
                bearer(token),
                listId,
                AddUserToListRequest(userId = userId, email = email)
            )
        }

    suspend fun removeUserFromList(token: String, listId: Long, userId: Long): Result<Unit> =
        safeApiCall { api.removeUserFromList(bearer(token), listId, userId) }

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
                val response = error.response()
                val bodyMsg = try {
                    response?.errorBody()?.string()?.takeIf { it.isNotBlank() }
                } catch (_: Exception) {
                    null
                }

                val requestPath = try {
                    response?.raw()?.request?.url?.encodedPath
                } catch (_: Exception) { null }

                val default = getDefaultHttpErrorMessage(error.code())
                val combined = listOfNotNull(default, bodyMsg, requestPath?.let { "path: $it" }).joinToString(" — ")

                ApiException(
                    statusCode = error.code(),
                    message = combined,
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