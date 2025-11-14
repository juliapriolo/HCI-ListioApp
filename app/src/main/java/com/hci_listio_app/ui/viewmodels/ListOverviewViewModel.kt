package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.ListHistoryManager
import com.hci_listio_app.data.ListRepository
import com.hci_listio_app.data.ListRepositoryProvider
import com.hci_listio_app.data.remote.dto.ShoppingListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListOverviewUiState(
    val lists: List<ShoppingListResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favorites: Set<Long> = emptySet(),
    val archivedListIds: Set<Long> = emptySet()
)

class ListOverviewViewModel(
    private val listRepository: ListRepository = ListRepositoryProvider.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListOverviewUiState())
    val uiState: StateFlow<ListOverviewUiState> = _uiState.asStateFlow()

    init {
        loadLists()
        observeHistoryChanges()
    }

    private fun observeHistoryChanges() {
        viewModelScope.launch {
            ListHistoryManager.archivedListIds.collect { archived ->
                _uiState.update { it.copy(archivedListIds = archived) }
            }
        }
    }

    fun loadLists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = listRepository.getLists()

            _uiState.update { current ->
                if (result.isSuccess) {
                    val lists = result.getOrNull() ?: emptyList()
                    current.copy(
                        lists = lists,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar las listas."
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun toggleFavorite(listId: Long) {
        _uiState.update { current ->
            val next = current.favorites.toMutableSet()
            if (!next.add(listId)) {
                next.remove(listId)
            }
            current.copy(favorites = next)
        }
    }

    fun renameList(listId: Long, newName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = listRepository.updateList(listId, newName)
            if (result.isSuccess) {
                val updatedList = result.getOrNull()
                _uiState.update { current ->
                    current.copy(
                        lists = current.lists.map { if (it.id == listId) updatedList ?: it else it }
                    )
                }
                onResult(true, null)
            } else {
                val message = result.exceptionOrNull()?.message ?: "No se pudo actualizar la lista."
                _uiState.update { it.copy(errorMessage = message) }
                onResult(false, message)
            }
        }
    }

    fun deleteList(listId: Long, fromHistory: Boolean, onResult: (Boolean, String?) -> Unit) {
        if (!fromHistory) {
            ListHistoryManager.moveToHistory(listId)
            onResult(true, null)
            return
        }

        viewModelScope.launch {
            val result = listRepository.deleteList(listId)
            if (result.isSuccess) {
                ListHistoryManager.removeFromHistory(listId)
                _uiState.update { current ->
                    current.copy(lists = current.lists.filterNot { it.id == listId })
                }
                onResult(true, null)
            } else {
                val message = result.exceptionOrNull()?.message ?: "No se pudo eliminar la lista."
                _uiState.update { it.copy(errorMessage = message) }
                onResult(false, message)
            }
        }
    }

    fun moveToHistory(listId: Long) {
        ListHistoryManager.moveToHistory(listId)
    }

    fun restoreList(listId: Long) {
        ListHistoryManager.removeFromHistory(listId)
    }
}