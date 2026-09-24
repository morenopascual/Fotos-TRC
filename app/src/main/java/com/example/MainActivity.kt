package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TRCCharcoal
import com.example.ui.theme.TRCCharcoalBorder
import com.example.ui.theme.TRCCharcoalSoft
import com.example.ui.theme.TRCGold
import com.example.ui.theme.TRCGray
import com.example.ui.theme.TRCWhite
import com.example.ui.viewmodel.RugbyPhotosViewModel

enum class NavScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Subir Fotos", Icons.Filled.AddPhotoAlternate, Icons.Outlined.AddPhotoAlternate, "tab_home"),
    HISTORY("Historial", Icons.Filled.History, Icons.Outlined.History, "tab_history"),
    SETTINGS("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

class MainActivity : ComponentActivity() {
    private val viewModel: RugbyPhotosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RugbyApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RugbyApp(viewModel: RugbyPhotosViewModel) {
    var currentTab by rememberSaveable { mutableStateOf(NavScreen.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = TRCCharcoal,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_trc_badge),
                            contentDescription = "TRC Ant Badge",
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "TRC FOTOS",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TRCWhite,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TRCCharcoal
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = TRCCharcoal,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(BorderStroke(0.5.dp, TRCCharcoalBorder))
                    .testTag("bottom_nav_bar")
            ) {
                NavScreen.entries.forEach { screen ->
                    val isSelected = currentTab == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TRCCharcoal,
                            selectedTextColor = TRCGold,
                            indicatorColor = TRCGold,
                            unselectedIconColor = TRCGray,
                            unselectedTextColor = TRCGray
                        ),
                        modifier = Modifier.testTag(screen.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TRCCharcoal)
        ) {
            when (currentTab) {
                NavScreen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { currentTab = NavScreen.HISTORY }
                )
                NavScreen.HISTORY -> HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { currentTab = NavScreen.HOME }
                )
                NavScreen.SETTINGS -> SettingsScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
