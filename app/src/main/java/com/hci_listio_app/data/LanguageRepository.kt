package com.hci_listio_app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageRepository(private val context: Context) {
    
    private val preferences = LanguagePreferences(context)
    
    private val _currentLanguage = MutableStateFlow<String>("es")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()
    
    val languageCode: Flow<String?> = preferences.languageCode
    
    suspend fun getLanguageCode(): String? {
        return preferences.getLanguageCode()
    }
    
    suspend fun setLanguageCode(languageCode: String) {
        preferences.setLanguageCode(languageCode)
        _currentLanguage.value = languageCode
    }
    
    suspend fun initialize() {
        val savedLanguage = getLanguageCode()
        if (savedLanguage != null) {
            _currentLanguage.value = savedLanguage
        } else {
            // Si no hay preferencia guardada, usar el idioma del sistema
            val systemLanguage = context.resources.configuration.locales[0].language
            val languageCode = if (systemLanguage == "es" || systemLanguage == "en") {
                systemLanguage
            } else {
                "es" // Por defecto español
            }
            _currentLanguage.value = languageCode
        }
    }
}

object LanguageRepositoryProvider {
    private var instance: LanguageRepository? = null
    
    fun getInstance(context: Context): LanguageRepository {
        if (instance == null) {
            instance = LanguageRepository(context.applicationContext)
        }
        return instance!!
    }
}

