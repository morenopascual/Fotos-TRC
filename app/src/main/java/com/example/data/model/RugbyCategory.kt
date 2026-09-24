package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Categorías oficiales del Torrelodones Rugby Club (TRC):
 * - Escuela
 * - Sub 14
 * - Sub 16
 * - Sub 18
 * - Senior Masculino
 * - Senior Femenino
 * - Touch
 * - Veteranos
 */
enum class RugbyCategory(
    val title: String,
    val code: String,
    val description: String,
    val isCantera: Boolean,
    val icon: ImageVector
) {
    ESCUELA("Escuela", "ESCUELA", "Escuela TRC (Iniciación y Base)", true, Icons.Default.ChildCare),
    SUB_14("Sub 14", "SUB14", "Cantera TRC (M-14)", true, Icons.Default.SportsScore),
    SUB_16("Sub 16", "SUB16", "Cantera TRC (M-16)", true, Icons.Default.FitnessCenter),
    SUB_18("Sub 18", "SUB18", "Cantera TRC (M-18)", true, Icons.Default.Groups),
    SENIOR_MASCULINO("Senior Masculino", "SEN_MASC", "Senior Masculino TRC", false, Icons.Default.FitnessCenter),
    SENIOR_FEMENINO("Senior Femenino", "SEN_FEM", "Senior Femenino TRC", false, Icons.Default.Group),
    TOUCH("Touch", "TOUCH", "Touch Rugby TRC", false, Icons.Default.TouchApp),
    VETERANOS("Veteranos", "VET", "Veteranos TRC (+35)", false, Icons.Default.Elderly);

    companion object {
        fun fromCode(code: String?): RugbyCategory {
            if (code == null) return ESCUELA
            return entries.firstOrNull { 
                it.name.equals(code, ignoreCase = true) || 
                it.code.equals(code, ignoreCase = true) ||
                it.title.equals(code, ignoreCase = true)
            } ?: when (code.uppercase()) {
                "SUB10", "SUB12" -> ESCUELA
                "SENIOR", "SENIOR_M", "SEN_M" -> SENIOR_MASCULINO
                "SENIOR_F", "SEN_F" -> SENIOR_FEMENINO
                else -> ESCUELA
            }
        }
    }
}
