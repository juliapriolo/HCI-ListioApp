package com.hci_listio_app.data.remote.dto

data class ProductRequest(
    val name: String,
    val category: CategoryId,
    val metadata: Map<String, Any>? = null
)

data class CategoryId(
    val id: Long
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val category: CategoryResponse,
    val metadata: Map<String, Any>?,
    val createdAt: String?,
    val updatedAt: String?
)

data class CategoryResponse(
    val id: Long,
    val name: String
)
