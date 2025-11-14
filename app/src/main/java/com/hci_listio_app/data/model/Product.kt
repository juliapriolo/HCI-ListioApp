package com.hci_listio_app.data.model

data class Product(
    val id: Long,
    val name: String,
    val categoryId: Long,
    val categoryName: String,
    val metadata: Map<String, Any>?,
    val createdAt: String?,
    val updatedAt: String?
)
