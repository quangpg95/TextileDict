package com.qpower.textiledict.utils

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.qpower.textiledict.R
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.ui.home.VocabularyAdapter
import com.qpower.textiledict.ui.home.VocabularyStatus
import java.text.Normalizer


@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: PagedList<Vocabulary>?) {
    data?.let {
        Log.d("QuangNDb:", "${data.size} ")
    }
    val adapter = recyclerView.adapter as VocabularyAdapter
    adapter.submitList(data)
}

@BindingAdapter("searchState")
fun binSearchRecyclerView(recyclerView: RecyclerView, vocabularyStatus: VocabularyStatus) {
    val adapter = recyclerView.adapter as VocabularyAdapter
    adapter.updateStatus(vocabularyStatus)
}


@BindingAdapter("searchWord")
fun binSearchRecyclerView(recyclerView: RecyclerView, word: String) {
    val adapter = recyclerView.adapter as VocabularyAdapter
    adapter.updateSearchWord(word)
}

@BindingAdapter("type")
fun TextView.binType(type: Int) {
    text = when (type) {
        1 -> context.getString(R.string.noun_text)
        2 -> context.getString(R.string.verb_text)
        else -> context.getString(R.string.adj_text)
    }
}

@BindingAdapter("typeAutoText")
fun AppCompatAutoCompleteTextView.binType(type: Int) {
    if (type > 0) {
        setText(
            when (type) {
                1 -> context.getString(R.string.noun_text)
                2 -> context.getString(R.string.verb_text)
                else -> context.getString(R.string.adj_text)
            }
        )
    }
    val types = arrayOf(context.getString(R.string.noun_text), context.getString(R.string.verb_text), context.getString(R.string.adj_text))
    val adapter = ArrayAdapter(
        context,
        android.R.layout.simple_dropdown_item_1line,
        types
    )
    setAdapter(adapter)
}

@BindingAdapter("spelling")
fun TextView.binSpelling(spelling: String) {
    text = when {
        spelling.isEmpty() -> context.getString(R.string.non_spelling_text)
        else -> spelling
    }
}

fun String.clear(): String {
    var result = Normalizer.normalize(this, Normalizer.Form.NFKD).replace(
        "\\p{InCombiningDiacriticalMarks}+".toRegex(),
        ""
    )
    // Ham do java convert latin code ben tren chi bi loi chu đ và Đ nên xử lý bằng tay đoạn này
    if ((result.contains("đ") || result.contains("Đ"))) {
        result = result.replace("đ", "d")
        result = result.replace("Đ", "D")
    }
    return result
}