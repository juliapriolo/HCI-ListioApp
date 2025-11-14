package com.hci_listio_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.historyDataStore: DataStore<Preferences> by preferencesDataStore(name = "list_history")

class ListHistoryPreferences(private val context: Context) {

    private val historyKey = stringPreferencesKey("archived_ids")

    val archivedIds: Flow<Set<Long>> = context.historyDataStore.data.map { prefs ->
        prefs[historyKey]
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.mapNotNull { it.toLongOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    suspend fun setArchivedIds(ids: Set<Long>) {
        context.historyDataStore.edit { prefs ->
            prefs[historyKey] = ids.joinToString(",")
        }
    }
}
