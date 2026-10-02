package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CovenPersona
import com.example.ui.theme.LocalCovenColors
import kotlin.random.Random

// Color palettes for authentic retro pixel art
private object PixelColors {
    // Shared Skin & Warmth
    val skinTone = Color(0xFFFFDFC4)
    val skinShadow = Color(0xFFF0BFA0)
    val blush = Color(0xFFFF9EAA)
    val eyeWhite = Color(0xFFFFFFFF)
    val eyePupil = Color(0xFF1E1B2E)

    // Ana: Brunette with Black Hair & White Eyebrows (Potions Witch)
    val anaHairLight = Color(0xFF383230)
    val anaHairBase = Color(0xFF1E1B1A)
    val anaHairShadow = Color(0xFF100F0E)
    val anaEyebrowWhite = Color(0xFFFFFFFF)
    val anaEye = Color(0xFF2DD4BF)
    val anaHatBase = Color(0xFF1B4332)
    val anaHatTrim = Color(0xFF40916C)
    val anaHatBuckle = Color(0xFFF9C74F)
    val anaRobe = Color(0xFF2D6A4F)
    val anaRobeAccent = Color(0xFF95D5B2)

    // Sabrina: Blonde Astral Witch
    val sabrinaHairLight = Color(0xFFFFEE88)
    val sabrinaHairBase = Color(0xFFFFD152)
    val sabrinaHairShadow = Color(0xFFE5A922)
    val sabrinaEye = Color(0xFFC084FC)
    val sabrinaHatBase = Color(0xFF241442)
    val sabrinaHatTrim = Color(0xFF4C2882)
    val sabrinaHatMoon = Color(0xFFFDE047)
    val sabrinaRobe = Color(0xFF3B1D66)
    val sabrinaRobeAccent = Color(0xFFE9D5FF)

    // Scene & Environment
    val deskWood = Color(0xFF5D4037)
    val deskWoodLight = Color(0xFF795548)
    val bookSpine1 = Color(0xFF10B981)
    val bookSpine2 = Color(0xFF8B5CF6)
    val bookSpine3 = Color(0xFFF59E0B)
    val teaMug = Color(0xFFE0E7FF)
    val potionGlow = Color(0xFF34D399)
    val candleGlow = Color(0xFFFDE68A)
}

/**
 * High-definition Retro Pixel Art renderer for Ana (brunette with black hair & white eyebrows)
 * and Sabrina (blonde).
 */
@Composable
fun PixelWitchAvatar(
    persona: CovenPersona,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    isStudying: Boolean = false,
    showHat: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pixel_witch_anim")
    
    // Idle bobbing animation
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    // Periodic blinking animation (stays open 90% of the time, blinks briefly)
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3600
                0.0f at 0
                0.0f at 3300
                1.0f at 3450
                0.0f at 3600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    val isBlinking = blinkProgress > 0.5f

    Box(
        modifier = modifier
            .size(size)
            .testTag("pixel_witch_avatar_${persona.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            
            // Define 16x16 grid cell dimension
            val gridSize = 16f
            val px = canvasW / gridSize
            val py = canvasH / gridSize
            val bobY = bobOffset * (py * 0.4f)

            fun drawPixel(gx: Int, gy: Int, color: Color) {
                drawRect(
                    color = color,
                    topLeft = Offset(gx * px, (gy * py) + bobY),
                    size = Size(px + 0.5f, py + 0.5f)
                )
            }

            fun drawPixelRect(gx: Int, gy: Int, w: Int, h: Int, color: Color) {
                drawRect(
                    color = color,
                    topLeft = Offset(gx * px, (gy * py) + bobY),
                    size = Size(w * px + 0.5f, h * py + 0.5f)
                )
            }

            when (persona) {
                CovenPersona.ANA -> {
                    // ANA: Brunette with Black Hair & Iconic White Eyebrows (Potions Witch)
                    
                    // 1. Hat (if enabled)
                    if (showHat) {
                        // Hat tip
                        drawPixelRect(7, 0, 2, 1, PixelColors.anaHatBase)
                        drawPixelRect(6, 1, 4, 1, PixelColors.anaHatBase)
                        drawPixelRect(5, 2, 6, 2, PixelColors.anaHatBase)
                        drawPixelRect(5, 3, 6, 1, PixelColors.anaHatTrim)
                        // Golden buckle
                        drawPixelRect(7, 3, 2, 1, PixelColors.anaHatBuckle)
                        // Brim
                        drawPixelRect(2, 4, 12, 1, PixelColors.anaHatBase)
                    }

                    // 2. Brunette with Black Hair (Twin buns & stylish bangs)
                    drawPixelRect(4, 3, 2, 2, PixelColors.anaHairLight) // Left bun
                    drawPixelRect(10, 3, 2, 2, PixelColors.anaHairLight) // Right bun
                    drawPixelRect(3, 4, 2, 2, PixelColors.anaHairBase)
                    drawPixelRect(11, 4, 2, 2, PixelColors.anaHairBase)
                    
                    // Fringe / bangs in jet-black
                    drawPixelRect(4, 5, 8, 2, PixelColors.anaHairBase)
                    drawPixelRect(3, 6, 2, 4, PixelColors.anaHairShadow)
                    drawPixelRect(11, 6, 2, 4, PixelColors.anaHairShadow)

                    // 3. Face & Skin
                    drawPixelRect(5, 7, 6, 5, PixelColors.skinTone)
                    drawPixelRect(5, 11, 6, 1, PixelColors.skinShadow) // Neck/chin shadow

                    // 4. Iconic White Eyebrows! (Distinct, bold & magical)
                    drawPixelRect(5, 6, 2, 1, PixelColors.anaEyebrowWhite)
                    drawPixelRect(9, 6, 2, 1, PixelColors.anaEyebrowWhite)

                    // 5. Eyes & Blush
                    if (isBlinking || isStudying) {
                        // Closed / happy studying eye lines (^_^)
                        drawPixelRect(5, 8, 2, 1, PixelColors.eyePupil)
                        drawPixelRect(9, 8, 2, 1, PixelColors.eyePupil)
                    } else {
                        // Big sparkle anime eyes
                        drawPixelRect(5, 7, 2, 2, PixelColors.anaEye)
                        drawPixelRect(5, 7, 1, 1, PixelColors.eyeWhite)
                        drawPixelRect(9, 7, 2, 2, PixelColors.anaEye)
                        drawPixelRect(9, 7, 1, 1, PixelColors.eyeWhite)
                    }
                    // Cute cheeks
                    drawPixelRect(4, 9, 2, 1, PixelColors.blush)
                    drawPixelRect(10, 9, 2, 1, PixelColors.blush)
                    // Smile
                    drawPixel(7, 10, PixelColors.skinShadow)
                    drawPixel(8, 10, PixelColors.skinShadow)

                    // 6. Robe & Shoulders
                    drawPixelRect(4, 12, 8, 4, PixelColors.anaRobe)
                    drawPixelRect(6, 12, 4, 3, PixelColors.anaRobeAccent) // Collar
                    drawPixelRect(3, 14, 2, 2, PixelColors.anaRobe) // Arms
                    drawPixelRect(11, 14, 2, 2, PixelColors.anaRobe)

                    // Magic Herb / Potion in hand
                    drawPixelRect(12, 12, 2, 3, PixelColors.potionGlow)
                }

                CovenPersona.SABRINA -> {
                    // SABRINA: Blonde Astral Witch (Golden Locks, Purple Robes & Moon Pin)

                    // 1. Purple Hat with Golden Crescent Moon
                    if (showHat) {
                        drawPixelRect(7, 0, 2, 1, PixelColors.sabrinaHatBase)
                        drawPixelRect(6, 1, 4, 1, PixelColors.sabrinaHatBase)
                        drawPixelRect(5, 2, 6, 2, PixelColors.sabrinaHatBase)
                        drawPixelRect(5, 3, 6, 1, PixelColors.sabrinaHatTrim)
                        // Golden Crescent Moon Pin
                        drawPixel(6, 2, PixelColors.sabrinaHatMoon)
                        drawPixel(7, 2, PixelColors.sabrinaHatMoon)
                        drawPixel(7, 3, PixelColors.sabrinaHatMoon)
                        // Brim
                        drawPixelRect(2, 4, 12, 1, PixelColors.sabrinaHatBase)
                    }

                    // 2. Blonde Hair (Rich golden blonde flowing locks)
                    drawPixelRect(3, 3, 3, 3, PixelColors.sabrinaHairLight)
                    drawPixelRect(10, 3, 3, 3, PixelColors.sabrinaHairLight)
                    drawPixelRect(4, 5, 8, 2, PixelColors.sabrinaHairBase)
                    drawPixelRect(3, 6, 2, 6, PixelColors.sabrinaHairBase)
                    drawPixelRect(11, 6, 2, 6, PixelColors.sabrinaHairBase)
                    drawPixelRect(2, 8, 2, 4, PixelColors.sabrinaHairShadow)
                    drawPixelRect(12, 8, 2, 4, PixelColors.sabrinaHairShadow)

                    // 3. Face & Skin
                    drawPixelRect(5, 7, 6, 5, PixelColors.skinTone)
                    drawPixelRect(5, 11, 6, 1, PixelColors.skinShadow)

                    // 4. Eyes (Astral Violet) & Blush
                    if (isBlinking || isStudying) {
                        drawPixelRect(5, 8, 2, 1, PixelColors.eyePupil)
                        drawPixelRect(9, 8, 2, 1, PixelColors.eyePupil)
                    } else {
                        drawPixelRect(5, 7, 2, 2, PixelColors.sabrinaEye)
                        drawPixelRect(5, 7, 1, 1, PixelColors.eyeWhite)
                        drawPixelRect(9, 7, 2, 2, PixelColors.sabrinaEye)
                        drawPixelRect(9, 7, 1, 1, PixelColors.eyeWhite)
                    }
                    drawPixelRect(4, 9, 2, 1, PixelColors.blush)
                    drawPixelRect(10, 9, 2, 1, PixelColors.blush)
                    // Gentle calm smile
                    drawPixel(7, 10, PixelColors.skinShadow)
                    drawPixel(8, 10, PixelColors.skinShadow)

                    // 5. Robe & Starry Brooch
                    drawPixelRect(4, 12, 8, 4, PixelColors.sabrinaRobe)
                    drawPixelRect(6, 12, 4, 3, PixelColors.sabrinaRobeAccent)
                    drawPixel(7, 13, PixelColors.sabrinaHatMoon) // Star brooch
                    drawPixelRect(3, 14, 2, 2, PixelColors.sabrinaRobe)
                    drawPixelRect(11, 14, 2, 2, PixelColors.sabrinaRobe)

                    // Floating Astral Quill / Rune
                    drawPixelRect(2, 13, 2, 2, PixelColors.sabrinaEye)
                }

                CovenPersona.DUO -> {
                    // Coven Duo: Ana (brunette/black hair, white eyebrows) & Sabrina (blonde)!
                    // Left half: Ana
                    drawPixelRect(2, 2, 5, 2, PixelColors.anaHatBase)
                    drawPixelRect(1, 4, 6, 1, PixelColors.anaHatBase)
                    drawPixelRect(2, 4, 4, 3, PixelColors.anaHairBase)
                    drawPixelRect(3, 6, 4, 4, PixelColors.skinTone)
                    // White eyebrows for Ana
                    drawPixel(3, 6, PixelColors.anaEyebrowWhite)
                    drawPixel(4, 6, PixelColors.anaEyebrowWhite)
                    if (isBlinking) {
                        drawPixel(4, 7, PixelColors.eyePupil)
                        drawPixel(5, 7, PixelColors.eyePupil)
                    } else {
                        drawPixel(4, 7, PixelColors.anaEye)
                        drawPixel(5, 7, PixelColors.anaEye)
                    }
                    drawPixel(3, 8, PixelColors.blush)
                    drawPixel(6, 8, PixelColors.blush)
                    drawPixelRect(2, 10, 5, 5, PixelColors.anaRobe)

                    // Right half: Sabrina (Blonde)
                    drawPixelRect(9, 2, 5, 2, PixelColors.sabrinaHatBase)
                    drawPixel(10, 3, PixelColors.sabrinaHatMoon)
                    drawPixelRect(8, 4, 6, 1, PixelColors.sabrinaHatBase)
                    drawPixelRect(9, 4, 4, 3, PixelColors.sabrinaHairBase)
                    drawPixelRect(9, 6, 4, 4, PixelColors.skinTone)
                    if (isBlinking) {
                        drawPixel(10, 7, PixelColors.eyePupil)
                        drawPixel(11, 7, PixelColors.eyePupil)
                    } else {
                        drawPixel(10, 7, PixelColors.sabrinaEye)
                        drawPixel(11, 7, PixelColors.sabrinaEye)
                    }
                    drawPixel(9, 8, PixelColors.blush)
                    drawPixel(12, 8, PixelColors.blush)
                    drawPixelRect(9, 10, 5, 5, PixelColors.sabrinaRobe)

                    // Heart sparkle in between them
                    drawPixel(7, 8, Color(0xFFFF6584))
                    drawPixel(8, 8, Color(0xFFFF6584))
                    drawPixel(7, 9, Color(0xFFFF6584))
                }
            }
        }
    }
}

/**
 * Interactive Coven Besties Study Scene Card:
 * Features BOTH Ana (brunette with black hair & white eyebrows) and Sabrina (blonde)
 * sitting together at their cozy pixel art study desk!
 */
@Composable
fun PixelBestiesStudyDeskCard(
    modifier: Modifier = Modifier,
    activePersona: CovenPersona = CovenPersona.DUO,
    onSelectPersona: ((CovenPersona) -> Unit)? = null,
    isFocusMode: Boolean = false
) {
    val colors = LocalCovenColors.current

    val bestieQuotes = remember {
        listOf(
            "Ana: Sabrina and I brewed chamomile potion for this sprint! ✨",
            "Sabrina: The astral constellations predict 100% on our exams! 🌙",
            "Ana: Don't forget to stretch your shoulders, coven sister! 🌿",
            "Sabrina: We've got 5 spells reviewed. You're doing amazing! 🔮",
            "Coven Besties: Studying together doubles the magical power! 💖"
        )
    }

    var quoteIndex by remember { mutableIntStateOf(0) }
    val currentQuote = bestieQuotes[quoteIndex % bestieQuotes.size]

    PixelCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pixel_besties_study_desk_card"),
        backgroundColor = colors.surfaceCard,
        borderColor = colors.primaryAccent.copy(alpha = 0.5f),
        borderWidth = 1.5.dp,
        contentPadding = 12.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "COVEN BESTIES STUDY SANCTUM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.primaryAccent,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.8.sp
                    )
                }

                // Interactive Poke Button
                Surface(
                    onClick = { quoteIndex = (quoteIndex + 1) % bestieQuotes.size },
                    shape = RoundedCornerShape(6.dp),
                    color = colors.surfaceCardElevated,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💬 Talk to Girls", fontSize = 10.sp, color = colors.secondaryAccent, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The Cute Pixel Art Duo Scene
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { quoteIndex = (quoteIndex + 1) % bestieQuotes.size },
                color = colors.surfaceCardElevated,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background magical stars & bookshelf backdrop
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Left Character: ANA (Brunette with Black Hair & White Brows)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                onSelectPersona?.invoke(CovenPersona.ANA)
                                quoteIndex = 0
                            }
                        ) {
                            Text(
                                text = "ANA",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(PixelColors.anaHatBuckle.value),
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            PixelWitchAvatar(
                                persona = CovenPersona.ANA,
                                size = 68.dp,
                                isStudying = isFocusMode
                            )
                        }

                        // Center: Cozy Study Desk with Steaming Mugs & Cauldron
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            // Glowing Cauldron / Enchanted Candle
                            Text(if (isFocusMode) "🔥 🧪 ✨" else "☕ 🕯️ 🍵", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            // Desk Books Stack
                            Text("📚 📖 📜", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                modifier = Modifier
                                    .width(88.dp)
                                    .height(6.dp),
                                color = PixelColors.deskWoodLight,
                                shape = RoundedCornerShape(2.dp)
                            ) {}
                        }

                        // Right Character: SABRINA (Astral Witch)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                onSelectPersona?.invoke(CovenPersona.SABRINA)
                                quoteIndex = 1
                            }
                        ) {
                            Text(
                                text = "SABRINA",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(PixelColors.sabrinaEye.value),
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            PixelWitchAvatar(
                                persona = CovenPersona.SABRINA,
                                size = 68.dp,
                                isStudying = isFocusMode
                            )
                        }
                    }

                    // Floating Heart or Study Sparkle
                    Text(
                        text = if (isFocusMode) "⚡ FOCUSING ⚡" else "💖 BESTIES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryAccent,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 6.dp)
                            .background(colors.background.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                            .border(1.dp, colors.border, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Speech Bubble with Dialogue from the girls
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = colors.surfaceCardElevated,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔮", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentQuote,
                        fontSize = 11.sp,
                        color = colors.textPrimary,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
