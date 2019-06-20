package com.qpower.textiledict.ui.add_vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.databinding.FragmentAddVocabularyBinding
import com.qpower.textiledict.ui.BaseFragment
import com.qpower.textiledict.view_model.AddVocabulatyViewModel


class AddVocabularyFragment : BaseFragment() {
    private lateinit var binding: FragmentAddVocabularyBinding
    override fun scrollToTop() {
        binding.vocabularyList.layoutManager!!.scrollToPosition(0)
    }


    override fun handleItemClick(vocabulary: Vocabulary) {
        findNavController().navigate(
            AddVocabularyFragmentDirections.actionAddVocabularyFragmentToDetailsFragment(
                vocabulary
            )
        )
    }

    private val viewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, AddVocabulatyViewModel.Factory(activity.application))
            .get(AddVocabulatyViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddVocabularyBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.vocabularyList.apply {
            setHasFixedSize(true)
            adapter = vocabularyAdapter
        }
        viewModel.vocabularies.observe(this, Observer {
            if (it.isEmpty()) {
                binding.apply {
                    addEmptyIcon.visibility = View.VISIBLE
                    addEmptyText.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    addEmptyIcon.visibility = View.GONE
                    addEmptyText.visibility = View.GONE
                }
            }
        })
        binding.fab.setOnClickListener {
            val dialog = AddOrEditVocabularyDialog.newInstance(
                Vocabulary(
                    title = "",
                    spelling = "",
                    means = "",
                    isUserAdded = 1,
                    type = 0
                )
            )
            dialog.show(fragmentManager!!, "AddDialog")
        }
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        return binding.root
    }
}