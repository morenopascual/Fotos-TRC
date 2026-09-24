package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CategoryChipSelector
import com.example.ui.components.PhotoPreviewGrid
import com.example.ui.components.PhotoViewerDialog
import com.example.ui.theme.TRCCharcoal
import com.example.ui.theme.TRCCharcoalBorder
import com.example.ui.theme.TRCCharcoalElevated
import com.example.ui.theme.TRCCharcoalSoft
import com.example.ui.theme.TRCGold
import com.example.ui.theme.TRCGold10
import com.example.ui.theme.TRCGray
import com.example.ui.theme.TRCPitchGreen
import com.example.ui.theme.TRCRust
import com.example.ui.theme.TRCWhite
import com.example.ui.viewmodel.RugbyPhotosViewModel
import com.example.ui.viewmodel.SendDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: RugbyPhotosViewModel,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val clubConfig by viewModel.clubConfig.collectAsState()
    val scrollState = rememberScrollState()

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addPhotos(uris)
        }
    }

    var showOtherOptions by remember { mutableStateOf(false) }

    val todayDateStamp = remember {
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TRC Hero Header Card with Ant Mascot
        TRCHeaderCard(
            clubName = clubConfig.clubName,
            stadiumName = clubConfig.stadiumName
        )

        // TRC Scoreboard status component (as seen in the identity manual)
        TRCScoreboard(
            photoCount = uiState.selectedPhotos.size,
            categoryCode = uiState.selectedCategory.code,
            destinationLabel = "GOOGLE PHOTOS"
        )

        // 1. Rugby Category Selector (Sub 10, Sub 12, Sub 14, Sub 16, Sub 18, Veteranos)
        CategoryChipSelector(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = { viewModel.selectCategory(it) }
        )

        // Error Banner
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            uiState.errorMessage?.let { errorMsg ->
                Surface(
                    color = TRCRust.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, TRCRust),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Aviso",
                            tint = TRCRust
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMsg,
                            color = TRCWhite,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // 2. Photo Selection Area (Empty State or Preview Grid)
        if (uiState.selectedPhotos.isEmpty()) {
            TRCEmptyPhotoPickerCard(
                categoryTitle = uiState.selectedCategory.title,
                onPickPhotos = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        } else {
            PhotoPreviewGrid(
                photos = uiState.selectedPhotos,
                onAddMore = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemovePhoto = { viewModel.removePhoto(it) },
                onPhotoClick = { viewModel.setPreviewPhoto(it) },
                onClearAll = { viewModel.clearSelectedPhotos() }
            )

            // Naming convention tag preview
            Surface(
                color = TRCCharcoalSoft,
                border = BorderStroke(1.dp, TRCCharcoalBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ETIQUETA:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TRCGold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TRC_${uiState.selectedCategory.code}_$todayDateStamp.jpg",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = TRCWhite
                        )
                    )
                }
            }

            // Optional match note / rival / details
            OutlinedTextField(
                value = uiState.note,
                onValueChange = { viewModel.setNote(it) },
                label = { Text("Nota de partido (ej: vs Cisneros, entrenamiento...)") },
                placeholder = { Text("Jornada de liga, Torneo...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("optional_note_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TRCCharcoalSoft,
                    unfocusedContainerColor = TRCCharcoalSoft,
                    focusedBorderColor = TRCGold,
                    unfocusedBorderColor = TRCCharcoalBorder,
                    focusedLabelColor = TRCGold,
                    unfocusedLabelColor = TRCGray,
                    focusedTextColor = TRCWhite,
                    unfocusedTextColor = TRCWhite
                ),
                singleLine = true
            )
        }

        // 3. Primary Big Action Button: TRC Gold 10% focal point
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (uiState.selectedPhotos.isEmpty()) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    } else {
                        viewModel.sendPhotos(context, SendDestination.GOOGLE_PHOTOS)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                    .testTag("main_send_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TRCGold,
                    contentColor = TRCCharcoal
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                enabled = !uiState.isSending
            ) {
                if (uiState.isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TRCCharcoal,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ENVIANDO AL TRC...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TRCCharcoal
                    )
                } else {
                    Icon(
                        imageVector = if (uiState.selectedPhotos.isEmpty()) Icons.Default.AddPhotoAlternate else Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = TRCCharcoal
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (uiState.selectedPhotos.isEmpty()) {
                            "1. ELEGIR FOTOS DE LA GALERÍA"
                        } else {
                            "SUBIR AL GOOGLE PHOTOS DEL TRC (${uiState.selectedPhotos.size})"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = TRCCharcoal
                    )
                }
            }

            // Progress bar
            if (uiState.isSending) {
                LinearProgressIndicator(
                    progress = { uiState.sendProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = TRCGold,
                    trackColor = TRCCharcoalBorder
                )
            }

            // Secondary links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.openClubPhotosAlbum(context) },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Abrir Google Photos",
                        tint = TRCGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Abrir Álbum TRC",
                        style = MaterialTheme.typography.labelMedium,
                        color = TRCWhite
                    )
                }

                TextButton(
                    onClick = { showOtherOptions = !showOtherOptions },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(
                        text = if (showOtherOptions) "Menos opciones" else "Opciones alternativas",
                        style = MaterialTheme.typography.labelMedium,
                        color = TRCGray
                    )
                }
            }

            // Alternative send options (Email, Shared album direct)
            AnimatedVisibility(
                visible = showOtherOptions,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = TRCCharcoalSoft),
                    border = BorderStroke(1.dp, TRCCharcoalBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Canales de comunicación:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TRCGold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { viewModel.sendPhotos(context, SendDestination.EMAIL_COMMUNICATION) },
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 48.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = TRCCharcoalElevated,
                                    contentColor = TRCWhite
                                ),
                                enabled = uiState.selectedPhotos.isNotEmpty() && !uiState.isSending
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp), tint = TRCGold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email Club", style = MaterialTheme.typography.labelSmall)
                            }

                            FilledTonalButton(
                                onClick = { viewModel.sendPhotos(context, SendDestination.GOOGLE_PHOTOS_SHARED_ALBUM) },
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 48.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = TRCCharcoalElevated,
                                    contentColor = TRCWhite
                                ),
                                enabled = !uiState.isSending
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = TRCGold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Álbum Web", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to zoom/preview a single photo
    PhotoViewerDialog(
        photoUri = uiState.previewingPhotoUri,
        onDismiss = { viewModel.setPreviewPhoto(null) }
    )

    // Success Dialog after sending
    if (uiState.showSuccessDialog) {
        TRCSuccessDialog(
            category = uiState.selectedCategory.title,
            count = uiState.selectedPhotos.size,
            onDismiss = {
                viewModel.dismissSuccessDialog()
                viewModel.clearSelectedPhotos()
            },
            onOpenAlbum = {
                viewModel.dismissSuccessDialog()
                viewModel.clearSelectedPhotos()
                viewModel.openClubPhotosAlbum(context)
            },
            onViewHistory = {
                viewModel.dismissSuccessDialog()
                viewModel.clearSelectedPhotos()
                onNavigateToHistory()
            }
        )
    }
}

@Composable
private fun TRCHeaderCard(clubName: String, stadiumName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = TRCCharcoalSoft),
        border = BorderStroke(1.dp, TRCCharcoalBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Official TRC Ant Mascot Badge
                Image(
                    painter = painterResource(id = R.drawable.ic_trc_badge),
                    contentDescription = "Hormiga TRC Oficial",
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "TORRELODONES RUGBY CLUB",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TRCGold
                    )
                    Text(
                        text = "CONSTRUIDO EN EL CAMPO.\nUNIDOS DESPUÉS.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            lineHeight = 18.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = TRCWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$stadiumName · 2026/27",
                        style = MaterialTheme.typography.bodySmall,
                        color = TRCGray
                    )
                }
            }
        }
    }
}

@Composable
private fun TRCScoreboard(
    photoCount: Int,
    categoryCode: String,
    destinationLabel: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TRCCharcoal,
        border = BorderStroke(1.dp, TRCCharcoalBorder),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreboardCell(
                number = String.format(Locale.getDefault(), "%02d", photoCount),
                label = "FOTOS",
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp)
                    .background(TRCCharcoalBorder)
            )

            ScoreboardCell(
                number = categoryCode,
                label = "CATEGORÍA",
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp)
                    .background(TRCCharcoalBorder)
            )

            ScoreboardCell(
                number = "PHOTOS",
                label = "DESTINO",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ScoreboardCell(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = TRCGold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TRCGray
        )
    }
}

@Composable
private fun TRCEmptyPhotoPickerCard(
    categoryTitle: String,
    onPickPhotos: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                BorderStroke(1.5.dp, TRCGold.copy(alpha = 0.5f)),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onPickPhotos)
            .testTag("empty_photo_picker_card"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = TRCCharcoalSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(TRCGold10)
                    .border(BorderStroke(1.dp, TRCGold), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Añadir fotos",
                    tint = TRCGold,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Toca para abrir la galería del móvil",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TRCWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Selecciona fotos de $categoryTitle. Se subirán organizadas a la cuenta del club en 1 solo clic.",
                style = MaterialTheme.typography.bodySmall,
                color = TRCGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPickPhotos,
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("pick_photos_empty_button"),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TRCGold,
                    contentColor = TRCCharcoal
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = TRCCharcoal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Abrir Galería",
                    fontWeight = FontWeight.ExtraBold,
                    color = TRCCharcoal
                )
            }
        }
    }
}

@Composable
private fun TRCSuccessDialog(
    category: String,
    count: Int,
    onDismiss: () -> Unit,
    onOpenAlbum: () -> Unit,
    onViewHistory: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TRCCharcoalSoft,
        titleContentColor = TRCWhite,
        textContentColor = TRCWhite,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Éxito",
                tint = TRCGold,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "¡Fotos Enviadas al TRC!",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Se han preparado $count fotos etiquetadas para $category y enviadas a Google Photos del Torrelodones Rugby Club.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "El registro ha quedado archivado en tu historial local.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = TRCGray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TRCGold,
                    contentColor = TRCCharcoal
                )
            ) {
                Text("Listo / Enviar Más", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onViewHistory,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Ver Historial", color = TRCWhite)
                }
                TextButton(
                    onClick = onOpenAlbum,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Abrir Álbum", color = TRCGold)
                }
            }
        }
    )
}
