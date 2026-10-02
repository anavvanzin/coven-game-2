package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.LocalCovenColors
import com.example.viewmodel.CovenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DownloadVaultDialog(
    viewModel: CovenViewModel,
    onDismiss: () -> Unit
) {
    val colors = LocalCovenColors.current
    val context = LocalContext.current
    val notes by viewModel.notes.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val downloadHistory by viewModel.downloadHistory.collectAsState()
    val scrollState = rememberScrollState()

    var showRestoreDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("download_vault_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = colors.background,
            border = BorderStroke(2.dp, colors.borderHighlight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📥", fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "COVEN DOWNLOAD VAULT",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryAccent,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Export notes, quests & offline backups to device",
                                fontSize = 10.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_download_vault_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Offline readiness banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceCardElevated,
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "100% OFFLINE DOWNLOAD READY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6EE7B7),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "All audio soundscapes, potions, and grimoire data reside locally on your phone without cloud requirements.",
                                fontSize = 9.sp,
                                color = colors.textMuted,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable List of Download Options
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option 1: Grimoire Spells Archive (.md)
                    DownloadActionCard(
                        title = "Grimoire Spellbook Notes",
                        description = "Export all ${notes.size} inscribed notes as formatted Markdown (.md) for Obsidian, Notion, or file storage.",
                        buttonText = "Download Spells (.md)",
                        emoji = "📖",
                        onClick = {
                            viewModel.downloadAllNotes(context)
                            Toast.makeText(context, "Saved Grimoire notes to Downloads!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Option 2: Quests & Syllabi (.txt)
                    DownloadActionCard(
                        title = "Quests & Syllabi Checklist",
                        description = "Export ${tasks.size} study to-dos, priorities, due dates, and XP rewards as a clean plain-text log.",
                        buttonText = "Download Quests (.txt)",
                        emoji = "📜",
                        onClick = {
                            viewModel.downloadQuests(context)
                            Toast.makeText(context, "Saved Quest log to Downloads!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Option 3: Comprehensive Study Report (.md)
                    DownloadActionCard(
                        title = "Study Progress & Lore Report",
                        description = "Full analytical summary of completed quests, active potion buffs, study levels, and grimoire metrics.",
                        buttonText = "Download Report (.md)",
                        emoji = "🔮",
                        onClick = {
                            viewModel.downloadStudySummary(context)
                            Toast.makeText(context, "Saved Study Report to Downloads!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Option 4: Full JSON Backup & Restore
                    DownloadActionCard(
                        title = "Full Coven Offline Backup (.json)",
                        description = "Complete portable snapshot of all quests, grimoire notes, XP, and settings. Can be transferred between devices.",
                        buttonText = "Download Backup (.json)",
                        emoji = "📦",
                        onClick = {
                            viewModel.downloadFullBackup(context)
                            Toast.makeText(context, "Backup downloaded to Downloads/StudyCoven!", Toast.LENGTH_SHORT).show()
                        },
                        secondaryButtonText = "Restore JSON",
                        onSecondaryClick = { showRestoreDialog = true }
                    )

                    // Download History Section
                    if (downloadHistory.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "RECENT DOWNLOADS THIS SESSION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryAccent,
                            fontFamily = FontFamily.Monospace
                        )

                        downloadHistory.take(5).forEach { record ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colors.surfaceCard,
                                border = BorderStroke(1.dp, colors.border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(record.typeEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = record.filename,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(record.timestamp)),
                                                fontSize = 9.sp,
                                                color = colors.textMuted
                                            )
                                        }
                                    }
                                    Text("✓ Saved", fontSize = 10.sp, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_vault_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.surfaceCardElevated,
                        contentColor = colors.primaryAccent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, colors.borderHighlight)
                ) {
                    Text(
                        text = "Close Vault",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Restore Backup Dialog
    if (showRestoreDialog) {
        var jsonInput by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = {
                Text(
                    text = "📥 Restore Coven Backup",
                    color = colors.primaryAccent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Paste the contents of a previously downloaded StudyCoven_Backup.json file below:",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )

                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = {
                            jsonInput = it
                            errorMessage = null
                        },
                        placeholder = { Text("{\n  \"tasks\": [...],\n  \"notes\": [...]\n}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 10.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (jsonInput.isBlank()) {
                            errorMessage = "Please paste JSON backup contents."
                            return@Button
                        }
                        viewModel.restoreFromBackup(jsonInput) { success, msg ->
                            if (success) {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                showRestoreDialog = false
                            } else {
                                errorMessage = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primaryAccent,
                        contentColor = colors.background
                    )
                ) {
                    Text("Restore", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surfaceCardElevated
        )
    }
}

@Composable
private fun DownloadActionCard(
    title: String,
    description: String,
    buttonText: String,
    emoji: String,
    onClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    val colors = LocalCovenColors.current

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = colors.surfaceCard,
        border = BorderStroke(1.dp, colors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryAccent,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = description,
                fontSize = 10.sp,
                color = colors.textSecondary,
                lineHeight = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primaryAccent,
                        contentColor = colors.background
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = buttonText,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (secondaryButtonText != null && onSecondaryClick != null) {
                    OutlinedButton(
                        onClick = onSecondaryClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colors.borderHighlight),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                    ) {
                        Text(
                            text = secondaryButtonText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
