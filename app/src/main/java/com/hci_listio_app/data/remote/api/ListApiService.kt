package com.hci_listio_app.data.remote.api

import com.hci_listio_app.data.remote.dto.*
import retrofit2.http.*

interface ListApiService {

    // Obtener todas las listas del usuario
    @GET("api/shopping-lists")
    suspend fun getLists(
        @Header("Authorization") authorization: String
    ): List<ShoppingListResponse>

    // Obtener una lista específica
    @GET("api/shopping-lists/{id}")
    suspend fun getList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long
    ): ShoppingListResponse

    // Crear una nueva lista
    @POST("api/shopping-lists")
    suspend fun createList(
        @Header("Authorization") authorization: String,
        @Body payload: CreateListRequest
    ): ShoppingListResponse

    // Actualizar una lista
    @PUT("api/shopping-lists/{id}")
    suspend fun updateList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Body payload: UpdateListRequest
    ): ShoppingListResponse

    // Eliminar una lista
    @DELETE("api/shopping-lists/{id}")
    suspend fun deleteList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long
    )

    // Agregar item a lista
    @POST("api/shopping-lists/{id}/items")
    suspend fun addItem(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Body payload: CreateItemRequest
    ): ShoppingListItemResponse

    // Obtener items de lista
    @GET("api/shopping-lists/{id}/items")
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

    // Actualizar item
    @PUT("api/shopping-lists/{id}/items/{item_id}")
    suspend fun updateItem(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long,
        @Body payload: UpdateItemRequest
    ): ShoppingListItemResponse

    // Toggle purchased status
    @PATCH("api/shopping-lists/{id}/items/{item_id}")
    suspend fun togglePurchased(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long,
        @Body payload: TogglePurchasedRequest
    ): ShoppingListItemResponse

    // Eliminar item
    @DELETE("api/shopping-lists/{id}/items/{item_id}")
    suspend fun deleteItem(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long
    )

    // Añadir usuario a lista
    @POST("api/shopping-lists/{id}/share")
    suspend fun addUserToList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Body payload: AddUserToListRequest
    )

    // Remover usuario de lista
    @DELETE("api/shopping-lists/{id}/share/{user_id}")
    suspend fun removeUserFromList(
        @Header("Authorization") authorization: String,
        @Path("id") listId: Long,
        @Path("user_id") userId: Long
    )
}