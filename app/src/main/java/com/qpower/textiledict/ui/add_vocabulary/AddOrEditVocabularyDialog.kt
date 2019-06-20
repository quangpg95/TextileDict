package com.qpower.textiledict.ui.add_vocabulary

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.qpower.textiledict.R
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.database.getDatabase
import com.qpower.textiledict.databinding.DialogAddOrEditVocabularyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddOrEditVocabularyDialog : DialogFragment() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    companion object {
        private const val VOCABULARY_KEY = "vocabulary_key"
        fun newInstance(vocabulary: Vocabulary): AddOrEditVocabularyDialog {
            val args = Bundle()
            args.putParcelable(VOCABULARY_KEY, vocabulary)
            val fragment = AddOrEditVocabularyDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val binding = DialogAddOrEditVocabularyBinding.inflate(inflater)
        val vocabulary = arguments!!.getParcelable<Vocabulary>(VOCABULARY_KEY)
        vocabulary?.let {
            binding.vocabulary = vocabulary
        }
        builder.setView(binding.root)
            .setPositiveButton(getString(R.string.positive_text), null)
            .setNegativeButton(getString(R.string.cancel_text), null)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                binding.apply {
                    if (wordsInput.text!!.isEmpty()) {
                        wordsInput.error = getString(R.string.empty_error_message)
                        return@setOnClickListener
                    }
                    if (filledExposedDropdown.text!!.isEmpty()) {
                        filledExposedDropdown.error = getString(R.string.empty_error_message)
                        return@setOnClickListener
                    }
                    if (meansInput.text!!.isEmpty()) {
                        meansInput.error = getString(R.string.empty_error_message)
                        return@setOnClickListener
                    }
                    scope.launch {
                        vocabulary?.let {
                            vocabulary.title = wordsInput.text.toString().trim()
                            vocabulary.means = meansInput.text.toString().trim()
                            vocabulary.spelling = spellingInput.text.toString().trim()
                            vocabulary.type = when (filledExposedDropdown.text.toString().trim()) {
                                getString(R.string.noun_text) -> 1
                                getString(R.string.verb_text) -> 2
                                else -> 3
                            }
                            if (vocabulary.id == 0) {
                                withContext(Dispatchers.IO) {
                                    getDatabase(activity!!).vocabularyDao.insert(vocabulary)
                                }
                            } else {
                                withContext(Dispatchers.IO) {
                                    getDatabase(activity!!).vocabularyDao.updateVocabulary(vocabulary)
                                }
                            }
                        }
                    }

                    dialog.dismiss()
                }
            }
        }

        return dialog
    }
}