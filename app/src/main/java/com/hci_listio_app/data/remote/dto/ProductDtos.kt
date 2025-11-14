package com.hci_listio_app.data.remote.dto

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

