package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hci_listio_app.data.ProductRepository
import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.repository.DefaultCategoriesInitializer

class ProductsViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val remote = ProductRemoteDataSource()
        val repository = ProductRepository(remote)
        val initializer = DefaultCategoriesInitializer()

        return ProductsViewModel(
            initializer = initializer,
            repository = repository,
            token = token
        ) as T
    }
}
