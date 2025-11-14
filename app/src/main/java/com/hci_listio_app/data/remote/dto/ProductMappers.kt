package com.hci_listio_app.data.remote.dto


import com.hci_listio_app.data.model.Product

fun ProductResponse.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        categoryId = this.category.id,
        categoryName = this.category.name,
        metadata = this.metadata,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
