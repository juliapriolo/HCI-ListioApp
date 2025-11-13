package com.hci_listio_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

object LanguagePreferencesKeys {
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
}

class LanguagePreferences(private val context: Context) {
    
    val languageCode: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LanguagePreferencesKeys.LANGUAGE_CODE]
    }
    
    suspend fun setLanguageCode(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LanguagePreferencesKeys.LANGUAGE_CODE] = languageCode
        }
    }
    
    suspend fun getLanguageCode(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[LanguagePreferencesKeys.LANGUAGE_CODE]
        }.first()
    }
}

