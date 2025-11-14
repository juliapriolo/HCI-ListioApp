package com.hci_listio_app.data.remote.api


import com.hci_listio_app.data.remote.dto.CategoryCreateRequest
import com.hci_listio_app.data.remote.dto.CategoryResponse
import com.hci_listio_app.data.remote.dto.CategoryListResponse
import retrofit2.http.*

interface CategoryApiService {

    @GET("categories")
    suspend fun getCategories(@Header("Authorization") auth: String): CategoryListResponse

    @POST("categories")
    suspend fun createCategory(
        @Header("Authorization") auth: String,
        @Body body: CategoryCreateRequest
    ): CategoryResponse
}
