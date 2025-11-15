package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hci_listio_app.data.ProductRepository
import com.hci_listio_app.data.remote.CategoryRemoteDataSource
import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.repository.CategoryRepository

class ProductsViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val categoryRepo = CategoryRepository(CategoryRemoteDataSource())
        val productRepo = ProductRepository(ProductRemoteDataSource())

        return ProductsViewModel(
            categoryRepo = categoryRepo,
            productRepo = productRepo,
            token = token
        ) as T
    }
}
