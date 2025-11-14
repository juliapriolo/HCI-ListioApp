package com.hci_listio_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// ---------- REQUEST ----------
data class ProductRequest(
    val name: String,
    val category: CategoryRef,
    val metadata: Map<String, Any>? = emptyMap()
)

data class CategoryRef(
    val id: Long
)


// ---------- RESPONSE ----------
data class ProductResponse(
    val id: Long,
    val name: String,
    val metadata: Map<String, Any>?,
    val createdAt: String?,
    val updatedAt: String?,
    val category: CategoryResponse
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val metadata: Map<String, Any>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ProductsListResponse(
    val data: List<ProductResponse>,
    val pagination: PaginationResponse
)

data class PaginationResponse(
    val total: Int,
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("has_next") val hasNext: Boolean,
    @SerializedName("has_prev") val hasPrev: Boolean
)


