package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.hci_listio_app.ui.Components.ListItemData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListViewModel : ViewModel() {

    // Estado de los items de la lista
    private val _items = MutableStateFlow(
        listOf(
            ListItemData("1", "Servilletas", isChecked = true),
            ListItemData("2", "Globos", isChecked = false),
            ListItemData("3", "Hamburguesas - 24 unidades", isChecked = false),
            ListItemData("4", "Queso - 500 gramos", isChecked = false),
            ListItemData("5", "Coca Cola - 10 Botellas de 2,25", isChecked = false),
            ListItemData("6", "Cebollas - 3 unidades", isChecked = false),
            ListItemData("7", "Helado - 5 kg", isChecked = false)
        )
    )
    val items: StateFlow<List<ListItemData>> = _items.asStateFlow()

    // Contador de items completados
    val completedCount: StateFlow<Int> = MutableStateFlow(0).apply {
        value = _items.value.count { it.isChecked }
    }

    // Total de items
    val totalCount: StateFlow<Int> = MutableStateFlow(0).apply {
        value = _items.value.size
    }

    // Actualizar el estado de check de un item
    fun toggleItemCheck(itemId: String, isChecked: Boolean) {
        _items.value = _items.value.map { item ->
            if (item.id == itemId) {
                item.copy(isChecked = isChecked)
            } else {
                item
            }
        }
        updateCounters()
    }

    // Agregar un nuevo item
    fun addItem(name: String) {
        val newId = (_items.value.size + 1).toString()
        val newItem = ListItemData(newId, name, isChecked = false)
        _items.value = _items.value + newItem
        updateCounters()
    }

    // Editar un item existente
    fun editItem(itemId: String, newName: String, quantity: String, unit: String, brand: String, store: String) {
        _items.value = _items.value.map { item ->
            if (item.id == itemId) {
                // Por ahora solo actualizamos el nombre, pero podrías expandir ListItemData
                // para incluir quantity, unit, brand, store
                item.copy(name = newName)
            } else {
                item
            }
        }
    }

    // Eliminar un item
    fun deleteItem(itemId: String) {
        _items.value = _items.value.filter { it.id != itemId }
        updateCounters()
    }

    // Actualizar contadores
    private fun updateCounters() {
        (completedCount as MutableStateFlow).value = _items.value.count { it.isChecked }
        (totalCount as MutableStateFlow).value = _items.value.size
    }
}