package com.hci_listio_app.data.remote.dto

data class CategoryCreateRequest(
    val name: String,
    val metadata: Map<String, Any> = emptyMap()
)
data class CategoryListResponse(
    val data: List<CategoryResponse>
)
