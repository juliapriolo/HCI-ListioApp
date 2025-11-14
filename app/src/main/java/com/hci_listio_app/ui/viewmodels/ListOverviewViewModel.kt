package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val errorMessage: String? = null
)

class ListOverviewViewModel(
    private val listRepository: ListRepository = ListRepositoryProvider.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListOverviewUiState())
    val uiState: StateFlow<ListOverviewUiState> = _uiState.asStateFlow()

    init {
        loadLists()
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
}