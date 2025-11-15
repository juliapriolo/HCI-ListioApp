package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.model.Product
import com.hci_listio_app.data.remote.dto.CategoryRef
import com.hci_listio_app.data.remote.dto.ProductRequest
import com.hci_listio_app.data.repository.CategoryProductsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryProductsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
class CategoryProductsViewModel(
    private val repository: CategoryProductsRepository,
    private val token: String,
    private val categoryId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryProductsUiState())
    val uiState: StateFlow<CategoryProductsUiState> = _uiState.asStateFlow()

    init {
        loadProducts()   // 👈 cada vez que se crea el VM, trae del backend
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.getProducts(token, categoryId)

            result.fold(
                onSuccess = { products ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            products = products
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al cargar productos"
                        )
                    }
                }
            )
        }
    }

    fun addProduct(name: String, brand: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val metadata = if (brand != null) mapOf("brand" to brand) else emptyMap()
            val request = ProductRequest(
                name = name,
                category = CategoryRef(categoryId),
                metadata = metadata
            )

            val result = repository.addProduct(token, request)

            result.fold(
                onSuccess = { product ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            products = it.products + product
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al agregar producto"
                        )
                    }
                }
            )
        }
    }
    
    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.deleteProduct(token, id)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            products = it.products.filter { p -> p.id != id }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al eliminar producto"
                        )
                    }
                }
            )
        }
    }

}