package com.hci_listio_app.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

object ListHistoryManager {
    private val _archivedListIds = MutableStateFlow<Set<Long>>(emptySet())
    val archivedListIds: StateFlow<Set<Long>> = _archivedListIds.asStateFlow()

    private lateinit var preferences: ListHistoryPreferences
    private val scope = CoroutineScope(Dispatchers.IO)

    fun initialize(context: Context) {
        if (this::preferences.isInitialized) return
        preferences = ListHistoryPreferences(context)
        preferences.archivedIds.onEach { ids ->
            _archivedListIds.value = ids
        }.launchIn(scope)
    }

    fun moveToHistory(listId: Long) {
        updateState { current ->
            if (listId in current) current else current + listId
        }
    }

    fun removeFromHistory(listId: Long) {
        updateState { current ->
            if (listId in current) current - listId else current
        }
    }

    fun isInHistory(listId: Long): Boolean = _archivedListIds.value.contains(listId)

    private fun updateState(transform: (Set<Long>) -> Set<Long>) {
        val newState = transform(_archivedListIds.value)
        _archivedListIds.value = newState
        if (this::preferences.isInitialized) {
            scope.launch { preferences.setArchivedIds(newState) }
        }
    }
}
