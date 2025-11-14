package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.ProductRepository
import com.hci_listio_app.data.model.Product
import com.hci_listio_app.data.repository.DefaultCategoriesInitializer
import com.hci_listio_app.ui.Components.Categoria
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val initializer: DefaultCategoriesInitializer,
    private val repository: ProductRepository,
    private val token: String
) : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults

    init {
        viewModelScope.launch {
            val list = initializer.loadOrCreate(token)
            _categorias.value = list
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            val result = repository.searchProducts(token, query)
            result.fold(
                onSuccess = { _searchResults.value = it },
                onFailure = { _searchResults.value = emptyList() }
            )
        }
    }
}
