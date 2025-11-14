package com.hci_listio_app.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.hci_listio_app.data.AuthRepositoryProvider
import com.hci_listio_app.data.ProductRepository
import com.hci_listio_app.data.remote.NetworkModule
import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.remote.dto.CategoryId
import kotlinx.coroutines.launch

class CategoryProductsViewModel : ViewModel() {
    private val _products = mutableStateOf<List<String>>(emptyList())
    val products: State<List<String>> = _products

    var showDialog by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Repositorios y data source
    private val authRepository = AuthRepositoryProvider.instance
    private val productRepository = ProductRepository(
        ProductRemoteDataSource.create(NetworkModule.baseUrl)
    )

    private var currentCategoryId: Long? = null

    fun setCategoryId(categoryId: Long) {
        currentCategoryId = categoryId
    }

    fun onAddProductClicked() {
        showDialog = true
    }

    fun onDialogDismiss() {
        showDialog = false
        errorMessage = null
    }

    fun onProductSaved(product: String) {
        val categoryId = currentCategoryId
        val token = authRepository.authToken.value
        if (categoryId == null || token == null) {
            errorMessage = "Falta información de categoría o autenticación."
            return
        }
        isLoading = true
        viewModelScope.launch {
            val result = productRepository.addProduct(
                token,
                ProductRequest(
                    name = product,
                    category = CategoryId(categoryId)
                )
            )
            isLoading = false
            if (result.isSuccess) {
                _products.value = _products.value + product
                showDialog = false
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Error al agregar producto"
            }
        }
    }
}
