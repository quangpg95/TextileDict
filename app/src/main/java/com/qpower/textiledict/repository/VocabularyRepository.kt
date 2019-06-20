package com.qpower.textiledict.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.qpower.textiledict.database.History
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.database.VocabularyDao
import com.qpower.textiledict.database.VocabularySearchResult
import com.qpower.textiledict.ui.home.VocabularyStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VocabularyRepository(private val database: VocabularyDao) {

    fun search(word: String, vocabularyStatus: VocabularyStatus): VocabularySearchResult {
        return when (vocabularyStatus) {
            VocabularyStatus.SEARCH -> {
                val wordMath = String.format("%s*", word)
                val wordLike = String.format("%s%%", word)
                val dataSourceFactory = database.searchVocabularies(word, wordMath, wordLike)
                val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE).build()
                VocabularySearchResult(data)
            }
            VocabularyStatus.RECENT -> {
                val dataSourceFactory = database.getVocabulariesRecent()
                val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE).build()
                VocabularySearchResult(data)
            }
            VocabularyStatus.FAVORITE -> {
                val dataSourceFactory = database.getFavorites()
                val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE).build()
                VocabularySearchResult(data)
            }
            else -> {
                val dataSourceFactory = database.getUserAdded()
                val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE).build()
                VocabularySearchResult(data)
            }
        }
    }

    suspend fun addHistory(voId: Int) {
        withContext(Dispatchers.IO) {
            val existHistory = database.getHistory(voId)
            if (existHistory == null) {
                database.addHistory(History(voId = voId, timeStamp = System.currentTimeMillis()))
            } else {
                existHistory.timeStamp = System.currentTimeMillis()
                database.updateHistory(existHistory)
            }
        }
    }

    suspend fun updateVocabulary(vocabulary: Vocabulary) {
        withContext(Dispatchers.IO) {
            database.updateVocabulary(vocabulary)
        }
    }

    fun getTests():LiveData<PagedList<Vocabulary>>{
        val dataSourceFactory = database.getTests()
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE).build()
        return data
    }


    companion object {
        //Bkav QuangNDb load 30 item 1 lan, load more se tiep tuc load them 30 item
        private const val DATABASE_PAGE_SIZE = 30
    }
}