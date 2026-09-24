package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upload_batches")
data class UploadBatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String,
    val photoCount: Int,
    val photoUris: String, // Comma-separated or JSON list of photo URI strings
    val note: String = "",
    val destination: String = "Google Photos",
    val status: String = "Enviado"
)
