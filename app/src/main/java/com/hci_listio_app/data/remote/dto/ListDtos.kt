package com.hci_listio_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// Respuesta de lista
data class ShoppingListResponse(
    val id: Long = 0L,
    val name: String = "",
    val description: String? = null,
    val recurring: Boolean? = null,
    val metadata: Map<String, Any?>? = null,
    val owner: UserProfileResponse? = null,
    @SerializedName("sharedWith")
    val sharedWith: List<UserProfileResponse> = emptyList(),
    val lastPurchasedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val items: List<ShoppingListItemResponse> = emptyList(),
    // Algunos endpoints devuelven "users" y otros "sharedWith"; mantenemos ambos por compatibilidad
    val users: List<UserProfileResponse> = emptyList()
)

// Respuesta de item de lista (según la API real)
data class ShoppingListItemResponse(
    val id: Long = 0L,
    val productName: String = "",
    val quantity: Int? = null,
    val purchased: Boolean = false,
    val productId: Long? = null,
    val categoryId: Long? = null,
    val pantryId: Long? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val lastPurchasedAt: String? = null
)

// Request para crear una lista
data class CreateListRequest(
    val name: String,
    val description: String? = null,
    val recurring: Boolean = false  // Agregar este campo
)

// Request para actualizar una lista
data class UpdateListRequest(
    val name: String
)

data class ProductRef(
    @SerializedName("id")
    val id: Long
)

// Modificar CreateItemRequest
data class CreateItemRequest(
    @SerializedName("product")
    val product: ProductRef,
    @SerializedName("quantity")
    val quantity: Int? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("metadata")
    val metadata: Map<String, Any>? = emptyMap()
)

// Request para actualizar un item (según la API real)
data class UpdateItemRequest(
    val productName: String,
    val quantity: Int? = null,
    val productId: Long? = null,
    val categoryId: Long? = null
)

// Request para toggle purchased status
data class TogglePurchasedRequest(
    val purchased: Boolean
)

// Request para añadir usuario a lista
data class AddUserToListRequest(
    val userId: Long? = null,
    val email: String? = null
)

// Respuesta paginada de listas
data class PaginatedListsResponse(
    val content: List<ShoppingListResponse>,
    val page: Int? = null,
    val size: Int? = null,
    val totalElements: Int? = null,
    val totalPages: Int? = null,
    val isFirst: Boolean? = null,
    val isLast: Boolean? = null
)