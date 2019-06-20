package com.qpower.textiledict.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.databinding.FragmentHistoryBinding
import com.qpower.textiledict.databinding.FragmentHomeBinding
import com.qpower.textiledict.ui.BaseFragment
import com.qpower.textiledict.ui.home.VocabularyAdapter
import com.qpower.textiledict.view_model.HistoryViewModel
import com.qpower.textiledict.view_model.HomeViewModel

class HistoryFragment : BaseFragment() {
    private lateinit var binding: FragmentHistoryBinding
    override fun scrollToTop() {
        binding.vocabularyList.layoutManager!!.scrollToPosition(0)
    }

    override fun handleItemClick(vocabulary: Vocabulary) {
        findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToDetailsFragment(vocabulary))
    }

    private val viewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, HistoryViewModel.Factory(activity.application))
            .get(HistoryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.vocabularyList.apply {
            setHasFixedSize(true)
            adapter = vocabularyAdapter
        }
        viewModel.vocabularies.observe(this, Observer {
            if (it.isEmpty()) {
                binding.apply {
                    recentEmptyIcon.visibility = View.VISIBLE
                    recentEmptyText.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    recentEmptyIcon.visibility = View.GONE
                    recentEmptyText.visibility = View.GONE
                }
            }
        })
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        return binding.root
    }
}