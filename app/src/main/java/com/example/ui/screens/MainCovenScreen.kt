package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CovenPersona
import com.example.ui.components.MiniSoundscapeBar
import com.example.ui.components.PixelWitchAvatar
import com.example.ui.theme.CovenThemePalette
import com.example.ui.theme.LocalCovenColors
import com.example.viewmodel.CovenNavTab
import com.example.viewmodel.CovenViewModel

@Composable
fun MainCovenScreen(
    viewModel: CovenViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    val currentTab by viewModel.currentTab.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()
    val activeStudyBuff by viewModel.activeStudyBuff.collectAsState()
    val currentPalette by viewModel.themePalette.collectAsState()

    var showPersonaMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Logo & Buff Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if (activeStudyBuff != null) viewModel.setTab(CovenNavTab.POTIONS)
                        }
                    ) {
                        Text(if (activeStudyBuff != null) activeStudyBuff!!.emoji else "🔮", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "STUDY COVEN",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.primaryAccent,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            if (activeStudyBuff != null) {
                                Text(
                                    text = "BUFF: ${activeStudyBuff!!.title}".uppercase(),
                                    fontSize = 9.sp,
                                    color = colors.secondaryAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Actions: Theme Switcher ('Deep Forest' vs 'Mystic Moon') & Persona Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Theme Switcher Button
                        Surface(
                            onClick = { viewModel.toggleThemePalette() },
                            shape = RoundedCornerShape(8.dp),
                            color = colors.surfaceCardElevated,
                            border = BorderStroke(1.dp, colors.border),
                            modifier = Modifier.testTag("theme_switcher_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (currentPalette == CovenThemePalette.DEEP_FOREST) "🌲 Forest" else "🌙 Moon",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryAccent,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Persona Selector Dropdown Chip
                        Box {
                            Surface(
                                onClick = { showPersonaMenu = true },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(activePersona.tagColorHex).copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color(activePersona.tagColorHex)),
                                modifier = Modifier.testTag("persona_dropdown_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PixelWitchAvatar(persona = activePersona, size = 20.dp, showHat = false)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = activePersona.displayName.take(7),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(" ▾", fontSize = 10.sp, color = colors.textMuted)
                                }
                            }

                            DropdownMenu(
                                expanded = showPersonaMenu,
                                onDismissRequest = { showPersonaMenu = false },
                                modifier = Modifier.background(colors.surfaceCardElevated)
                            ) {
                                CovenPersona.values().forEach { persona ->
                                    val isSelected = activePersona == persona
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                PixelWitchAvatar(persona = persona, size = 26.dp, showHat = true)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = persona.displayName,
                                                    color = if (isSelected) colors.primaryAccent else colors.textPrimary,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.setPersona(persona)
                                            showPersonaMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                MiniSoundscapeBar(
                    viewModel = viewModel,
                    onOpenSoundscapes = {
                        viewModel.setTab(CovenNavTab.FOCUS)
                    }
                )

                NavigationBar(
                    containerColor = colors.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .border(1.dp, colors.border, RoundedCornerShape(0.dp))
                        .testTag("coven_bottom_nav")
                ) {
                CovenNavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            Text(
                                text = tab.iconEmoji,
                                fontSize = if (isSelected) 22.sp else 18.sp
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected) colors.primaryAccent else colors.textMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.primaryAccent,
                            unselectedIconColor = colors.textMuted,
                            indicatorColor = colors.surfaceCardElevated
                        )
                    )
                }
            }
        }
    }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                CovenNavTab.QUESTS -> TasksAndCalendarScreen(viewModel = viewModel)
                CovenNavTab.SPELLBOOK -> SpellbookAndWhiteboardScreen(viewModel = viewModel)
                CovenNavTab.POTIONS -> StudyBreakScreen(viewModel = viewModel)
                CovenNavTab.FOCUS -> StudyBreakScreen(viewModel = viewModel)
            }
        }
    }
}
