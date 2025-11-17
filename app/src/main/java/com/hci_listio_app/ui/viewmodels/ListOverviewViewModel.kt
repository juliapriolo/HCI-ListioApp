package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.ListHistoryManager
import com.hci_listio_app.data.FavoritesManager
import com.hci_listio_app.data.ListRepository
import com.hci_listio_app.data.ListRepositoryProvider
import com.hci_listio_app.data.remote.dto.ShoppingListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

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
        observeFavoritesChanges()
        syncFavoritesFromApi()
    }

    private fun observeHistoryChanges() {
        viewModelScope.launch {
            ListHistoryManager.archivedListIds.collect { archived ->
                _uiState.update { it.copy(archivedListIds = archived) }
            }
        }
    }

    private fun observeFavoritesChanges() {
        viewModelScope.launch {
            FavoritesManager.favorites.collect { favs ->
                _uiState.update { current ->
                    val (favLists, otherLists) = current.lists.partition { it.id in favs }
                    current.copy(favorites = favs, lists = favLists + otherLists)
                }
            }
        }
    }

    private fun syncFavoritesFromApi() {
        viewModelScope.launch {
            _uiState.first { it.lists.isNotEmpty() }

            val apiRecurringIds = _uiState.value.lists
                .filter { it.recurring == true }
                .map { it.id }
                .toSet()

            val localFavs = FavoritesManager.favorites.value

            if (apiRecurringIds != localFavs) {
                apiRecurringIds.forEach { FavoritesManager.addFavorite(it) }

                localFavs.forEach { localId ->
                    if (localId !in apiRecurringIds) {
                        FavoritesManager.removeFavorite(localId)
                    }
                }
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
                    val favs = FavoritesManager.favorites.value
                    val (favLists, otherLists) = lists.partition { it.id in favs }
                    current.copy(
                        lists = favLists + otherLists,
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
    suspend fun createList(name: String, description: String?, recurring: Boolean = false): Long? {
        _uiState.update { it.copy(isLoading = true) }
        val result = listRepository.createList(name, description ?: "", recurring)
        return if (result.isSuccess) {
            val created = result.getOrNull()
            created?.let {
                if (recurring) {
                    FavoritesManager.addFavorite(it.id)
                }

                _uiState.update { current ->
                    val combined = current.lists + it
                    val favs = current.favorites
                    val (favLists, otherLists) = combined.partition { list -> list.id in favs }
                    current.copy(lists = favLists + otherLists, isLoading = false)
                }
                created.id
            }
        } else {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Error al crear la lista.")
            }
            null
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun toggleFavorite(listId: Long) {
        viewModelScope.launch {
            val wasFavorite = FavoritesManager.isFavorite(listId)
            FavoritesManager.toggleFavorite(listId)

            delay(220L)

            val result = listRepository.toggleFavorite(listId, !wasFavorite)

            if (result.isFailure) {
                FavoritesManager.toggleFavorite(listId)
                _uiState.update {
                    it.copy(errorMessage = "Error al guardar favorito en el servidor")
                }
            } else {
                val updatedList = result.getOrNull()
                if (updatedList != null) {
                    _uiState.update { current ->
                        val updatedLists = current.lists.map { list ->
                            if (list.id == listId) updatedList else list
                        }
                        val favs = FavoritesManager.favorites.value
                        val (favLists, otherLists) = updatedLists.partition { it.id in favs }
                        current.copy(lists = favLists + otherLists)
                    }
                }
            }
        }
    }

    fun renameList(listId: Long, newName: String, recurring: Boolean? = null, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = listRepository.updateList(listId, newName, recurring)
            if (result.isSuccess) {
                val updatedList = result.getOrNull()
                _uiState.update { current ->
                    val updated = current.lists.map { if (it.id == listId) updatedList ?: it else it }
                    val favs = current.favorites
                    val (favLists, otherLists) = updated.partition { it.id in favs }
                    current.copy(lists = favLists + otherLists)
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