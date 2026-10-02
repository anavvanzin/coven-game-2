package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.components.*
import com.example.ui.theme.LocalCovenColors
import com.example.viewmodel.CovenViewModel

@Composable
fun TasksAndCalendarScreen(
    viewModel: CovenViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalCovenColors.current
    val tasks by viewModel.tasks.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()
    val level by viewModel.covenLevel.collectAsState()
    val xp by viewModel.totalXp.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredTasks = remember(tasks, selectedFilter) {
        tasks.filter { task ->
            when (selectedFilter) {
                "Study" -> task.category == "Study"
                "Spellcraft" -> task.category == "Spellcraft"
                "Feast" -> task.category == "Hobbit Feast"
                else -> true
            }
        }
    }

    Scaffold(
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = colors.primaryAccent,
                contentColor = colors.background,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Quest",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                PersonaSelectorHeader(
                    activePersona = activePersona,
                    onSelectPersona = { viewModel.setPersona(it) },
                    level = level,
                    xp = xp
                )
            }

            // Pixel Art Study Buddy Avatar Toggle Component (Ana & Sabrina)
            item {
                StudyBuddySelectorComponent(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Pixel Art Study Companions Desk (Ana & Sabrina)
            item {
                PixelBestiesStudyDeskCard(
                    activePersona = activePersona,
                    onSelectPersona = { viewModel.setPersona(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Daily Witchy Affirmation Component (Randomized on Load)
            item {
                WitchyAffirmationCard(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Ambient Soundscapes Focus Audio Card (Rainy Forest, Fireplace, Ancient Library)
            item {
                AmbientSoundscapesCard(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Study", "Spellcraft", "Feast").forEach { filter ->
                        PixelChip(
                            label = filter,
                            isSelected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Quests List
            items(filteredTasks, key = { it.id }) { task ->
                PixelCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_item_${task.id}"),
                    backgroundColor = colors.surfaceCard
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleTaskCompletion(task) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (task.isCompleted) "Completed" else "Incomplete",
                                tint = if (task.isCompleted) colors.secondaryAccent else colors.primaryAccent
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = colors.primaryAccent.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, colors.primaryAccent.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = task.category.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primaryAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+${task.xpReward} XP",
                                    fontSize = 10.sp,
                                    color = colors.secondaryAccent,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = task.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (task.isCompleted) colors.textMuted else colors.textPrimary,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                fontFamily = FontFamily.Monospace
                            )

                            if (task.studyNotes.isNotBlank()) {
                                Text(
                                    text = task.studyNotes,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteTask(task) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Quest",
                                tint = colors.textMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Study") }
        var xpReward by remember { mutableStateOf(50) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "🔮 Summon New Quest",
                    color = colors.primaryAccent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Quest Title / Exam Topic") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Study", "Spellcraft", "Hobbit Feast").forEach { cat ->
                            PixelChip(
                                label = cat,
                                isSelected = category == cat,
                                onClick = { category = cat },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addTask(
                                TaskEntity(
                                    title = title.trim(),
                                    category = category,
                                    xpReward = xpReward
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent, contentColor = colors.background)
                ) {
                    Text("Summon Quest", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surfaceCardElevated
        )
    }
}
