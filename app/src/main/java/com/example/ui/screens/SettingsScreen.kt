package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClubConfig
import com.example.data.model.RugbyCategory
import com.example.ui.theme.TRCCharcoal
import com.example.ui.theme.TRCCharcoalBorder
import com.example.ui.theme.TRCCharcoalSoft
import com.example.ui.theme.TRCGold
import com.example.ui.theme.TRCGray
import com.example.ui.theme.TRCWhite
import com.example.ui.viewmodel.RugbyPhotosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: RugbyPhotosViewModel,
    modifier: Modifier = Modifier
) {
    val currentConfig by viewModel.clubConfig.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var clubName by remember(currentConfig) { mutableStateOf(currentConfig.clubName) }
    var stadiumName by remember(currentConfig) { mutableStateOf(currentConfig.stadiumName) }
    var photosUrl by remember(currentConfig) { mutableStateOf(currentConfig.googlePhotosAlbumUrl) }
    var email by remember(currentConfig) { mutableStateOf(currentConfig.communicationEmail) }
    var defaultCategory by remember(currentConfig) { mutableStateOf(currentConfig.defaultCategory) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 20.dp)
                    .background(TRCGold, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CONFIGURACIÓN TRC",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = TRCWhite
            )
        }

        // Official TRC Mascot Identity Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = TRCCharcoalSoft),
            border = BorderStroke(1.dp, TRCCharcoalBorder)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_trc_badge),
                    contentDescription = "Emblema Oficial TRC",
                    modifier = Modifier.size(46.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "IDENTIDAD OFICIAL TRC",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TRCGold
                    )
                    Text(
                        text = "Torrelodones Rugby Club · 2026/27",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TRCWhite
                    )
                    Text(
                        text = "Construido en el campo. Unidos después.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TRCGray
                    )
                }
            }
        }

        Text(
            text = "Parámetros del Torrelodones Rugby Club para canalizar las fotos a Google Photos y al equipo de Comunicación.",
            style = MaterialTheme.typography.bodyMedium,
            color = TRCGray
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = TRCCharcoalSoft),
            border = BorderStroke(1.dp, TRCCharcoalBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Club Name
                OutlinedTextField(
                    value = clubName,
                    onValueChange = { clubName = it },
                    label = { Text("Nombre del Club") },
                    leadingIcon = {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = TRCGold)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("club_name_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TRCCharcoal,
                        unfocusedContainerColor = TRCCharcoal,
                        focusedBorderColor = TRCGold,
                        unfocusedBorderColor = TRCCharcoalBorder,
                        focusedLabelColor = TRCGold,
                        unfocusedLabelColor = TRCGray,
                        focusedTextColor = TRCWhite,
                        unfocusedTextColor = TRCWhite
                    ),
                    singleLine = true
                )

                // Stadium / Field Name
                OutlinedTextField(
                    value = stadiumName,
                    onValueChange = { stadiumName = it },
                    label = { Text("Campo de Juego") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TRCGold)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stadium_name_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TRCCharcoal,
                        unfocusedContainerColor = TRCCharcoal,
                        focusedBorderColor = TRCGold,
                        unfocusedBorderColor = TRCCharcoalBorder,
                        focusedLabelColor = TRCGold,
                        unfocusedLabelColor = TRCGray,
                        focusedTextColor = TRCWhite,
                        unfocusedTextColor = TRCWhite
                    ),
                    singleLine = true
                )

                // Google Photos Album URL
                OutlinedTextField(
                    value = photosUrl,
                    onValueChange = { photosUrl = it },
                    label = { Text("Enlace Álbum Google Photos TRC") },
                    placeholder = { Text("https://photos.app.goo.gl/...") },
                    leadingIcon = {
                        Icon(Icons.Default.Link, contentDescription = null, tint = TRCGold)
                    },
                    supportingText = {
                        Text("Enlace del álbum compartido del Torrelodones Rugby Club.", color = TRCGray)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("photos_url_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TRCCharcoal,
                        unfocusedContainerColor = TRCCharcoal,
                        focusedBorderColor = TRCGold,
                        unfocusedBorderColor = TRCCharcoalBorder,
                        focusedLabelColor = TRCGold,
                        unfocusedLabelColor = TRCGray,
                        focusedTextColor = TRCWhite,
                        unfocusedTextColor = TRCWhite
                    )
                )

                OutlinedButton(
                    onClick = { viewModel.openClubPhotosAlbum(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp),
                    border = BorderStroke(1.dp, TRCCharcoalBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TRCGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Probar y Abrir Álbum en Google Photos", color = TRCWhite)
                }

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email de Comunicación TRC") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = TRCGold)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TRCCharcoal,
                        unfocusedContainerColor = TRCCharcoal,
                        focusedBorderColor = TRCGold,
                        unfocusedBorderColor = TRCCharcoalBorder,
                        focusedLabelColor = TRCGold,
                        unfocusedLabelColor = TRCGray,
                        focusedTextColor = TRCWhite,
                        unfocusedTextColor = TRCWhite
                    ),
                    singleLine = true
                )

                // Default Category
                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = defaultCategory.title.uppercase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría Predeterminada") },
                        supportingText = { Text("Categoría por defecto al abrir la aplicación", color = TRCGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                        leadingIcon = {
                            Icon(Icons.Default.SportsScore, contentDescription = null, tint = TRCGold)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TRCCharcoal,
                            unfocusedContainerColor = TRCCharcoal,
                            focusedBorderColor = TRCGold,
                            unfocusedBorderColor = TRCCharcoalBorder,
                            focusedLabelColor = TRCGold,
                            unfocusedLabelColor = TRCGray,
                            focusedTextColor = TRCWhite,
                            unfocusedTextColor = TRCWhite
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        RugbyCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${cat.title.uppercase()} (${cat.description})",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    defaultCategory = cat
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Save Button (TRC Gold accent)
        Button(
            onClick = {
                val updated = currentConfig.copy(
                    clubName = clubName.trim().ifBlank { "Torrelodones Rugby Club" },
                    stadiumName = stadiumName.trim().ifBlank { "Campo Julián Ariza" },
                    googlePhotosAlbumUrl = photosUrl.trim().ifBlank { "https://photos.app.goo.gl/trc-torrelodones-rugby" },
                    communicationEmail = email.trim().ifBlank { "comunicacion@torrelodonesrugby.com" },
                    defaultCategory = defaultCategory
                )
                viewModel.updateClubConfig(updated)
                Toast.makeText(context, "Ajustes de TRC guardados correctamente", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 52.dp)
                .testTag("save_settings_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TRCGold,
                contentColor = TRCCharcoal
            )
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = TRCCharcoal)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Guardar Configuración TRC",
                fontWeight = FontWeight.Black,
                color = TRCCharcoal
            )
        }
    }
}
