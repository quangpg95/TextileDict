package com.qpower.textiledict.database_helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val DATABASE_NAME = "textile.db"

class AssetDatabaseOpenHelper(private val context: Context) {
    fun storeDatabase(): SQLiteDatabase {
        val path = context.filesDir.absolutePath
        val pathDb = File(path)
        try {
            if (!pathDb.exists()) {
                pathDb.mkdir()
            }
            if (!File("$path/$DATABASE_NAME").exists()) {
                copyDatabase(path)
            }
        } catch (e: IOException) {
            println(e.stackTrace)
        }
        return SQLiteDatabase.openDatabase(
            "$path/$DATABASE_NAME", null, SQLiteDatabase.OPEN_READONLY
        )
    }


    private fun copyDatabase(path: String) {
        val inputStream = context.assets.open(DATABASE_NAME)
        val fileOutputStream = FileOutputStream("$path/$DATABASE_NAME")
        val buffer = ByteArray(1024)
        var length: Int = inputStream.read(buffer)
        // đọc và ghi vào thư mục databases
        while (length > 0) {
            fileOutputStream.write(buffer, 0, length)
            length = inputStream.read(buffer)
        }
        inputStream.close()
        fileOutputStream.flush()
        fileOutputStream.close()
    }
}