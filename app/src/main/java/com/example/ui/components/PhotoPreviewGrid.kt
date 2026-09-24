package com.example.ui.components

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.theme.TRCCharcoal
import com.example.ui.theme.TRCCharcoalBorder
import com.example.ui.theme.TRCCharcoalElevated
import com.example.ui.theme.TRCCharcoalSoft
import com.example.ui.theme.TRCGold
import com.example.ui.theme.TRCGray
import com.example.ui.theme.TRCRust
import com.example.ui.theme.TRCWhite

@Composable
fun PhotoPreviewGrid(
    photos: List<Uri>,
    onAddMore: () -> Unit,
    onRemovePhoto: (Uri) -> Unit,
    onPhotoClick: (Uri) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(TRCGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${photos.size}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black
                        ),
                        color = TRCCharcoal
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (photos.size == 1) "1 FOTO SELECCIONADA" else "${photos.size} FOTOS SELECCIONADAS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TRCWhite
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = onAddMore,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("add_more_photos_button"),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, TRCCharcoalBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Añadir más",
                        tint = TRCGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Añadir", color = TRCWhite, style = MaterialTheme.typography.labelMedium)
                }

                IconButton(
                    onClick = onClearAll,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
                        .testTag("clear_all_photos_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Limpiar selección",
                        tint = TRCRust
                    )
                }
            }
        }

        // Display photos in a 3-column grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TRCCharcoalSoft)
                .border(BorderStroke(1.dp, TRCCharcoalBorder), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(photos, key = { it.toString() }) { photoUri ->
                    PhotoThumbnailItem(
                        uri = photoUri,
                        onClick = { onPhotoClick(photoUri) },
                        onRemove = { onRemovePhoto(photoUri) }
                    )
                }

                // Add more card item
                item {
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onAddMore)
                            .testTag("grid_add_more_card"),
                        colors = CardDefaults.cardColors(containerColor = TRCCharcoal),
                        border = BorderStroke(1.dp, TRCCharcoalBorder)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = TRCGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Añadir",
                                style = MaterialTheme.typography.labelSmall,
                                color = TRCGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoThumbnailItem(
    uri: Uri,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(TRCCharcoalElevated)
            .border(BorderStroke(0.5.dp, TRCCharcoalBorder), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .testTag("photo_thumbnail_item")
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Miniatura seleccionada",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Zoom hint overlay on bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ZoomIn,
                contentDescription = "Ampliar foto",
                tint = TRCGold,
                modifier = Modifier.size(14.dp)
            )
        }

        // Close/Remove Button on top-right
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.7f))
                .testTag("remove_photo_button")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Quitar foto",
                tint = TRCRust,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
