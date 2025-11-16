package com.hci_listio_app.data.repository

import com.hci_listio_app.R
import com.hci_listio_app.data.remote.CategoryRemoteDataSource
import com.hci_listio_app.data.remote.dto.CategoryResponse
import com.hci_listio_app.ui.Components.Categoria
import java.text.Normalizer
import java.util.Locale

private val accentRegex = "\\p{Mn}+".toRegex()

private fun normalizeCategoryName(value: String): String {
    val normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
    return accentRegex.replace(normalized, "").lowercase(Locale.ROOT)
}

private data class DefaultCategory(
    val name: String,
    val imageRes: Int,
    val normalizedName: String = normalizeCategoryName(name)
)

class CategoryRepository(
    private val remote: CategoryRemoteDataSource
) {

    private val defaultCategories = listOf(
        DefaultCategory("Bebidas", R.drawable.bebidas),
        DefaultCategory("Carnes y pescados", R.drawable.carnes),
        DefaultCategory("L\u00E1cteos", R.drawable.lacteos),
        DefaultCategory("Limpieza y Hogar", R.drawable.limpieza),
        DefaultCategory("Verduler\u00EDa", R.drawable.verduleria),
        DefaultCategory("Cuidado personal", R.drawable.cuidadopersonal),
        DefaultCategory("Mascotas", R.drawable.mascotas),
        DefaultCategory("Panadería", R.drawable.panaderia),
        DefaultCategory("Snacks", R.drawable.snacks),
        DefaultCategory("Congelados", R.drawable.congelados),
        DefaultCategory("Despensa", R.drawable.despensa),
        DefaultCategory("Bebés", R.drawable.bebes)
    )
    private val defaultImagesByName = defaultCategories.associate { it.normalizedName to it.imageRes }

    suspend fun getCategories(token: String): Result<List<Categoria>> {
        val remoteResult = remote.getCategories(token)
        val apiCategories = remoteResult.getOrNull()?.toMutableList()
            ?: return Result.failure(
                remoteResult.exceptionOrNull() ?: IllegalStateException("Unknown error fetching categories")
            )

        val mapped = apiCategories
            .map(::mapToCategoria)
            .sortedWith(
                compareByDescending<Categoria> { it.isDefault }
                    .thenBy { it.nombre.lowercase(Locale.ROOT) }
            )

        return Result.success(mapped)
    }

    suspend fun deleteCategory(token: String, id: Long): Result<Unit> {
        return remote.deleteCategory(token, id)
    }

    suspend fun createCategory(token: String, name: String): Result<Categoria> {
        return remote.createCategory(token, name).map(::mapToCategoria)
    }

    private suspend fun ensureDefaultCategories(
        token: String,
        categories: MutableList<CategoryResponse>
    ): Result<Unit> {
        val existing = categories
            .map { normalizeCategoryName(it.name) }
            .toMutableSet()

        for (defaultCategory in defaultCategories) {
            if (defaultCategory.normalizedName !in existing) {
                val creation = remote.createCategory(token, defaultCategory.name)
                creation.fold(
                    onSuccess = { created ->
                        categories.add(created)
                        existing.add(normalizeCategoryName(created.name))
                    },
                    onFailure = { return Result.failure(it) }
                )
            }
        }

        return Result.success(Unit)
    }

    private fun mapToCategoria(response: CategoryResponse): Categoria {
        val normalizedName = normalizeCategoryName(response.name)
        val imageRes = defaultImagesByName[normalizedName] ?: R.drawable.ic_categoria_default
        val isDefault = normalizedName in defaultImagesByName
        return Categoria(
            id = response.id,
            nombre = response.name,
            imagenRes = imageRes,
            isDefault = isDefault
        )
    }
}
