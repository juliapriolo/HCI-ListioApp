package com.hci_listio_app.data.repository

import com.hci_listio_app.ui.Components.Categoria
import com.hci_listio_app.data.remote.CategoryRemoteDataSource

class CategoryRepository(
    private val remote: CategoryRemoteDataSource
) {

    suspend fun getCategories(token: String): Result<List<Categoria>> {
        val result = remote.getCategories(token)
        return result.map { list ->
            list.map { res ->
                Categoria(
                    id = res.id,
                    nombre = res.name,
                    imagenRes = com.hci_listio_app.R.drawable.ic_categoria_default
                )
            }
        }
    }

    suspend fun createCategory(token: String, name: String): Result<Categoria> {
        val result = remote.createCategory(token, name)
        return result.map { res ->
            Categoria(
                id = res.id,
                nombre = res.name,
                imagenRes = com.hci_listio_app.R.drawable.ic_categoria_default
            )
        }
    }
}
