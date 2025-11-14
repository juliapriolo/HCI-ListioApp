package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hci_listio_app.data.repository.CategoryProductsRepository

class CategoryProductsViewModelFactory(
    private val repository: CategoryProductsRepository,
    private val token: String,
    private val categoryId: Long
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryProductsViewModel(repository, token, categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
