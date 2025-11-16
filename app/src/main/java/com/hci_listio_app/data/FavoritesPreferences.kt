package com.hci_listio_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore: DataStore<Preferences> by preferencesDataStore(name = "list_favorites")

class FavoritesPreferences(private val context: Context) {
    private val favoritesKey = stringPreferencesKey("favorite_ids")

    val favorites: Flow<Set<Long>> = context.favoritesDataStore.data.map { prefs ->
        prefs[favoritesKey]
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.mapNotNull { it.toLongOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    suspend fun setFavorites(ids: Set<Long>) {
        context.favoritesDataStore.edit { prefs ->
            prefs[favoritesKey] = ids.joinToString(",")
        }
    }
}
