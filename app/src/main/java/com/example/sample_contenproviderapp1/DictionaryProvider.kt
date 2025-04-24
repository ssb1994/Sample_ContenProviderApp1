package com.example.sample_contenproviderapp1

import android.content.ContentProvider
import android.content.ContentValues
import android.content.IntentFilter.AuthorityEntry
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.example.sample_contenproviderapp1.DictionaryProvider.Companion.AUTHORITY
import com.example.sample_contenproviderapp1.db.DictionaryDao
import com.example.sample_contenproviderapp1.db.DictionaryDatabase
import com.example.sample_contenproviderapp1.db.DictionaryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat

class DictionaryProvider : ContentProvider() {
    private lateinit var dictionaryDao: DictionaryDao

    companion object {
        val AUTHORITY = "com.example.sample_contenproviderapp1"

        private const val WORDS = 100

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "words", WORDS)
        }
    }

    private val applicationScope by lazy {
        (context?.applicationContext as? App)?.applicationScope!!
    }

    override fun onCreate(): Boolean {
        dictionaryDao = Room.databaseBuilder(
            context?.applicationContext!!,
            DictionaryDatabase::class.java,
            "dictionary.db"
        ).build().dao

        applicationScope.launch {
            prepopulateDb()
        }

        return true
    }

    private suspend fun prepopulateDb() {
        if (dictionaryDao.getCount() == 0L) {
            val words = parseCsv()
            dictionaryDao.insertAll(words)
        }
    }

    private suspend fun parseCsv() = withContext(Dispatchers.IO) {
        try {
            context
                ?.applicationContext!!
                .assets
                .open("english-dict.csv")
                .use { inputStream ->
                    val records = CSVFormat.DEFAULT.parse(inputStream.bufferedReader())

                    records
                        .toList()
                        .drop(1)//Every CSV have a head to drop that
                        .mapNotNull { record ->
                            val word = record.get(0)
                            val definition = record.get(2)

                            DictionaryEntity(
                                word = word ?: return@mapNotNull null,
                                definition = definition ?: return@mapNotNull null
                            )
                        }
                }
        } catch (e: Exception) {
            ensureActive()
            e.printStackTrace()
            //Return empty list if loading fails
            emptyList<DictionaryEntity>()
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        return when (uriMatcher.match(uri)) {
            WORDS ->
                selectionArgs?.getOrNull(0)?.let { query ->
                    dictionaryDao.findByWord(query)
                } ?: dictionaryDao.getAll()

            else -> throw IllegalArgumentException("Invalid Uri.")
        }
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.dir/vnd.$AUTHORITY"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}