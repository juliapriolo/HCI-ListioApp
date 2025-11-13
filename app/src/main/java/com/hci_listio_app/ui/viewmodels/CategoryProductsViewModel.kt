package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class CategoryProductsViewModel : ViewModel() {
    // Estado de ejemplo: lista de productos (vacía por ahora)
    private val _products = mutableStateOf<List<String>>(emptyList())
    val products: State<List<String>> = _products

    // Estado para mostrar el diálogo
    var showDialog by mutableStateOf(false)
        private set

    fun onAddProductClicked() {
        showDialog = true
    }

    fun onDialogDismiss() {
        showDialog = false
    }

    fun onProductSaved(product: String) {
        // Aquí iría la lógica para guardar el producto
        _products.value = _products.value + product
        showDialog = false
    }
}
