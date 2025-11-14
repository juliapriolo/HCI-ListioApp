package com.hci_listio_app.data.remote.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse

interface ProductApiService {

    @POST("products") // SE DEBE USAR ASÍ
    suspend fun addProduct(
        @Header("Authorization") authorization: String,
        @Body product: ProductRequest
    ): ProductResponse
}
