package com.qpower.textiledict

import android.app.Application
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.qpower.textiledict.database.Vocabulary
import com.qpower.textiledict.database.getDatabase
import com.qpower.textiledict.database_helper.AssetDatabaseOpenHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextileApplication : Application() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate() {
        super.onCreate()
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pre_populate_db", false)) {
            prePopulateData()
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            sharedPreferences.edit {
                putBoolean("pre_populate_db", true)
            }
        }
    }

    //Bkav QuangNDb seed db for first time
    private fun prePopulateData() {
        val assetDatabaseOpenHelper = AssetDatabaseOpenHelper(this)
        val databaseDeferred = scope.async(Dispatchers.IO) {
            assetDatabaseOpenHelper.storeDatabase()
        }
        scope.launch {
            val databaseTemp = databaseDeferred.await()
            withContext(Dispatchers.IO) {
                val cursor = databaseTemp.query(
                    "vocabulary", null, null
                    , null, null, null, null
                )
                with(cursor) {
                    val voList = mutableListOf<Vocabulary>()
                    while (moveToNext()) {
                        val title = getString(getColumnIndexOrThrow("title"))
                        val spellingTemp = getString(getColumnIndexOrThrow("spelling"))
                        val means = getString(getColumnIndexOrThrow("means"))
                        val type = getInt(getColumnIndexOrThrow("type"))
                        val spelling = when {
                            spellingTemp.startsWith('[') -> spellingTemp.replace(
                                Regex("[\\[\\]]"),
                                ""
                            )
                            spellingTemp.startsWith('-') -> spellingTemp.replace(
                                Regex("[-]"),
                                ""
                            )
                            else -> spellingTemp
                        }
                        voList.add(
                            Vocabulary(
                                title = title,
                                means = means,
                                spelling = spelling,
                                type = type
                            )
                        )

                    }
                    getDatabase(this@TextileApplication).vocabularyDao.insertAll(voList)
                    close()
                }
            }
        }
    }

}