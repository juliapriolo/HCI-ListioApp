package com.hci_listio_app.data.remote.dto

// Respuesta de lista
data class ShoppingListResponse(
    val id: Long,
    val name: String,
    val items: List<ShoppingListItemResponse>,
    val users: List<UserProfileResponse>,
    val createdAt: String,
    val updatedAt: String
)

// Respuesta de item de lista (según la API real)
data class ShoppingListItemResponse(
    val id: Long,
    val productName: String,
    val quantity: Int? = null,
    val purchased: Boolean = false,
    val productId: Long? = null,
    val categoryId: Long? = null,
    val pantryId: Long? = null,
    val createdAt: String,
    val updatedAt: String,
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

// Request para crear un item (según la API real)
data class CreateItemRequest(
    val productName: String,
    val quantity: Int? = null,
    val productId: Long? = null,
    val categoryId: Long? = null
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
    val userId: Long
)

// Respuesta paginada de listas
data class PaginatedListsResponse(
    val content: List<ShoppingListResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean
)