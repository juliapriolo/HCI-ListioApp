package com.hci_listio_app.data.remote.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse
import com.hci_listio_app.data.remote.dto.ProductsListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {

    @POST("products") // SE DEBE USAR ASÍ
    suspend fun addProduct(
        @Header("Authorization") authorization: String,
        @Body product: ProductRequest
    ): ProductResponse

    // ProductApiService.kt
    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String,
        @Query("category_id") categoryId: Long,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50,
        @Query("sort_by") sortBy: String = "name",
        @Query("order") order: String = "ASC"
    ): ProductsListResponse

}
