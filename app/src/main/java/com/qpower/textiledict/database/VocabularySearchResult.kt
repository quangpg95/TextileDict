package com.qpower.textiledict.database

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

data class VocabularySearchResult(val data: LiveData<PagedList<Vocabulary>>)