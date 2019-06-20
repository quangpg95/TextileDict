package com.qpower.textiledict.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.ui.home.VocabularyAdapter

abstract class BaseFragment : Fragment() {
    protected val vocabularyAdapter = VocabularyAdapter(VocabularyAdapter.OnClickListener { vocabulary ->
        handleItemClick(vocabulary)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerAdapterDataObserver()
    }

    private fun registerAdapterDataObserver() {
        vocabularyAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                scrollToTop()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                scrollToTop()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                scrollToTop()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                scrollToTop()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                scrollToTop()
            }
        })
    }

    abstract fun scrollToTop()

    abstract fun handleItemClick(vocabulary: Vocabulary)
}