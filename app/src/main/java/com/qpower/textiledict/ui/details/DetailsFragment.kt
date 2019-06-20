package com.qpower.textiledict.ui.details

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS
import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.qpower.textiledict.R
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.databinding.FragmentDetailsBinding
import com.qpower.textiledict.ui.add_vocabulary.AddOrEditVocabularyDialog
import com.qpower.textiledict.view_model.DetailsViewModel
import java.util.*
import kotlin.collections.HashMap


class DetailsFragment : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    override fun onInit(status: Int) {
        when (status) {
            TextToSpeech.SUCCESS -> {
                tts.language = Locale.US
                isTTSReady = true
            }
            else -> isTTSReady = false
        }
    }

    private lateinit var vocabulary: Vocabulary
    private lateinit var viewModel: DetailsViewModel
    private var isTTSReady = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDetailsBinding.inflate(inflater)
        vocabulary = DetailsFragmentArgs.fromBundle(arguments!!).vocabulary
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        viewModel = ViewModelProviders.of(this, DetailsViewModel.Factory(activity.application, vocabulary))
            .get(DetailsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        tts = TextToSpeech(activity, this)
        binding.speakerImage.setOnClickListener {
            if (isTTSReady) {
                val bundle = Bundle()
                bundle.putInt(KEY_PARAM_VOLUME, 1)
                tts.speak(vocabulary.title, TextToSpeech.QUEUE_FLUSH, bundle, vocabulary.id.toString())
            }
        }
        binding.fab.setOnClickListener {
            val dialog = AddOrEditVocabularyDialog.newInstance(vocabulary)
            dialog.show(fragmentManager!!, "AddDialog")
        }
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        setHasOptionsMenu(true)
        return binding.root
    }

    @Suppress("DEPRECATION")
    private fun speakU21() {
        val onlineSpeech = HashMap<String, String>()
        onlineSpeech[KEY_FEATURE_NETWORK_SYNTHESIS] = "true"
        tts.speak(vocabulary.title, TextToSpeech.QUEUE_FLUSH, onlineSpeech)
        Log.d("QuangNDb:", "${tts.voice}")
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.details_menu, menu)
        val favoriteIcon = menu.findItem(R.id.favorite_item)
        favoriteIcon?.let {
            favoriteIcon.setIcon(
                when (vocabulary.favorite) {
                    0 -> R.drawable.ic_favorite_border
                    else -> R.drawable.ic_favorite
                }
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite_item -> {
                vocabulary.favorite = when (vocabulary.favorite) {
                    0 -> 1
                    else -> 0
                }
                viewModel.updateFavorite(vocabulary)
                item.setIcon(
                    when (vocabulary.favorite) {
                        0 -> R.drawable.ic_favorite_border
                        else -> R.drawable.ic_favorite
                    }
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}