package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RugbyCategory
import com.example.ui.theme.TRCCharcoal
import com.example.ui.theme.TRCCharcoalBorder
import com.example.ui.theme.TRCCharcoalSoft
import com.example.ui.theme.TRCGold
import com.example.ui.theme.TRCGray
import com.example.ui.theme.TRCPitchGreen
import com.example.ui.theme.TRCWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryChipSelector(
    selectedCategory: RugbyCategory,
    onCategorySelected: (RugbyCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header with TRC typography
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Diagonal Gold accent mark
                Box(
                    modifier = Modifier
                        .size(width = 8.dp, height = 18.dp)
                        .background(TRCGold, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CATEGORÍA TRC",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = TRCGold
                )
            }

            // Tag showing if it is Escuela / Cantera (Verde) or Club (Charcoal)
            Surface(
                color = if (selectedCategory.isCantera) TRCPitchGreen else TRCCharcoalBorder,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (selectedCategory.isCantera) "ESCUELA / CANTERA" else "SÉNIOR / CLUB",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TRCWhite,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Chip selection grid
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RugbyCategory.entries.forEach { category ->
                val isSelected = category == selectedCategory
                TRCCategoryChip(
                    category = category,
                    isSelected = isSelected,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
private fun TRCCategoryChip(
    category: RugbyCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected && category.isCantera -> TRCPitchGreen
            isSelected -> TRCGold
            else -> TRCCharcoalSoft
        },
        label = "trc_chip_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isSelected && category.isCantera -> TRCWhite
            isSelected -> TRCCharcoal
            else -> TRCWhite
        },
        label = "trc_chip_content"
    )

    val borderStroke = when {
        isSelected && category.isCantera -> BorderStroke(2.dp, TRCGold)
        isSelected -> BorderStroke(2.dp, TRCGold)
        else -> BorderStroke(1.dp, TRCCharcoalBorder)
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                role = Role.RadioButton,
                onClick = onClick
            )
            .testTag("category_chip_${category.code}"),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = borderStroke,
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 44.dp)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
            } else {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (category.isCantera) Color(0xFF8FD19E) else TRCGold
                )
            }

            Column {
                Text(
                    text = category.title.uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = contentColor
                )
            }

            // Monospace code tag
            Text(
                text = category.code,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected) contentColor.copy(alpha = 0.8f) else TRCGray
            )
        }
    }
}
