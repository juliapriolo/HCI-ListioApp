package com.hci_listio_app.data.remote

import com.hci_listio_app.data.remote.api.ProductApiService
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductRemoteDataSource(
    private val api: ProductApiService = NetworkModule.productApiService
) {

    // 🔹 CREAR PRODUCTO
    suspend fun addProduct(token: String, product: ProductRequest): Result<ProductResponse> =
        safeApiCall {
            api.addProduct("Bearer $token", product)
        }

    // 🔹 BUSCAR PRODUCTOS POR NOMBRE
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


    // 🔹 OBTENER PRODUCTOS POR CATEGORÍA (LO QUE TE FALTABA)
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

    // 🔹 ELIMINAR PRODUCTO (LO QUE TE FALTABA)
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

    // 🔹 SAFE API CALL
    private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
}
