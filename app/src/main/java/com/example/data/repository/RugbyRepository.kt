package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.UploadBatchDao
import com.example.data.local.UploadBatchEntity
import com.example.data.model.ClubConfig
import com.example.data.model.RugbyCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RugbyRepository(
    private val dao: UploadBatchDao,
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rugby_club_prefs", Context.MODE_PRIVATE)

    private val _clubConfig = MutableStateFlow(loadConfig())
    val clubConfig = _clubConfig.asStateFlow()

    val allBatches: Flow<List<UploadBatchEntity>> = dao.getAllBatches()

    private fun loadConfig(): ClubConfig {
        val name = prefs.getString("club_name", "Rugby Club") ?: "Rugby Club"
        val photosUrl = prefs.getString("google_photos_url", "https://photos.app.goo.gl/rugby-club") ?: "https://photos.app.goo.gl/rugby-club"
        val email = prefs.getString("comm_email", "comunicacion@rugbyclub.com") ?: "comunicacion@rugbyclub.com"
        val catCode = prefs.getString("default_category", RugbyCategory.SUB_14.name)
        val defaultCategory = RugbyCategory.fromCode(catCode)
        val webhook = prefs.getString("webhook_url", "") ?: ""
        val autoShare = prefs.getBoolean("auto_share", true)

        return ClubConfig(
            clubName = name,
            googlePhotosAlbumUrl = photosUrl,
            communicationEmail = email,
            defaultCategory = defaultCategory,
            webhookUploadUrl = webhook,
            autoShareToGooglePhotos = autoShare
        )
    }

    fun updateConfig(config: ClubConfig) {
        prefs.edit()
            .putString("club_name", config.clubName)
            .putString("google_photos_url", config.googlePhotosAlbumUrl)
            .putString("comm_email", config.communicationEmail)
            .putString("default_category", config.defaultCategory.name)
            .putString("webhook_url", config.webhookUploadUrl)
            .putBoolean("auto_share", config.autoShareToGooglePhotos)
            .apply()
        _clubConfig.value = config
    }

    suspend fun saveBatch(
        category: RugbyCategory,
        uris: List<String>,
        note: String,
        destination: String = "Google Photos"
    ): Long {
        val entity = UploadBatchEntity(
            category = category.title,
            photoCount = uris.size,
            photoUris = uris.joinToString(","),
            note = note.trim(),
            destination = destination,
            status = "Enviado al Club"
        )
        return dao.insertBatch(entity)
    }

    suspend fun deleteBatch(id: Long) {
        dao.deleteBatchById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllBatches()
    }
}
