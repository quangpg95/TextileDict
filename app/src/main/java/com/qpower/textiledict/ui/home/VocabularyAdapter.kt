package com.qpower.textiledict.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qpower.textiledict.R
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.databinding.VocabularyItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class VocabularyStatus {
    SEARCH, RECENT, FAVORITE, ADD_VOCABULARY
}

class VocabularyAdapter(private val clickListener: OnClickListener) :
    PagedListAdapter<Vocabulary, VocabularyAdapter.ViewHolder>(DiffCallback) {


    private var vocabularyStatus: VocabularyStatus = VocabularyStatus.RECENT
    private var word: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(VocabularyItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vocabulary = getItem(position)
        if (vocabulary != null) {
            holder.itemView.setOnClickListener {
                clickListener.onClick(vocabulary)
            }
            holder.bind(vocabulary, vocabularyStatus, word)
        }

    }

    fun updateStatus(vocabularyStatus: VocabularyStatus) {
        this.vocabularyStatus = vocabularyStatus
        notifyDataSetChanged()
    }

    fun updateSearchWord(word: String) {
        this.word = word
    }

    class ViewHolder(private val binding: VocabularyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val job = Job()
        private val scope = CoroutineScope(Dispatchers.Main + job)
        fun bind(vocabulary: Vocabulary, vocabularyStatus: VocabularyStatus, word: String) {
            binding.vocabulary = vocabulary
            scope.launch {
                binding.imageView.setImageResource(withContext(Dispatchers.IO) {
                    when (vocabularyStatus) {
                        VocabularyStatus.SEARCH -> R.drawable.ic_search
                        VocabularyStatus.RECENT -> R.drawable.ic_history
                        VocabularyStatus.ADD_VOCABULARY -> R.drawable.ic_add_vocabulary
                        else -> R.drawable.ic_favorite
                    }
                })
            }
            binding.executePendingBindings()
//            if (word.isNotEmpty()) {
//                val startPos = vocabulary.title.toLowerCase().indexOf(word.toLowerCase())
//                val endPos = startPos + word.length
//                if (startPos != -1) {
//                    val spannable = SpannableString(vocabulary.title)
//                    val span =
//                        ForegroundColorSpan(ContextCompat.getColor(binding.textView.context, R.color.colorAccent))
//                    spannable.setSpan(span, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                    binding.textView.text = spannable
//                }
//            } else {
//                binding.textView.text = vocabulary.title
//            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Vocabulary>() {
        override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
            return oldItem == newItem
        }
    }

    //Bkav QuangNDb click listener
    class OnClickListener(val clickListener: (vocabulary: Vocabulary) -> Unit) {
        fun onClick(vocabulary: Vocabulary) = clickListener(vocabulary)
    }
}