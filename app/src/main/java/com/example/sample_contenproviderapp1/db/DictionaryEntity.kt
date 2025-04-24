package com.example.sample_contenproviderapp1.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database table to store and fetch Dictionary words
 */

@Entity
data class DictionaryEntity(
    val word: String,
    val definition: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)