package com.hci_listio_app.data.remote.api

import com.hci_listio_app.data.remote.dto.*
import retrofit2.http.*

interface ListApiService {

    @GET("shopping-lists")
    suspend fun getLists(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): PaginatedListsResponse

    @GET("shopping-lists/{id}")
    suspend fun getList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long
    ): ShoppingListResponse

    @POST("shopping-lists")
    suspend fun createList(
        @Header("Authorization") authorization: String,
        @Body payload: CreateListRequest
    ): ShoppingListResponse

    @PUT("shopping-lists/{id}")
    suspend fun updateList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Body payload: UpdateListRequest
    ): ShoppingListResponse

    @DELETE("shopping-lists/{id}")
    suspend fun deleteList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long
    )

    @POST("shopping-lists/{id}/items")
    suspend fun addItem(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Body payload: CreateItemRequest
    ): ShoppingListItemResponse

    @GET("shopping-lists/{id}/items")
    suspend fun getItems(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Query("purchased") purchased: Boolean? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("order") order: String? = null,
        @Query("pantry_id") pantryId: Long? = null,
        @Query("category_id") categoryId: Long? = null,
        @Query("search") search: String? = null
    ): List<ShoppingListItemResponse>

    @PUT("shopping-lists/{id}/items/{item_id}")
    suspend fun updateItem(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long,
        @Body payload: UpdateItemRequest
    ): ShoppingListItemResponse

    @PATCH("shopping-lists/{id}/items/{item_id}")
    suspend fun togglePurchased(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long,
        @Body payload: TogglePurchasedRequest
    ): ShoppingListItemResponse

    @DELETE("shopping-lists/{id}/items/{item_id}")
    suspend fun deleteItem(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long
    )

    @POST("shopping-lists/{id}/share")
    suspend fun addUserToList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Body payload: AddUserToListRequest
    )

    @DELETE("shopping-lists/{id}/share/{user_id}")
    suspend fun removeUserFromList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Path("user_id") userId: Long
    )
}