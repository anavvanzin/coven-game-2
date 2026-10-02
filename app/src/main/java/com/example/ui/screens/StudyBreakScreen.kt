package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.LocalCovenColors
import com.example.viewmodel.CovenViewModel

@Composable
fun StudyBreakScreen(
    viewModel: CovenViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    val activePersona by viewModel.activePersona.collectAsState()
    val pomodoroSeconds by viewModel.pomodoroSecondsRemaining.collectAsState()
    val isPomodoroRunning by viewModel.isPomodoroRunning.collectAsState()
    val pomodoroMode by viewModel.pomodoroMode.collectAsState()

    var activeSubTab by remember { mutableStateOf("PotionBrew") } // "PotionBrew" or "Pomodoro"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PixelChip(
                    label = "🧪 Potions",
                    isSelected = activeSubTab == "PotionBrew",
                    onClick = { activeSubTab = "PotionBrew" },
                    modifier = Modifier.weight(1f)
                )
                PixelChip(
                    label = "⏳ Focus",
                    isSelected = activeSubTab == "Pomodoro",
                    onClick = { activeSubTab = "Pomodoro" },
                    modifier = Modifier.weight(1f)
                )
                PixelChip(
                    label = "🧙‍♀️ Buddy",
                    isSelected = activeSubTab == "StudyBuddy",
                    onClick = { activeSubTab = "StudyBuddy" },
                    modifier = Modifier.weight(1f)
                )
                PixelChip(
                    label = "🎵 Sounds",
                    isSelected = activeSubTab == "Soundscapes",
                    onClick = { activeSubTab = "Soundscapes" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when (activeSubTab) {
            "Soundscapes" -> {
                item {
                    AmbientSoundscapesCard(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            "StudyBuddy" -> {
                item {
                    StudyBuddySelectorComponent(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    PixelBestiesStudyDeskCard(
                        activePersona = activePersona,
                        isFocusMode = isPomodoroRunning,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            "PotionBrew" -> {
                item {
                    PotionBrewingGame(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            "Pomodoro" -> {
                item {
                    val minutes = pomodoroSeconds / 60
                    val seconds = pomodoroSeconds % 60
                    val formattedTime = "%02d:%02d".format(minutes, seconds)

                    PixelCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pomodoro_focus_card"),
                        backgroundColor = colors.surfaceCard,
                        borderColor = colors.secondaryAccent
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⏳ FAMILIAR STUDY FOCUS CLOCK",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = colors.secondaryAccent,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = pomodoroMode,
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Giant Timer Display in Pixel Box
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(CircleShape)
                                    .background(colors.background)
                                    .border(4.dp, if (isPomodoroRunning) colors.primaryAccent else colors.border, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formattedTime,
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isPomodoroRunning) colors.primaryAccent else colors.textPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = if (isPomodoroRunning) "✨ In Spell Flow ✨" else "Ready",
                                        fontSize = 10.sp,
                                        color = colors.textMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PixelButton(
                                    text = if (isPomodoroRunning) "Pause Focus" else "Start Sprint",
                                    onClick = { viewModel.togglePomodoro() },
                                    icon = if (isPomodoroRunning) "⏸️" else "▶️",
                                    backgroundColor = if (isPomodoroRunning) colors.secondaryAccent else colors.primaryAccent,
                                    textColor = colors.background,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("toggle_pomodoro_button")
                                )
                                PixelButton(
                                    text = "Reset",
                                    onClick = { viewModel.resetPomodoro(25, "25m Study Focus") },
                                    icon = "🔄",
                                    backgroundColor = colors.surfaceCardElevated,
                                    textColor = colors.textPrimary,
                                    modifier = Modifier.testTag("reset_pomodoro_button")
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(Pair(25, "25m Focus"), Pair(5, "5m Break"), Pair(15, "15m Long Break")).forEach { (min, label) ->
                                    PixelChip(
                                        label = label,
                                        isSelected = pomodoroSeconds == min * 60,
                                        onClick = { viewModel.resetPomodoro(min, label) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Pixel Art Study Companions (Ana & Sabrina)
                item {
                    PixelBestiesStudyDeskCard(
                        activePersona = activePersona,
                        isFocusMode = isPomodoroRunning,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Ambient Soundscapes Player (Rainy Forest, Fireplace, Ancient Library)
                item {
                    AmbientSoundscapesCard(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Daily Affirmation Component directly inside Study Session
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔮 STUDY SESSION AFFIRMATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryAccent,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    WitchyAffirmationCard(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
