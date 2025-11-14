package com.hci_listio_app.data.repository

import com.hci_listio_app.data.model.Product
import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.ProductResponse

class CategoryProductsRepository(
    private val remoteDataSource: ProductRemoteDataSource
) {

    suspend fun addProduct(token: String, request: ProductRequest): Result<Product> {
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

    suspend fun getProducts(token: String, categoryId: Long): Result<List<Product>> {
        val responseResult = remoteDataSource.getProductsByCategory(token, categoryId)

        return responseResult.map { list ->
            list.map { response ->
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




}
