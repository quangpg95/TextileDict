package com.qpower.textiledict.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

enum class VocabularyType(val value: Int) {
    NOUN(1),
    VERB(2),
    ADJECTIVE(3)
}

@Dao
interface VocabularyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vocabularies: List<Vocabulary>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addHistory(history: History)

    @Update
    fun updateHistory(history: History)

    @Query("select * from history where vo_id = :voId")
    fun getHistory(voId: Int): History

    @Query("select vocabulary.* from vocabulary join history on vocabulary._id = history.vo_id order by timeStamp DESC")
    fun getVocabulariesRecent(): DataSource.Factory<Int, Vocabulary>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vocabulary: Vocabulary): Long

    @Query("select vocabulary.* from vocabulary JOIN vocabulary_fts on (vocabulary._id = vocabulary_fts.docid) where vocabulary_fts MATCH :wordMath order by case when upper(vocabulary.title) like upper(:word) then 0 when upper(vocabulary.title) like upper(:wordLike) then 1 else 2 end")
    fun searchVocabularies(word: String, wordMath: String, wordLike: String): DataSource.Factory<Int, Vocabulary>

    @Update
    fun updateVocabulary(vocabulary: Vocabulary)

    @Query("select vocabulary.* from vocabulary where favorite = 1 order by title asc")
    fun getFavorites(): DataSource.Factory<Int, Vocabulary>

    @Query("select vocabulary.* from vocabulary join history on vocabulary._id = history.vo_id order by timeStamp DESC")
    fun getHistories(): LiveData<List<Vocabulary>>

    @Query("select vocabulary.* from vocabulary where is_user_added = 1 order by _id desc")
    fun getUserAdded(): DataSource.Factory<Int, Vocabulary>

    @Query("select vocabulary.* from vocabulary JOIN vocabulary_fts on (vocabulary._id = vocabulary_fts.docid) where vocabulary_fts MATCH 'a*'")
    fun getTests():DataSource.Factory<Int, Vocabulary>

}

@Database(entities = [Vocabulary::class, History::class, VocabularyFts::class], version = 1, exportSchema = false)
abstract class TextileDatabase : RoomDatabase() {
    abstract val vocabularyDao: VocabularyDao
}

private lateinit var INSTANCE: TextileDatabase

fun getDatabase(context: Context): TextileDatabase {
    synchronized(TextileDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context, TextileDatabase::class.java, "textile.db")
                .addMigrations()
                .build()
        }
    }
    return INSTANCE
}
