package com.hci_listio_app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hci_listio_app.data.AuthRepositoryProvider
import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.repository.CategoryProductsRepository

class CategoryProductsViewModelFactory(
    private val application: Application,
    private val categoryId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryProductsViewModel::class.java)) {

            val repository = CategoryProductsRepository(
                remoteDataSource = ProductRemoteDataSource()
            )

            val token = AuthRepositoryProvider.instance.authToken.value ?: ""

            return CategoryProductsViewModel(
                application = application,
                repository = repository,
                token = token,
                categoryId = categoryId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
