package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hci_listio_app.data.repository.DefaultCategoriesInitializer

class ProductsViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsViewModel(
                initializer = DefaultCategoriesInitializer(),
                token = token
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}
