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

object FavoritesManager {
    private val _favorites = MutableStateFlow<Set<Long>>(emptySet())
    val favorites: StateFlow<Set<Long>> = _favorites.asStateFlow()

    private lateinit var preferences: FavoritesPreferences
    private val scope = CoroutineScope(Dispatchers.IO)

    fun initialize(context: Context) {
        if (this::preferences.isInitialized) return
        preferences = FavoritesPreferences(context)
        preferences.favorites.onEach { ids ->
            _favorites.value = ids
        }.launchIn(scope)
    }

    fun toggleFavorite(listId: Long) {
        updateState { current ->
            if (listId in current) current - listId else current + listId
        }
    }

    fun isFavorite(listId: Long): Boolean = _favorites.value.contains(listId)

    fun addFavorite(listId: Long) {
        updateState { current ->
            if (listId in current) current else current + listId
        }
    }

    fun removeFavorite(listId: Long) {
        updateState { current ->
            if (listId in current) current - listId else current
        }
    }

    private fun updateState(transform: (Set<Long>) -> Set<Long>) {
        val newState = transform(_favorites.value)
        _favorites.value = newState
        if (this::preferences.isInitialized) {
            scope.launch { preferences.setFavorites(newState) }
        }
    }
}
