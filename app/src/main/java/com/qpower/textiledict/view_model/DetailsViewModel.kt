package com.qpower.textiledict.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.database.getDatabase
import com.qpower.textiledict.repository.VocabularyRepository
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application, vocabulary: Vocabulary) : AndroidViewModel(application) {
    private val repository = VocabularyRepository(getDatabase(application).vocabularyDao)

    private val _vocabulary = MutableLiveData<Vocabulary>()
    val vocabularyLive: LiveData<Vocabulary> get() = _vocabulary

    init {
        updateVocabulary(vocabulary)
    }

    private fun updateVocabulary(vocabulary: Vocabulary) {
        _vocabulary.value = vocabulary
        viewModelScope.launch {
            repository.addHistory(vocabulary.id)
        }
    }

    fun updateFavorite(vocabulary: Vocabulary) {
        viewModelScope.launch {
            repository.updateVocabulary(vocabulary)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application, private val vocabulary: Vocabulary) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                return DetailsViewModel(application, vocabulary) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}