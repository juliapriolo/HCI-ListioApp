package com.hci_listio_app.data.repository

import com.hci_listio_app.R
import com.hci_listio_app.data.remote.CategoryRemoteDataSource
import com.hci_listio_app.ui.Components.Categoria

class CategoryRepository(
    private val remote: CategoryRemoteDataSource
) {

    private val predefinedImages = mapOf(
        "Bebidas" to R.drawable.bebidas,
        "Carnes y pescados" to R.drawable.carnes,
        "Lácteos" to R.drawable.lacteos,
        "Limpieza y Hogar" to R.drawable.limpieza,
        "Verdulería" to R.drawable.verduleria
    )

    suspend fun getCategories(token: String): Result<List<Categoria>> {
        return remote.getCategories(token).map { list ->
            list.map { apiCat ->
                Categoria(
                    id = apiCat.id,
                    nombre = apiCat.name,
                    imagenRes = predefinedImages[apiCat.name] 
                        ?: R.drawable.ic_categoria_default
                )
            }
        }
    }

    suspend fun deleteCategory(token: String, id: Long): Result<Unit> {
        return remote.deleteCategory(token, id)
    }

    suspend fun createCategory(token: String, name: String): Result<Categoria> {
        return remote.createCategory(token, name).map { apiCat ->
            Categoria(
                id = apiCat.id,
                nombre = apiCat.name,
                imagenRes = R.drawable.ic_categoria_default // siempre default
            )
        }
    }
}
