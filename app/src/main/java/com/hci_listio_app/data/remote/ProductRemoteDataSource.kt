package com.hci_listio_app.data.remote

import com.hci_listio_app.data.remote.api.ProductApiService
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductRemoteDataSource(private val api: ProductApiService) {
    suspend fun addProduct(token: String, product: ProductRequest): Result<ProductResponse> =
        safeApiCall { api.addProduct("Bearer $token", product) }

    private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }

    companion object {
        fun create(baseUrl: String): ProductRemoteDataSource {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(ProductApiService::class.java)
            return ProductRemoteDataSource(api)
        }
    }
}
