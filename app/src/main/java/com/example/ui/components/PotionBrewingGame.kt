package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveStudyBuff
import com.example.data.model.BrewingState
import com.example.data.model.PotionGrimoire
import com.example.data.model.PotionIngredient
import com.example.ui.theme.LocalCovenColors
import com.example.viewmodel.CovenViewModel
import kotlin.math.roundToInt

@Composable
fun PotionBrewingGame(
    viewModel: CovenViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    val cauldronIngredients by viewModel.cauldronIngredients.collectAsState()
    val brewingState by viewModel.brewingState.collectAsState()
    val activeBuff by viewModel.activeStudyBuff.collectAsState()
    val potionsBrewed by viewModel.brewedPotionsCount.collectAsState()

    var cauldronBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var draggingIngredient by remember { mutableStateOf<PotionIngredient?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("potion_brewing_mini_game"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Active Study Buff Banner
        activeBuff?.let { buff ->
            PixelCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_study_buff_card"),
                backgroundColor = colors.surfaceCardElevated,
                borderColor = colors.secondaryAccent,
                borderWidth = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = buff.emoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ACTIVE DAILY BUFF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.secondaryAccent,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+${buff.xpBonusPercent}% XP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryAccent,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = buff.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = buff.description,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // Cauldron Vessel Area
        PixelCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = colors.surfaceCard,
            borderColor = colors.borderHighlight
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧪 ALCHEMY CAULDRON",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.primaryAccent,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Brewed: $potionsBrewed",
                        fontSize = 10.sp,
                        color = colors.textMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The Bubbling Cauldron Target Box
                val infiniteTransition = rememberInfiniteTransition(label = "cauldron_bubble")
                val bubbleScale by infiniteTransition.animateFloat(
                    initialValue = 0.96f,
                    targetValue = 1.04f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bubble_scale"
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(if (brewingState is BrewingState.Brewing) bubbleScale else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    colors.secondaryAccent.copy(alpha = 0.4f),
                                    colors.background
                                )
                            )
                        )
                        .border(3.dp, colors.primaryAccent, CircleShape)
                        .onGloballyPositioned { coordinates ->
                            cauldronBounds = coordinates.boundsInRoot()
                        }
                        .testTag("cauldron_vessel_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (brewingState is BrewingState.Brewing) "✨🫧✨" else "⚗️",
                            fontSize = 36.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${cauldronIngredients.size} / 3",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryAccent,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (cauldronIngredients.isEmpty()) "Drag / Tap Here" else "Ready to stir",
                            fontSize = 9.sp,
                            color = colors.textMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ingredients Inside Cauldron Slots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 3) {
                        val ingredient = cauldronIngredients.getOrNull(i)
                        Surface(
                            onClick = {
                                if (ingredient != null) {
                                    viewModel.removeIngredientFromCauldron(i)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (ingredient != null) colors.surfaceCardElevated else colors.background,
                            border = BorderStroke(1.dp, if (ingredient != null) colors.primaryAccent else colors.border),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (ingredient != null) {
                                    Text(text = ingredient.emoji, fontSize = 22.sp)
                                } else {
                                    Text(text = "${i + 1}", fontSize = 12.sp, color = colors.textMuted, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Brew & Clear Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PixelButton(
                        text = if (brewingState is BrewingState.Brewing) "Brewing Potion..." else "Stir & Brew Buff",
                        onClick = { viewModel.brewCurrentPotion() },
                        icon = "🔥",
                        backgroundColor = if (cauldronIngredients.size == 3) colors.primaryAccent else colors.surfaceCardElevated,
                        textColor = if (cauldronIngredients.size == 3) colors.background else colors.textMuted,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("brew_potion_button")
                    )

                    if (cauldronIngredients.isNotEmpty()) {
                        PixelButton(
                            text = "Dump",
                            onClick = { viewModel.clearCauldron() },
                            icon = "🗑️",
                            backgroundColor = colors.surfaceCardElevated,
                            textColor = colors.textSecondary,
                            modifier = Modifier.testTag("clear_cauldron_button")
                        )
                    }
                }

                // Status / Error message
                if (brewingState is BrewingState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (brewingState as BrewingState.Error).message,
                        color = colors.primaryAccent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Pantry Cabinet (Pixelated Ingredients Grid)
        Text(
            text = "🌿 PANTRY INGREDIENTS (DRAG OR TAP TO ADD)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryAccent,
            fontFamily = FontFamily.Monospace
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(PotionGrimoire.ALL_INGREDIENTS) { ingredient ->
                Surface(
                    onClick = {
                        viewModel.addIngredientToCauldron(ingredient)
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = colors.surfaceCard,
                    border = BorderStroke(1.dp, Color(ingredient.rarityColorHex)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("ingredient_card_${ingredient.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = ingredient.emoji, fontSize = 26.sp)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = ingredient.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                        Text(
                            text = "+Drop",
                            fontSize = 9.sp,
                            color = Color(ingredient.rarityColorHex),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
