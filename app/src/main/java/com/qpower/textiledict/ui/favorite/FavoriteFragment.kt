package com.qpower.textiledict.ui.favorite

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
import com.qpower.textiledict.databinding.FragmentFavoriteBinding
import com.qpower.textiledict.databinding.FragmentHistoryBinding
import com.qpower.textiledict.ui.BaseFragment
import com.qpower.textiledict.ui.history.HistoryFragmentDirections
import com.qpower.textiledict.ui.home.VocabularyAdapter
import com.qpower.textiledict.view_model.FavoriteViewModel

class FavoriteFragment : BaseFragment() {
    private lateinit var binding: FragmentFavoriteBinding
    override fun scrollToTop() {
        binding.vocabularyList.layoutManager!!.scrollToPosition(0)
    }

    override fun handleItemClick(vocabulary: Vocabulary) {
        findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToDetailsFragment(vocabulary))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavoriteBinding.inflate(inflater)
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        val viewModel = ViewModelProviders.of(this, FavoriteViewModel.Factory(activity.application))
            .get(FavoriteViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.vocabularyList.apply {
            setHasFixedSize(true)
            adapter = vocabularyAdapter
        }
        viewModel.vocabularies.observe(this, Observer {
            if (it.isEmpty()) {
                binding.apply {
                    favoriteEmptyIcon.visibility = View.VISIBLE
                    favoriteEmptyText.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    favoriteEmptyIcon.visibility = View.GONE
                    favoriteEmptyText.visibility = View.GONE
                }
            }
        })
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)

        return binding.root
    }

}