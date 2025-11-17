package com.hci_listio_app.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.LanguageRepository
import com.hci_listio_app.data.LanguageRepositoryProvider
import com.hci_listio_app.utils.LocaleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LanguageUiState(
    val selectedLanguage: String = "es",
    val isLoading: Boolean = false,
    val isLanguageChanged: Boolean = false
)

class LanguageViewModel(
    private val context: Context,
    private val languageRepository: LanguageRepository = LanguageRepositoryProvider.getInstance(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            languageRepository.initialize()
            val currentLanguage = languageRepository.getLanguageCode() 
                ?: LocaleHelper.getSystemLanguage(context)
            _uiState.update { it.copy(selectedLanguage = currentLanguage) }
        }
    }

    fun onLanguageSelected(languageCode: String) {
        _uiState.update { it.copy(selectedLanguage = languageCode) }
    }

    fun saveLanguage(activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val languageCode = _uiState.value.selectedLanguage
            
            languageRepository.setLanguageCode(languageCode)
            
            kotlinx.coroutines.delay(200)
            
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    isLanguageChanged = true
                )
            }
            
            activity.runOnUiThread {
                activity.recreate()
            }
        }
    }

    fun consumeLanguageChange() {
        _uiState.update { it.copy(isLanguageChanged = false) }
    }
}

