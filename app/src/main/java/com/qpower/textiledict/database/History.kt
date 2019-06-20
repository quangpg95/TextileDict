package com.qpower.textiledict.database

import androidx.room.*

@Entity(
    tableName = "history",
    foreignKeys = [ForeignKey(
        entity = Vocabulary::class,
        parentColumns = ["_id"],
        childColumns = ["vo_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["vo_id", "_id"], unique = true)]
)
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    @ColumnInfo(name = "vo_id")
    val voId: Int,
    var timeStamp: Long = System.currentTimeMillis()
)