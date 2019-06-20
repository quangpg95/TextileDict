package com.qpower.textiledict.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qpower.textiledict.ui.home.VocabularyStatus

class FavoriteViewModel(application: Application) : HomeViewModel(application) {

    init {
        updateStatus(VocabularyStatus.FAVORITE)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
                return FavoriteViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }
}