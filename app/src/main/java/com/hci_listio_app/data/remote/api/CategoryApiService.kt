package com.hci_listio_app.data.remote.api

import com.hci_listio_app.data.remote.dto.*
import retrofit2.http.*

interface CategoryApiService {

    @GET("categories")
    suspend fun getCategories(
        @Header("Authorization") auth: String
    ): CategoryListResponse

    @POST("categories")
    suspend fun createCategory(
        @Header("Authorization") auth: String,
        @Body body: CategoryCreateRequest
    ): CategoryResponse

    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") auth: String,
        @Path("id") id: Long
    )
}
