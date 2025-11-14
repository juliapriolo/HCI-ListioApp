package com.hci_listio_app.data

import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse

class ProductRepository(private val remoteDataSource: ProductRemoteDataSource) {
    suspend fun addProduct(token: String, product: ProductRequest): Result<ProductResponse> {
        return remoteDataSource.addProduct(token, product)
    }
}
