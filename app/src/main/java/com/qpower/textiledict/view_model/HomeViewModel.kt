package com.qpower.textiledict.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.database.VocabularySearchResult
import com.qpower.textiledict.database.getDatabase
import com.qpower.textiledict.repository.VocabularyRepository
import com.qpower.textiledict.ui.home.VocabularyStatus

open class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VocabularyRepository(getDatabase(application).vocabularyDao)


    private val _vocabularyStatus = MutableLiveData<VocabularyStatus>()
    val vocabularyStatus: LiveData<VocabularyStatus> get() = _vocabularyStatus

    private val _searchWord = MutableLiveData<String>()

    val searchWord: LiveData<String> get() = _searchWord

    val test : LiveData<PagedList<Vocabulary>> = repository.getTests()

    val testX:List<Vocabulary> = listOf()


    private val vocabulariesResult: LiveData<VocabularySearchResult> = Transformations.map(searchWord) { word ->
        Log.d("QuangNDb:", "search $word ")
        repository.search(word, vocabularyStatus.value ?: VocabularyStatus.RECENT)
    }

    val vocabularies: LiveData<PagedList<Vocabulary>> = Transformations.switchMap(vocabulariesResult) {
        Log.d("QuangNDb:", "hello: ")
        it.data
    }

    val vocatest: LiveData<List<Vocabulary>> = Transformations.map(vocabularies){
        Log.d("QuangNDb:", "vocatest ${it.size} ")
        testX
    }

    init {
        _searchWord.value = ""
        updateStatus(VocabularyStatus.RECENT)
    }

    fun updateSearch(word: String, vocabularyStatus: VocabularyStatus) {
        updateStatus(vocabularyStatus)
        _searchWord.value = word
    }

    protected fun updateStatus(vocabularyStatus: VocabularyStatus) {
        if (vocabularyStatus != _vocabularyStatus.value) {
//            Log.d("QuangNDb:", "updateSearch: ${vocabularyStatus.name}")
            _vocabularyStatus.value = vocabularyStatus
        }
    }


    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }
}