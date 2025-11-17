package com.hci_listio_app.data.remote.dto

import com.google.gson.annotations.SerializedName

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
    val users: List<UserProfileResponse> = emptyList()
)

data class ShoppingListItemResponse(
    val id: Long = 0L,
    val quantity: Int? = 1,
    val unit: String? = "",
    val metadata: Map<String, Any?>? = null,
    val purchased: Boolean = false,
    val lastPurchasedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val product: ProductResponse? = null,
)

data class ShoppingListItemsResponse(
    val data: List<ShoppingListItemResponse>,
    val pagination: PaginationResponse
)

data class CreateListRequest(
    val name: String,
    val description: String? = null,
    val recurring: Boolean = false
)

data class UpdateListRequest(
    val name: String,
    val recurring: Boolean? = null
)

data class ProductRef(
    @SerializedName("id")
    val id: Long
)

data class CreateItemRequest(
    val product: ProductRef,
    val quantity: Int? = 1,
    val unit: String? = "kg",
    val metadata: Map<String, Any>? = emptyMap()
)

data class UpdateItemRequest(
    val quantity: Int? = 1,
    val unit: String? = "kg",
    val metadata: Map<String, Any>? = emptyMap()
)

data class TogglePurchasedRequest(
    val purchased: Boolean
)

data class AddUserToListRequest(
    val userId: Long? = null,
    val email: String? = null
)

data class PaginatedListsResponse(
    val content: List<ShoppingListResponse>,
    val page: Int? = null,
    val size: Int? = null,
    val totalElements: Int? = null,
    val totalPages: Int? = null,
    val isFirst: Boolean? = null,
    val isLast: Boolean? = null
)