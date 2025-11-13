package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductsViewModel : ViewModel() {

    // Estado inmutable expuesto a la UI
    private val _categorias = MutableStateFlow(
        listOf(
            Categoria("Bebidas", R.drawable.bebidas),
            Categoria("Carnes y pescados", R.drawable.carnes),
            Categoria("Lácteos", R.drawable.lacteos),
            Categoria("Limpieza y Hogar", R.drawable.limpieza),
            Categoria("Verdulería", R.drawable.verduleria)
        )
    )
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    // En un futuro podrías cargar categorías desde una API o base de datos aquí.
}
