package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.UploadBatchEntity
import com.example.data.model.RugbyCategory
import com.example.ui.components.PhotoViewerDialog
import com.example.ui.theme.TRCCharcoal
import com.example.ui.theme.TRCCharcoalBorder
import com.example.ui.theme.TRCCharcoalElevated
import com.example.ui.theme.TRCCharcoalSoft
import com.example.ui.theme.TRCGold
import com.example.ui.theme.TRCGray
import com.example.ui.theme.TRCPitchGreen
import com.example.ui.theme.TRCRust
import com.example.ui.theme.TRCWhite
import com.example.ui.viewmodel.RugbyPhotosViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: RugbyPhotosViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val historyItems by viewModel.history.collectAsState()
    val context = LocalContext.current
    var previewPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header with TRC aesthetics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 6.dp, height = 20.dp)
                            .background(TRCGold, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HISTORIAL DE ENVÍOS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TRCWhite
                    )
                }
                Text(
                    text = "${historyItems.size} lotes registrados en este dispositivo",
                    style = MaterialTheme.typography.bodySmall,
                    color = TRCGray
                )
            }

            if (historyItems.isNotEmpty()) {
                IconButton(
                    onClick = { showConfirmClearDialog = true },
                    modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Borrar historial",
                        tint = TRCRust
                    )
                }
            }
        }

        // Empty State or List
        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TRCGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aún no hay envíos registrados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TRCWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cuando envíes fotos de los partidos al club, aparecerán aquí con su fecha, categoría y etiqueta TRC.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TRCGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                        border = BorderStroke(1.dp, TRCGold)
                    ) {
                        Text("Subir Fotos Ahora", color = TRCGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyItems, key = { it.id }) { batch ->
                    TRCHistoryBatchCard(
                        batch = batch,
                        onPhotoClick = { uri -> previewPhotoUri = uri },
                        onDelete = { viewModel.deleteBatch(batch.id) },
                        onOpenAlbum = { viewModel.openClubPhotosAlbum(context) }
                    )
                }
            }
        }
    }

    PhotoViewerDialog(
        photoUri = previewPhotoUri,
        onDismiss = { previewPhotoUri = null }
    )

    if (showConfirmClearDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            containerColor = TRCCharcoalSoft,
            titleContentColor = TRCWhite,
            textContentColor = TRCWhite,
            title = { Text("¿Vaciar historial?") },
            text = { Text("Se borrarán los registros locales de envíos de la aplicación. Tus fotos en la galería y en Google Photos no se verán afectadas.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showConfirmClearDialog = false
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Vaciar", color = TRCRust, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmClearDialog = false },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Cancelar", color = TRCWhite)
                }
            }
        )
    }
}

@Composable
private fun TRCHistoryBatchCard(
    batch: UploadBatchEntity,
    onPhotoClick: (Uri) -> Unit,
    onDelete: () -> Unit,
    onOpenAlbum: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(batch.timestamp) { dateFormat.format(Date(batch.timestamp)) }
    val photoUris = remember(batch.photoUris) {
        if (batch.photoUris.isBlank()) emptyList()
        else batch.photoUris.split(",").map { Uri.parse(it.trim()) }
    }

    val category = remember(batch.category) { RugbyCategory.fromCode(batch.category) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_batch_card_${batch.id}"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = TRCCharcoalSoft),
        border = BorderStroke(1.dp, TRCCharcoalBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (category.isCantera) TRCPitchGreen else TRCCharcoalElevated,
                        border = BorderStroke(1.dp, if (category.isCantera) Color(0xFF2D6A4F) else TRCGold),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = batch.category.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TRCWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "${batch.photoCount} fotos",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TRCGold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .defaultMinSize(minWidth = 36.dp, minHeight = 36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar del historial",
                        tint = TRCGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = TRCGray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = TRCGray
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "• ${batch.destination}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TRCGold
                )
            }

            if (batch.note.isNotBlank()) {
                Text(
                    text = "Nota: ${batch.note}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TRCWhite
                )
            }

            // Thumbnail horizontal preview
            if (photoUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(photoUris) { uri ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(TRCCharcoal)
                                .border(BorderStroke(1.dp, TRCCharcoalBorder), RoundedCornerShape(6.dp))
                                .clickable { onPhotoClick(uri) }
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Miniatura",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onOpenAlbum,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TRCGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Abrir Álbum TRC",
                        style = MaterialTheme.typography.labelMedium,
                        color = TRCWhite
                    )
                }
            }
        }
    }
}
