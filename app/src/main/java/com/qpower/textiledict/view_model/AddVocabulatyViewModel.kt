package com.qpower.textiledict.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qpower.textiledict.ui.home.VocabularyStatus

class AddVocabulatyViewModel(application: Application) : HomeViewModel(application) {
    init {
        updateStatus(VocabularyStatus.ADD_VOCABULARY)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddVocabulatyViewModel::class.java)) {
                return AddVocabulatyViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }
}