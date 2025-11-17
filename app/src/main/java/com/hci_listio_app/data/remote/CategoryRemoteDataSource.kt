package com.hci_listio_app.data.remote

import com.hci_listio_app.data.remote.api.CategoryApiService
import com.hci_listio_app.data.remote.dto.*

class CategoryRemoteDataSource(
    private val api: CategoryApiService = NetworkModule.categoryApiService
) {

    suspend fun getCategories(token: String): Result<List<CategoryResponse>> =
        safeApi { api.getCategories("Bearer $token").data }

    suspend fun createCategory(token: String, name: String): Result<CategoryResponse> =
        safeApi { api.createCategory("Bearer $token", CategoryCreateRequest(name)) }

    suspend fun deleteCategory(token: String, id: Long): Result<Unit> =
        safeApi { api.deleteCategory("Bearer $token", id) }

    private suspend fun <T> safeApi(block: suspend () -> T): Result<T> =
        try { Result.success(block()) }
        catch (e: Exception) { Result.failure(e) }
}
