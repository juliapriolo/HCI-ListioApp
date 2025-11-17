package com.hci_listio_app.data.remote

import com.hci_listio_app.data.remote.api.ProductApiService
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse


class ProductRemoteDataSource(
    private val api: ProductApiService = NetworkModule.productApiService
) {

    suspend fun addProduct(token: String, product: ProductRequest): Result<ProductResponse> =
        safeApiCall {
            api.addProduct("Bearer $token", product)
        }

    suspend fun searchProductsByName(
        token: String,
        name: String
    ): Result<List<ProductResponse>> =
        safeApiCall {
            api.getProducts(
                authorization = "Bearer $token",
                name = name
            ).data
        }

    suspend fun getProductsByCategory(
        token: String,
        categoryId: Long
    ): Result<List<ProductResponse>> =
        safeApiCall {
            api.getProducts(
                authorization = "Bearer $token",
                categoryId = categoryId
            ).data
        }

    suspend fun deleteProduct(
        token: String,
        id: Long
    ): Result<Unit> =
        safeApiCall {
            api.deleteProduct(
                authorization = "Bearer $token",
                id = id
            )
        }

    private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
}
