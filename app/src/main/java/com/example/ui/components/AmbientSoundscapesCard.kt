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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CovenPersona
import com.example.data.model.SoundscapeType
import com.example.ui.theme.LocalCovenColors
import com.example.viewmodel.CovenViewModel

/**
 * Animated pixel sound wave / equalizer bars that bounce when audio is active.
 */
@Composable
fun AnimatedSoundVisualizer(
    isPlaying: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    barCount: Int = 12
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_visualizer")
    
    Row(
        modifier = modifier.height(28.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until barCount) {
            val duration = remember(i) { 450 + (i * 90) % 650 }
            val minHeight = 4.dp
            val maxHeight = remember(i) { (12 + (i * 7) % 16).dp }

            val barHeight by if (isPlaying) {
                infiniteTransition.animateValue(
                    initialValue = minHeight,
                    targetValue = maxHeight,
                    typeConverter = androidx.compose.ui.unit.Dp.VectorConverter,
                    animationSpec = infiniteRepeatable(
                        animation = tween(duration, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bar_$i"
                )
            } else {
                remember { mutableStateOf(minHeight) }
            }

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isPlaying) accentColor else accentColor.copy(alpha = 0.3f))
            )
        }
    }
}

/**
 * Full-featured Ambient Soundscapes study card allowing toggling between
 * different study-focused audio loops like 'Rainy Forest,' 'Crackling Fireplace,'
 * and 'Ancient Library.'
 */
@Composable
fun AmbientSoundscapesCard(
    viewModel: CovenViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    val soundscapeState by viewModel.soundscapeState.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()

    val currentType = soundscapeState.currentType
    val isPlaying = soundscapeState.isPlaying
    val volume = soundscapeState.volume
    val accentColor = Color(currentType.colorHex)

    PixelCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ambient_soundscapes_card"),
        backgroundColor = colors.surfaceCard,
        borderColor = if (isPlaying) accentColor else colors.border,
        borderWidth = if (isPlaying) 1.5.dp else 1.dp,
        contentPadding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Title, Playing Status & Visualizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(currentType.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "AMBIENT SOUNDSCAPES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.primaryAccent,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isPlaying) "Playing: ${currentType.title}" else "Tap loop to start focus audio",
                            fontSize = 10.sp,
                            color = if (isPlaying) accentColor else colors.textSecondary,
                            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Audio Spectrum Equalizer
                AnimatedSoundVisualizer(
                    isPlaying = isPlaying,
                    accentColor = accentColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Soundscape Selection Cards Grid (Rainy Forest, Fireplace, Library, etc.)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SoundscapeType.values().forEach { soundscape ->
                    val isSelected = currentType == soundscape
                    val isCardPlaying = isSelected && isPlaying
                    val cardAccent = Color(soundscape.colorHex)

                    Surface(
                        onClick = {
                            if (isSelected) {
                                viewModel.toggleSoundscape()
                            } else {
                                viewModel.toggleSoundscape(soundscape)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) cardAccent.copy(alpha = 0.15f) else colors.surfaceCardElevated,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) cardAccent else colors.border
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("soundscape_option_${soundscape.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(soundscape.iconEmoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = soundscape.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) cardAccent else colors.textPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = soundscape.subtitle,
                                        fontSize = 10.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            // Quick Play / State indicator button
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCardPlaying) cardAccent else colors.surface,
                                border = BorderStroke(1.dp, if (isSelected) cardAccent else colors.border)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isCardPlaying) "⏸️ PLAYING" else if (isSelected) "▶️ RESUME" else "▶️ PLAY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCardPlaying) colors.background else if (isSelected) cardAccent else colors.textMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lore Description of currently selected soundscape
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = colors.surfaceCardElevated,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "📜 COVEN ATMOSPHERE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.secondaryAccent,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentType.loreDescription,
                        fontSize = 10.sp,
                        color = colors.textSecondary,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Master Volume Control & Sleep Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeDown,
                        contentDescription = "Volume",
                        tint = colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VOLUME: ${(volume * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Master Toggle Button
                PixelButton(
                    text = if (isPlaying) "Pause Audio" else "Play Soundscape",
                    onClick = { viewModel.toggleSoundscape() },
                    icon = if (isPlaying) "⏸️" else "▶️",
                    backgroundColor = if (isPlaying) accentColor else colors.primaryAccent,
                    textColor = colors.background,
                    modifier = Modifier.testTag("soundscape_master_toggle")
                )
            }

            // Volume Slider
            Slider(
                value = volume,
                onValueChange = { viewModel.setSoundscapeVolume(it) },
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = colors.border
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("soundscape_volume_slider")
            )

            // Sleep Timer Presets
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUTO-OFF TIMER",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textMuted,
                    fontFamily = FontFamily.Monospace
                )
                if (soundscapeState.selectedTimerPreset > 0 && soundscapeState.timerRemainingSeconds > 0) {
                    val m = soundscapeState.timerRemainingSeconds / 60
                    val s = soundscapeState.timerRemainingSeconds % 60
                    Text(
                        text = "⏳ %02d:%02d".format(m, s),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair(0, "♾️ Loop"),
                    Pair(15, "15m"),
                    Pair(25, "25m Focus"),
                    Pair(45, "45m")
                ).forEach { (presetMin, label) ->
                    val isTimerSelected = soundscapeState.selectedTimerPreset == presetMin
                    PixelChip(
                        label = label,
                        isSelected = isTimerSelected,
                        onClick = { viewModel.setSoundscapeTimer(presetMin) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Study Buddy Commentary based on selected soundscape
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = colors.background,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (activePersona) {
                            CovenPersona.ANA -> "🧙‍♀️"
                            CovenPersona.SABRINA -> "👱‍♀️"
                            CovenPersona.DUO -> "👭"
                        },
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (currentType) {
                            SoundscapeType.RAINY_FOREST -> "Ana: The rain outside makes our study sanctum feel ten times cozier! 🌲"
                            SoundscapeType.CRACKLING_FIREPLACE -> "Sabrina: The crackling hearth fire calms racing exam thoughts. 🔥"
                            SoundscapeType.ANCIENT_LIBRARY -> "Ana & Sabrina: Feel the scholarly focus of generations of witches! 📚"
                            SoundscapeType.MIDNIGHT_SANCTUM -> "Sabrina: Theta binaural waves synchronize mind and memory. 🌙"
                            SoundscapeType.BUBBLING_CAULDRON -> "Ana: Brewing chamomile potion while reviewing flashcards! 🧪"
                        },
                        fontSize = 10.sp,
                        color = colors.textPrimary,
                        lineHeight = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

/**
 * Compact persistent floating banner displayed when a soundscape is active,
 * allowing instant pause/play and soundscape switching from any screen!
 */
@Composable
fun MiniSoundscapeBar(
    viewModel: CovenViewModel,
    onOpenSoundscapes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    val soundscapeState by viewModel.soundscapeState.collectAsState()

    if (!soundscapeState.isPlaying) return

    val currentType = soundscapeState.currentType
    val accentColor = Color(currentType.colorHex)

    Surface(
        onClick = onOpenSoundscapes,
        shape = RoundedCornerShape(12.dp),
        color = colors.surface.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, accentColor),
        shadowElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("mini_soundscape_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(currentType.iconEmoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = currentType.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Ambient Audio • ${(soundscapeState.volume * 100).toInt()}% Vol",
                        fontSize = 9.sp,
                        color = accentColor
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedSoundVisualizer(
                    isPlaying = true,
                    accentColor = accentColor,
                    barCount = 6,
                    modifier = Modifier.height(18.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    onClick = { viewModel.toggleSoundscape() },
                    shape = CircleShape,
                    color = accentColor,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause Soundscape",
                            tint = colors.background,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
