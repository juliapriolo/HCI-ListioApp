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

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            categoryRepo.getCategories(token).fold(
                onSuccess = { _categorias.value = it },
                onFailure = { }
            )
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
                onSuccess = { _searchResults.value = it },
                onFailure = { _searchResults.value = emptyList() }
            )
        }
    }
}
