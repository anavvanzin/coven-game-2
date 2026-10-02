package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CovenPersona
import com.example.ui.theme.LocalCovenColors
import com.example.viewmodel.CovenViewModel

/**
 * A dedicated, interactive Compose UI component that showcases the pixel art
 * of Ana (brunette with black hair and white eyebrows) and Sabrina (blonde), allowing users to
 * toggle between them to act as their personal 'study buddy' avatar.
 */
@Composable
fun StudyBuddySelectorComponent(
    viewModel: CovenViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    val activePersona by viewModel.activePersona.collectAsState()
    val isPomodoroRunning by viewModel.isPomodoroRunning.collectAsState()

    var showHat by remember { mutableStateOf(true) }
    var cheerHeartsCount by remember { mutableIntStateOf(12) }
    var currentDialogueIndex by remember { mutableIntStateOf(0) }
    var isPetted by remember { mutableStateOf(false) }

    // Dialogue banks tailored to the blonde and brunette characters
    val anaDialogues = remember {
        listOf(
            "\"Hey bestie! I brewed fresh mint & chamomile tea for our study sprint! 🌿\"",
            "\"Remember to stretch your neck and drink some water! We've got this! ✨\"",
            "\"Potions take patience, and so do good grades. One flashcard at a time! 🧪\"",
            "\"High five! Sabrina and I are super proud of how hard you're trying! 💛\""
        )
    }

    val sabrinaDialogues = remember {
        listOf(
            "\"The celestial constellations are in perfect alignment for memory retention. 🌙\"",
            "\"Take a deep breath. Focus your mana, and let's conquer this chapter together. 🔮\"",
            "\"I noted down the hardest formulas in the grimoire for you to review! 📖\"",
            "\"Quiet focus yields the most powerful spells. You are unstoppable today. 💜\""
        )
    }

    val duoDialogues = remember {
        listOf(
            "\"Ana & Sabrina: Double the coven magic! We're studying side by side with you! ✨\"",
            "\"Ana brings the snacks, Sabrina brings the spell notes—we make the dream team! 💫\"",
            "\"Study sprint activated! Let's earn those coven XP points together! 🏆\""
        )
    }

    val currentDialogue = remember(activePersona, currentDialogueIndex) {
        when (activePersona) {
            CovenPersona.ANA -> anaDialogues[currentDialogueIndex % anaDialogues.size]
            CovenPersona.SABRINA -> sabrinaDialogues[currentDialogueIndex % sabrinaDialogues.size]
            CovenPersona.DUO -> duoDialogues[currentDialogueIndex % duoDialogues.size]
        }
    }

    // Heart bounce animation when petted/cheered
    val infiniteTransition = rememberInfiniteTransition(label = "buddy_aura")
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    PixelCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("study_buddy_selector_component"),
        backgroundColor = colors.surfaceCard,
        borderColor = if (activePersona == CovenPersona.ANA) Color(0xFF6EE7B7) else Color(0xFFB588FF),
        borderWidth = 1.5.dp,
        contentPadding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Title & Active Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧙‍♀️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "CHOOSE YOUR STUDY BUDDY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.primaryAccent,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Select your pixel witch companion for sessions",
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                // Hat/Ribbon toggle switch
                Surface(
                    onClick = { showHat = !showHat },
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceCardElevated,
                    border = BorderStroke(1.dp, colors.border),
                    modifier = Modifier.testTag("study_buddy_hat_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (showHat) "🎩 Hat On" else "🎀 Casual", fontSize = 10.sp, color = colors.secondaryAccent, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Character Toggle Cards: Ana vs Sabrina
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Character 1: Ana (Brunette with Black Hair & White Eyebrows)
                val isAnaSelected = activePersona == CovenPersona.ANA
                Surface(
                    onClick = {
                        viewModel.setPersona(CovenPersona.ANA)
                        currentDialogueIndex = 0
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAnaSelected) Color(0xFF6EE7B7).copy(alpha = 0.18f) else colors.surfaceCardElevated,
                    border = BorderStroke(
                        if (isAnaSelected) 2.dp else 1.dp,
                        if (isAnaSelected) Color(0xFF6EE7B7) else colors.border
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("study_buddy_toggle_ana")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pixel Art of Ana (Brunette with Black Hair & White Eyebrows)
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF6EE7B7).copy(alpha = if (isAnaSelected) 0.35f else 0.15f))
                                .border(1.dp, Color(0xFF6EE7B7), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            PixelWitchAvatar(
                                persona = CovenPersona.ANA,
                                size = 58.dp,
                                showHat = showHat,
                                isStudying = isPomodoroRunning
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "ANA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isAnaSelected) Color(0xFF6EE7B7) else colors.textPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Potions & Herbology",
                            fontSize = 9.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Selection indicator
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isAnaSelected) Color(0xFF6EE7B7) else colors.surface,
                            border = BorderStroke(1.dp, if (isAnaSelected) Color(0xFF6EE7B7) else colors.border)
                        ) {
                            Text(
                                text = if (isAnaSelected) "✓ ACTIVE" else "SELECT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAnaSelected) colors.background else colors.textMuted,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Character 2: Blonde Girl (Sabrina)
                val isSabrinaSelected = activePersona == CovenPersona.SABRINA
                Surface(
                    onClick = {
                        viewModel.setPersona(CovenPersona.SABRINA)
                        currentDialogueIndex = 0
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSabrinaSelected) Color(0xFFB588FF).copy(alpha = 0.18f) else colors.surfaceCardElevated,
                    border = BorderStroke(
                        if (isSabrinaSelected) 2.dp else 1.dp,
                        if (isSabrinaSelected) Color(0xFFB588FF) else colors.border
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("study_buddy_toggle_sabrina")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pixel Art of Sabrina (Blonde)
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFB588FF).copy(alpha = if (isSabrinaSelected) 0.35f else 0.15f))
                                .border(1.dp, Color(0xFFB588FF), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            PixelWitchAvatar(
                                persona = CovenPersona.SABRINA,
                                size = 58.dp,
                                showHat = showHat,
                                isStudying = isPomodoroRunning
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "SABRINA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isSabrinaSelected) Color(0xFFB588FF) else colors.textPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Astral Runes & Focus",
                            fontSize = 9.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Selection indicator
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSabrinaSelected) Color(0xFFB588FF) else colors.surface,
                            border = BorderStroke(1.dp, if (isSabrinaSelected) Color(0xFFB588FF) else colors.border)
                        ) {
                            Text(
                                text = if (isSabrinaSelected) "✓ ACTIVE" else "SELECT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSabrinaSelected) colors.background else colors.textMuted,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 3: Duo / Both Girls Together Pill Button
            val isDuoSelected = activePersona == CovenPersona.DUO
            Surface(
                onClick = {
                    viewModel.setPersona(CovenPersona.DUO)
                    currentDialogueIndex = 0
                },
                shape = RoundedCornerShape(10.dp),
                color = if (isDuoSelected) Color(0xFFF7C948).copy(alpha = 0.2f) else colors.surfaceCardElevated,
                border = BorderStroke(
                    1.dp,
                    if (isDuoSelected) Color(0xFFF7C948) else colors.border
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("study_buddy_toggle_duo")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "COVEN DUO (Both Study Besties)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDuoSelected) Color(0xFFF7C948) else colors.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Ana & Sabrina studying side by side (+25% Companion Buff)",
                                fontSize = 9.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Text(
                        text = if (isDuoSelected) "★ ACTIVE" else "SELECT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDuoSelected) Color(0xFFF7C948) else colors.textMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Study Buddy Stage & Speech Bubble
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        cheerHeartsCount += 1
                        viewModel.addXp(5)
                        currentDialogueIndex += 1
                        isPetted = true
                    }
                    .testTag("study_buddy_avatar_stage"),
                color = colors.surfaceCardElevated,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Buddy Stage Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isPomodoroRunning) Color(0xFFEF4444) else Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPomodoroRunning) "BUDDY STUDYING WITH YOU" else "BUDDY READY TO SPRINT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryAccent,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Heart cheer meter (Tap to cheer)
                        Surface(
                            onClick = {
                                cheerHeartsCount += 1
                                viewModel.addXp(5)
                                currentDialogueIndex += 1
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFF6584).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFFF6584)),
                            modifier = Modifier.testTag("study_buddy_cheer_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💖", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$cheerHeartsCount Cheer",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF6584),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Speech bubble with character's voice line
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = colors.background,
                        border = BorderStroke(1.dp, colors.border)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (activePersona) {
                                    CovenPersona.ANA -> "🧙‍♀️"
                                    CovenPersona.SABRINA -> "👱‍♀️"
                                    CovenPersona.DUO -> "👭"
                                },
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentDialogue,
                                fontSize = 11.sp,
                                color = colors.textPrimary,
                                lineHeight = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tip hint
                    Text(
                        text = "💡 Tap your buddy to high-five, hear new quotes, & earn +5 XP!",
                        fontSize = 9.sp,
                        color = colors.textMuted,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
