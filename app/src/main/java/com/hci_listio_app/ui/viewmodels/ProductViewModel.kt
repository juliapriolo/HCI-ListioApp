package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.R
import com.hci_listio_app.data.repository.DefaultCategoriesInitializer
import com.hci_listio_app.ui.Components.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val initializer: DefaultCategoriesInitializer,
    private val token: String
) : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    init {
        viewModelScope.launch {
            val list = initializer.loadOrCreate(token)
            _categorias.value = list
        }
    }
}
