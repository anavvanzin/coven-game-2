package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CovenPersona
import com.example.ui.theme.LocalCovenColors

@Composable
fun PixelCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalCovenColors.current.surfaceCard,
    borderColor: Color = LocalCovenColors.current.border,
    borderWidth: Dp = 1.dp,
    contentPadding: Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun PixelChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) colors.primaryAccent.copy(alpha = 0.2f) else colors.surfaceCardElevated,
        border = BorderStroke(1.dp, if (isSelected) colors.primaryAccent else colors.border),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) colors.primaryAccent else colors.textSecondary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    backgroundColor: Color = LocalCovenColors.current.primaryAccent,
    textColor: Color = LocalCovenColors.current.background
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Text(text = icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun PersonaSelectorHeader(
    activePersona: CovenPersona,
    onSelectPersona: (CovenPersona) -> Unit,
    level: Int,
    xp: Int,
    streakDays: Int = 14,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    PixelCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = colors.surfaceCard
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(activePersona.tagColorHex).copy(alpha = 0.25f))
                        .border(1.dp, Color(activePersona.tagColorHex), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    PixelWitchAvatar(
                        persona = activePersona,
                        size = 44.dp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = activePersona.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = activePersona.title,
                        fontSize = 10.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "LVL $level COVEN",
                    fontWeight = FontWeight.Black,
                    color = colors.primaryAccent,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "$xp XP • 🔥 $streakDays Days",
                    fontSize = 10.sp,
                    color = colors.textMuted,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
