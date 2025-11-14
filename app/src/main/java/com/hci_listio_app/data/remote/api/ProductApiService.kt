package com.hci_listio_app.data.remote.api

import com.hci_listio_app.data.remote.dto.ProductsListResponse
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse
import retrofit2.http.*

interface ProductApiService {

    @POST("products")
    suspend fun addProduct(
        @Header("Authorization") authorization: String,
        @Body product: ProductRequest
    ): ProductResponse

    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String,
        @Query("name") name: String? = null,
        @Query("category_id") categoryId: Long? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): ProductsListResponse

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    )

}
