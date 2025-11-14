package com.hci_listio_app.data.repository

import com.hci_listio_app.R
import com.hci_listio_app.data.remote.NetworkModule
import com.hci_listio_app.data.remote.dto.CategoryCreateRequest
import com.hci_listio_app.ui.Components.Categoria

class DefaultCategoriesInitializer {

    private val predefined = listOf(
        "Bebidas" to R.drawable.bebidas,
        "Carnes y pescados" to R.drawable.carnes,
        "Lácteos" to R.drawable.lacteos,
        "Limpieza y Hogar" to R.drawable.limpieza,
        "Verdulería" to R.drawable.verduleria
    )

    suspend fun loadOrCreate(token: String): List<Categoria> {

        val api = NetworkModule.categoryApiService
        val auth = "Bearer $token"

        val existing = api.getCategories(auth).data
        val result = mutableListOf<Categoria>()

        for ((name, image) in predefined) {
            val found = existing.find { it.name.equals(name, ignoreCase = true) }

            if (found != null) {
                // Ya existe en backend
                result.add(Categoria(found.id, name, image))
            } else {
                // No existe → crear
                val created = api.createCategory(auth, CategoryCreateRequest(name))
                result.add(Categoria(created.id, name, image))
            }
        }

        return result
    }
}
