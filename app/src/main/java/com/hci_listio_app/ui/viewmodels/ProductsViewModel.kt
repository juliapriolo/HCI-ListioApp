package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.ProductRepository
import com.hci_listio_app.data.model.Product
import com.hci_listio_app.data.repository.CategoryRepository
import com.hci_listio_app.ui.Components.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(
    val categoryRepo: CategoryRepository,
    private val productRepo: ProductRepository,
    private val token: String
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    fun deleteCategory(categoria: Categoria) {
        viewModelScope.launch {
            categoryRepo.deleteCategory(token, categoria.id).fold(
                onSuccess = {
                    _categorias.value = _categorias.value.filter { it.id != categoria.id }
                },
                onFailure = { }
            )
        }
    }

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            categoryRepo.getCategories(token).fold(
                onSuccess = { _categorias.value = it },
                onFailure = { }
            )
            _isLoading.value = false
        }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            categoryRepo.createCategory(token, name).fold(
                onSuccess = { newCat ->
                    _categorias.value = _categorias.value + newCat
                },
                onFailure = { }
            )
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            productRepo.searchProducts(token, query).fold(
                onSuccess = { products ->
                    // Filtrado local por nombre (case-insensitive)
                    val filtered = products.filter { it.name.contains(query, ignoreCase = true) }
                    _searchResults.value = filtered
                },
                onFailure = { _searchResults.value = emptyList() }
            )
        }
    }
}
