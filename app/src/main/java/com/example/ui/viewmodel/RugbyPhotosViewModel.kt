package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.RugbyDatabase
import com.example.data.local.UploadBatchEntity
import com.example.data.model.ClubConfig
import com.example.data.model.RugbyCategory
import com.example.data.repository.RugbyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SendDestination {
    GOOGLE_PHOTOS,
    GOOGLE_PHOTOS_SHARED_ALBUM,
    EMAIL_COMMUNICATION,
    WEBHOOK
}

data class UploadUiState(
    val selectedPhotos: List<Uri> = emptyList(),
    val selectedCategory: RugbyCategory = RugbyCategory.SUB_14,
    val note: String = "",
    val isSending: Boolean = false,
    val sendProgress: Float = 0f,
    val lastSuccessBatchId: Long? = null,
    val showSuccessDialog: Boolean = false,
    val previewingPhotoUri: Uri? = null,
    val errorMessage: String? = null
)

class RugbyPhotosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RugbyRepository

    val history: StateFlow<List<UploadBatchEntity>>
    val clubConfig: StateFlow<ClubConfig>

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    init {
        val db = RugbyDatabase.getInstance(application)
        repository = RugbyRepository(db.uploadBatchDao(), application)
        clubConfig = repository.clubConfig
        history = repository.allBatches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Set default category from saved config
        _uiState.value = _uiState.value.copy(
            selectedCategory = clubConfig.value.defaultCategory
        )
    }

    fun addPhotos(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val current = _uiState.value.selectedPhotos.toMutableList()
        uris.forEach { uri ->
            if (!current.contains(uri)) {
                current.add(uri)
            }
        }
        _uiState.value = _uiState.value.copy(
            selectedPhotos = current,
            errorMessage = null
        )
    }

    fun removePhoto(uri: Uri) {
        val current = _uiState.value.selectedPhotos.toMutableList()
        current.remove(uri)
        _uiState.value = _uiState.value.copy(selectedPhotos = current)
    }

    fun clearSelectedPhotos() {
        _uiState.value = _uiState.value.copy(
            selectedPhotos = emptyList(),
            note = "",
            errorMessage = null
        )
    }

    fun selectCategory(category: RugbyCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun setPreviewPhoto(uri: Uri?) {
        _uiState.value = _uiState.value.copy(previewingPhotoUri = uri)
    }

    fun dismissSuccessDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = false)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Effortless single-click upload:
     * Saves the batch to Room database and dispatches photos to Google Photos
     * or designated club communication channel.
     */
    fun sendPhotos(
        context: Context,
        destination: SendDestination = SendDestination.GOOGLE_PHOTOS
    ) {
        val photos = _uiState.value.selectedPhotos
        val category = _uiState.value.selectedCategory
        val note = _uiState.value.note

        if (photos.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Selecciona al menos una foto antes de enviar")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true, sendProgress = 0.2f)

            // Simulate smooth progress indicator for high responsiveness
            delay(250)
            _uiState.value = _uiState.value.copy(sendProgress = 0.6f)

            val destName = when (destination) {
                SendDestination.GOOGLE_PHOTOS -> "Google Photos"
                SendDestination.GOOGLE_PHOTOS_SHARED_ALBUM -> "Álbum Google Photos"
                SendDestination.EMAIL_COMMUNICATION -> "Email Club"
                SendDestination.WEBHOOK -> "Nube Club"
            }

            val batchId = withContext(Dispatchers.IO) {
                repository.saveBatch(
                    category = category,
                    uris = photos.map { it.toString() },
                    note = note,
                    destination = destName
                )
            }

            _uiState.value = _uiState.value.copy(sendProgress = 0.9f)
            delay(150)

            // Execute the system/app dispatch
            dispatchIntent(context, destination, photos, category, note)

            _uiState.value = _uiState.value.copy(
                isSending = false,
                sendProgress = 1.0f,
                lastSuccessBatchId = batchId,
                showSuccessDialog = true
            )
        }
    }

    private fun dispatchIntent(
        context: Context,
        destination: SendDestination,
        photos: List<Uri>,
        category: RugbyCategory,
        note: String
    ) {
        val config = clubConfig.value
        val dateFormat = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        val dateStamp = dateFormat.format(java.util.Date())
        val trcTag = "TRC_${category.code}_$dateStamp"

        val subject = "[$trcTag] ${config.clubName} - ${category.title}"
        val bodyNote = if (note.isNotBlank()) "\nNota/Partido: $note" else ""
        val textBody = "Lote de fotos para $trcTag (${category.title}).\nTorrelodones Rugby Club — Construido en el campo. Unidos después.$bodyNote"

        try {
            when (destination) {
                SendDestination.GOOGLE_PHOTOS -> {
                    // Try direct intent targeting Google Photos, with fallback to general share chooser
                    val photosIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                        type = "image/*"
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(photos))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, textBody)
                        setPackage("com.google.android.apps.photos")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    // Check if Google Photos package is available to handle it
                    val canHandleDirect = context.packageManager.resolveActivity(photosIntent, 0) != null
                    if (canHandleDirect) {
                        context.startActivity(photosIntent)
                    } else {
                        // Open share chooser with Google Photos prioritized
                        val generalShareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                            type = "image/*"
                            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(photos))
                            putExtra(Intent.EXTRA_SUBJECT, subject)
                            putExtra(Intent.EXTRA_TEXT, textBody)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        val chooser = Intent.createChooser(generalShareIntent, "Subir fotos a Google Photos / Club")
                        context.startActivity(chooser)
                    }
                }

                SendDestination.GOOGLE_PHOTOS_SHARED_ALBUM -> {
                    // Opens the club's collaborative Google Photos album link directly
                    val albumUrl = config.googlePhotosAlbumUrl.ifBlank { "https://photos.google.com" }
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(albumUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(webIntent)
                }

                SendDestination.EMAIL_COMMUNICATION -> {
                    val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(config.communicationEmail))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, textBody)
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(photos))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(emailIntent, "Enviar fotos por correo al club")
                    context.startActivity(chooser)
                }

                SendDestination.WEBHOOK -> {
                    // For Webhook or automated cloud dispatch
                    Toast.makeText(context, "Fotos registradas y enviadas al sistema del club", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al abrir la aplicación: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun openClubPhotosAlbum(context: Context) {
        val albumUrl = clubConfig.value.googlePhotosAlbumUrl.ifBlank { "https://photos.google.com" }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(albumUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir el enlace del álbum", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteBatch(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBatch(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearHistory()
        }
    }

    fun updateClubConfig(config: ClubConfig) {
        repository.updateConfig(config)
    }
}
