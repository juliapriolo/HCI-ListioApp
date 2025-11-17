package com.hci_listio_app.data.repository

import com.hci_listio_app.data.model.Product
import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.remote.NetworkModule
import com.hci_listio_app.data.remote.dto.CategoryRef
import com.hci_listio_app.data.remote.dto.ProductRequest

class AllProductsRepository(
    private val remoteDataSource: ProductRemoteDataSource = ProductRemoteDataSource(NetworkModule.productApiService)
) {

    suspend fun getAllProducts(token: String): Result<List<Product>> {
        return try {
            val categoriesResult = NetworkModule.categoryApiService.getCategories("Bearer $token")

            val allProducts = mutableListOf<Product>()

            categoriesResult.data.forEach { category ->
                val productsResult = remoteDataSource.getProductsByCategory(token, category.id)
                productsResult.getOrNull()?.let { productResponses ->
                    val products = productResponses.map { response ->
                        Product(
                            id = response.id,
                            name = response.name,
                            categoryId = response.category.id,
                            categoryName = response.category.name,
                            metadata = response.metadata,
                            createdAt = response.createdAt,
                            updatedAt = response.updatedAt
                        )
                    }
                    allProducts.addAll(products)
                }
            }

            Result.success(allProducts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(token: String, productName: String, categoryId: Long): Result<Product> {
        val request = ProductRequest(
            name = productName,
            category = CategoryRef(id = categoryId),
            metadata = emptyMap()
        )

        val responseResult = remoteDataSource.addProduct(token, request)

        return responseResult.map { response ->
            Product(
                id = response.id,
                name = response.name,
                categoryId = response.category.id,
                categoryName = response.category.name,
                metadata = response.metadata,
                createdAt = response.createdAt,
                updatedAt = response.updatedAt
            )
        }
    }
}