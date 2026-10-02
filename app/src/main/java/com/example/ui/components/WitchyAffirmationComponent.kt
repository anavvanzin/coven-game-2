package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WitchyAffirmation
import com.example.ui.theme.LocalCovenColors
import com.example.viewmodel.CovenViewModel

@Composable
fun WitchyAffirmationCard(
    viewModel: CovenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalCovenColors.current
    val affirmation by viewModel.dailyAffirmation.collectAsState()
    var isSavedToSpellbook by remember { mutableStateOf(false) }

    LaunchedEffect(affirmation.id) {
        isSavedToSpellbook = false
    }

    val infiniteTransition = rememberInfiniteTransition(label = "affirmation_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    var rotationDegree by remember { mutableStateOf(0f) }
    val auraColor = remember(affirmation.auraColorHex) { Color(affirmation.auraColorHex) }

    PixelCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("witchy_daily_affirmation_card"),
        backgroundColor = colors.surfaceCard,
        borderColor = auraColor,
        borderWidth = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Badge, Moon Phase & Shuffle Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = auraColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, auraColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨ DAILY WITCHY AFFIRMATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.primaryAccent,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = affirmation.moonPhase,
                        fontSize = 10.sp,
                        color = colors.textMuted,
                        fontFamily = FontFamily.Monospace
                    )

                    IconButton(
                        onClick = {
                            rotationDegree += 360f
                            viewModel.drawNewAffirmation()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("shuffle_affirmation_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Draw New Witchy Affirmation",
                            tint = colors.primaryAccent,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(rotationDegree)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Row: Pixel Icon Box + Quote Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    auraColor.copy(alpha = glowAlpha),
                                    colors.background
                                )
                            )
                        )
                        .border(
                            2.dp,
                            Brush.linearGradient(listOf(auraColor, colors.primaryAccent)),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = affirmation.iconEmoji,
                        fontSize = 30.sp,
                        modifier = Modifier.testTag("affirmation_pixel_icon")
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = affirmation.themeTitle.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = auraColor,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "\"${affirmation.quote}\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "— ${affirmation.authorOrOrigin}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primaryAccent,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lore Insight / Study Tip Banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = colors.background,
                border = BorderStroke(1.dp, colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔮", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = affirmation.loreTip,
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Save to Spellbook & Share Magic
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        viewModel.saveAffirmationToSpellbook(affirmation)
                        isSavedToSpellbook = true
                        Toast.makeText(context, "Inscribed into Grimoire Notes! ✨", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSavedToSpellbook) colors.secondaryAccent.copy(alpha = 0.25f) else colors.surfaceCardElevated,
                    border = BorderStroke(
                        1.dp,
                        if (isSavedToSpellbook) colors.secondaryAccent else colors.border
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("inscribe_affirmation_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSavedToSpellbook) Icons.Default.AutoAwesome else Icons.Default.BookmarkBorder,
                            contentDescription = "Save to Spellbook",
                            tint = if (isSavedToSpellbook) colors.secondaryAccent else colors.primaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSavedToSpellbook) "Inscribed! ✨" else "Save to Grimoire",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSavedToSpellbook) colors.secondaryAccent else colors.textPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Surface(
                    onClick = { shareAffirmation(context, affirmation) },
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceCardElevated,
                    border = BorderStroke(1.dp, colors.border),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("share_affirmation_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Blessing",
                            tint = colors.secondaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share Magic",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

private fun shareAffirmation(context: Context, affirmation: WitchyAffirmation) {
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "✨ Daily Witchy Study Affirmation")
            putExtra(
                Intent.EXTRA_TEXT,
                "${affirmation.iconEmoji} \"${affirmation.quote}\"\n\n— ${affirmation.authorOrOrigin}\n🔮 Study Tip: ${affirmation.loreTip}\n\nShared from Study Coven ✨"
            )
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Witchy Affirmation"))
    } catch (_: Exception) {
        Toast.makeText(context, "Unable to share blessing", Toast.LENGTH_SHORT).show()
    }
}
