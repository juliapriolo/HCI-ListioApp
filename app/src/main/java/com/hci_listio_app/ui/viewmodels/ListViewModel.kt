package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.ListRepository
import com.hci_listio_app.data.ListRepositoryProvider
import com.hci_listio_app.ui.Components.ListItemData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListUiState(
    val listId: Long? = null,
    val listName: String = "",
    val items: List<ListItemData> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ListViewModel(
    private val listRepository: ListRepository = ListRepositoryProvider.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    // Propiedades de compatibilidad con la implementación anterior
    private val _items = MutableStateFlow<List<ListItemData>>(emptyList())
    val items: StateFlow<List<ListItemData>> = _items

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount

    init {
        viewModelScope.launch {
            _uiState.collect { state ->
                _items.value = state.items
                _completedCount.value = state.completedCount
                _totalCount.value = state.totalCount
            }
        }
    }

    // Cargar items de la lista desde la API
    fun loadList(listId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, listId = listId) }

            val result = listRepository.getItems(listId)

            _uiState.update { current ->
                if (result.isSuccess) {
                    val itemsData = result.getOrNull()!!
                    val items = itemsData.map { item ->
                        ListItemData(
                            id = item.id.toString(),
                            name = item.productName,
                            isChecked = item.purchased
                        )
                    }
                    current.copy(
                        items = items,
                        completedCount = items.count { it.isChecked },
                        totalCount = items.size,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar la lista."
                    )
                }
            }
        }
    }

    // Actualizar el estado de check de un item (usando PATCH para purchased)
    fun toggleItemCheck(itemId: String, isChecked: Boolean) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull() ?: return

        viewModelScope.launch {
            // Actualizar localmente primero (optimistic update)
            _uiState.update { current ->
                val updatedItems = current.items.map { item ->
                    if (item.id == itemId) item.copy(isChecked = isChecked) else item
                }
                current.copy(
                    items = updatedItems,
                    completedCount = updatedItems.count { it.isChecked }
                )
            }

            // Actualizar en la API usando togglePurchased
            val result = listRepository.togglePurchased(
                listId = listId,
                itemId = itemIdLong,
                purchased = isChecked
            )

            if (result.isFailure) {
                // Revertir el cambio si falla
                _uiState.update { current ->
                    val revertedItems = current.items.map { i ->
                        if (i.id == itemId) i.copy(isChecked = !isChecked) else i
                    }
                    current.copy(
                        items = revertedItems,
                        completedCount = revertedItems.count { it.isChecked },
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al actualizar el item."
                    )
                }
            }
        }
    }

    // Agregar un nuevo item
    fun addItem(name: String) {
        val listId = _uiState.value.listId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = listRepository.addItem(
                listId = listId,
                productName = name
            )

            _uiState.update { current ->
                if (result.isSuccess) {
                    val newItem = result.getOrNull()!!
                    val newListItem = ListItemData(
                        id = newItem.id.toString(),
                        name = newItem.productName,
                        isChecked = newItem.purchased
                    )
                    val updatedItems = current.items + newListItem
                    current.copy(
                        items = updatedItems,
                        totalCount = updatedItems.size,
                        completedCount = updatedItems.count { it.isChecked },
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al agregar el item."
                    )
                }
            }
        }
    }

    // Editar un item existente
    fun editItem(itemId: String, newName: String, quantity: String, unit: String, brand: String, store: String) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Convertir quantity de String a Int si es posible
            val quantityInt = quantity.toIntOrNull()

            val result = listRepository.updateItem(
                listId = listId,
                itemId = itemIdLong,
                productName = newName,
                quantity = quantityInt
            )

            _uiState.update { current ->
                if (result.isSuccess) {
                    val updatedItem = result.getOrNull()!!
                    val updatedItems = current.items.map { item ->
                        if (item.id == itemId) {
                            item.copy(name = updatedItem.productName)
                        } else {
                            item
                        }
                    }
                    current.copy(
                        items = updatedItems,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al editar el item."
                    )
                }
            }
        }
    }

    // Eliminar un item
    fun deleteItem(itemId: String) {
        val listId = _uiState.value.listId ?: return
        val itemIdLong = itemId.toLongOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = listRepository.deleteItem(listId, itemIdLong)

            _uiState.update { current ->
                if (result.isSuccess) {
                    val updatedItems = current.items.filter { it.id != itemId }
                    current.copy(
                        items = updatedItems,
                        totalCount = updatedItems.size,
                        completedCount = updatedItems.count { it.isChecked },
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al eliminar el item."
                    )
                }
            }
        }
    }

    // Limpiar mensaje de error
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}