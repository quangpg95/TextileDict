package com.qpower.textiledict.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "vocabulary")
@Parcelize
data class Vocabulary(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    var title: String,
    var spelling: String,
    var means: String,
    var type: Int,
    var favorite: Int = 0,
    @ColumnInfo(name = "is_user_added")
    val isUserAdded: Int = 0
) : Parcelable

@Entity(tableName = "vocabulary_fts")
@Fts4(contentEntity = Vocabulary::class)
data class VocabularyFts(
    val title: String
)