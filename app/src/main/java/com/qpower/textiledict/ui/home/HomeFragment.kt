package com.qpower.textiledict.ui.home

import android.app.Activity.RESULT_OK
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.qpower.textiledict.R
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.databinding.FragmentHomeBinding
import com.qpower.textiledict.ui.BaseFragment
import com.qpower.textiledict.utils.clear
import com.qpower.textiledict.view_model.HomeViewModel
import java.util.*


const val REQ_CODE_SPEECH_INPUT = 100

class HomeFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var searchView: SearchView
    private lateinit var keyboardVoice: View

    private val viewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, HomeViewModel.Factory(activity.application))
            .get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.vocabularyList.apply {
            setHasFixedSize(true)
            adapter = vocabularyAdapter
        }
        viewModel.vocabularies.observe(this, Observer {
            if (it.isEmpty()) {
                binding.apply {
                    if (viewModel!!.searchWord.value!!.isEmpty()) {
                        recentEmptyIcon.setImageResource(R.drawable.ic_history)
                        recentEmptyText.text = getString(R.string.recent_empty_text)
                    } else {
                        recentEmptyIcon.setImageResource(R.drawable.ic_search)
                        recentEmptyText.text = getString(R.string.no_results_text)
                    }
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
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val rootView = menu.findItem(R.id.app_bar_search).actionView as View
        searchView = rootView.findViewById(R.id.search_item)
        keyboardVoice = rootView.findViewById(R.id.voice_item)
        searchView.apply {
            setSearchableInfo(
                searchManager
                    .getSearchableInfo(activity!!.componentName)
            )
            val searchHintIcon = findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            searchHintIcon.layoutParams = LinearLayout.LayoutParams(0, 0)
            searchHintIcon.visibility = View.GONE
            val underline = findViewById<View>(androidx.appcompat.R.id.search_plate)
            underline.setBackgroundColor(Color.TRANSPARENT)
            setOnQueryTextListener(this@HomeFragment)
            setQuery(viewModel.searchWord.value, false)
        }

        keyboardVoice.setOnClickListener {
            promptSpeechInput()
        }
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val word = newText!!.clear()
        Log.d("QuangNDb:", word)
        if (word.isEmpty()) {
            keyboardVoice.visibility = View.VISIBLE
        } else {
            keyboardVoice.visibility = View.GONE
        }
        viewModel.updateSearch(
            word,
            when (word.isEmpty()) {
                true -> VocabularyStatus.RECENT
                else -> VocabularyStatus.SEARCH
            }
        )
        return true
    }

    override fun scrollToTop() {
        binding.vocabularyList.layoutManager!!.scrollToPosition(0)
    }

    override fun handleItemClick(vocabulary: Vocabulary) {
        searchView.clearFocus()
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment(vocabulary))
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.speech_prompt)
        )
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                activity,
                getString(R.string.speech_not_supported),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    searchView.setQuery(result[0], true)
                }
            }
        }
    }

}