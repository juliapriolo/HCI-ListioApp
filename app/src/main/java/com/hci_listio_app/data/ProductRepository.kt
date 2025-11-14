package com.hci_listio_app.data

import com.hci_listio_app.data.model.Product
import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse
import com.hci_listio_app.data.remote.dto.toDomain

class ProductRepository(
    private val remoteDataSource: ProductRemoteDataSource
) {

    suspend fun addProduct(token: String, product: ProductRequest): Result<ProductResponse> {
        return remoteDataSource.addProduct(token, product)
    }

    suspend fun searchProducts(token: String, name: String): Result<List<Product>> {
        return remoteDataSource.searchProductsByName(token, name).map { list ->
            list.map { it.toDomain() }
        }
    }
}
