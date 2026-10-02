package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CovenDatabase
import com.example.data.model.*
import com.example.data.repository.CovenRepository
import com.example.ui.theme.CovenThemePalette
import com.example.util.CovenDownloadManager
import com.example.util.DownloadResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DownloadRecord(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val filename: String,
    val typeEmoji: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class CovenNavTab(val label: String, val iconEmoji: String) {
    QUESTS("Quests", "📜"),
    SPELLBOOK("Spellbook", "📖"),
    POTIONS("Brew Lab", "🧪"),
    FOCUS("Focus Sprint", "⏳")
}

class CovenViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CovenRepository

    init {
        val db = CovenDatabase.getInstance(application)
        repository = CovenRepository(db.covenDao())
    }

    // Theme Palette Switcher ('Mystic Moon' vs 'Deep Forest')
    private val _themePalette = MutableStateFlow(CovenThemePalette.MYSTIC_MOON)
    val themePalette: StateFlow<CovenThemePalette> = _themePalette.asStateFlow()

    fun toggleThemePalette() {
        _themePalette.value = if (_themePalette.value == CovenThemePalette.MYSTIC_MOON) {
            CovenThemePalette.DEEP_FOREST
        } else {
            CovenThemePalette.MYSTIC_MOON
        }
    }

    // Active Persona & Tab
    private val _activePersona = MutableStateFlow(CovenPersona.DUO)
    val activePersona: StateFlow<CovenPersona> = _activePersona.asStateFlow()

    fun setPersona(persona: CovenPersona) {
        _activePersona.value = persona
    }

    private val _currentTab = MutableStateFlow(CovenNavTab.QUESTS)
    val currentTab: StateFlow<CovenNavTab> = _currentTab.asStateFlow()

    fun setTab(tab: CovenNavTab) {
        _currentTab.value = tab
    }

    // XP & Level System
    private val _totalXp = MutableStateFlow(1240)
    val totalXp: StateFlow<Int> = _totalXp.asStateFlow()

    val covenLevel: StateFlow<Int> = _totalXp.map { xp -> (xp / 300) + 1 }.stateIn(
        viewModelScope, SharingStarted.Eagerly, 5
    )

    fun addXp(amount: Int) {
        _totalXp.value += amount
    }

    // Room Database Streams
    val tasks: StateFlow<List<TaskEntity>> = repository.tasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val notes: StateFlow<List<NoteDocEntity>> = repository.notes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.addTask(task)
            addXp(20)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            repository.updateTask(updated)
            if (updated.isCompleted) {
                addXp(task.xpReward)
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun addNote(note: NoteDocEntity) {
        viewModelScope.launch {
            repository.addNote(note)
            addXp(25)
        }
    }

    fun deleteNote(note: NoteDocEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Daily Witchy Affirmation (Randomized on App Load)
    private val _dailyAffirmation = MutableStateFlow<WitchyAffirmation>(WitchyAffirmationBank.getRandomAffirmation())
    val dailyAffirmation: StateFlow<WitchyAffirmation> = _dailyAffirmation.asStateFlow()

    fun drawNewAffirmation() {
        val currentId = _dailyAffirmation.value.id
        val available = WitchyAffirmationBank.allAffirmations.filter { it.id != currentId }
        val next = if (available.isNotEmpty()) available.random() else WitchyAffirmationBank.getRandomAffirmation()
        _dailyAffirmation.value = next
    }

    fun saveAffirmationToSpellbook(affirmation: WitchyAffirmation) {
        viewModelScope.launch {
            repository.addNote(
                NoteDocEntity(
                    title = "✨ Affirmation: ${affirmation.themeTitle}",
                    category = "Inspirations & Spells",
                    content = "\"${affirmation.quote}\"\n\n— ${affirmation.authorOrOrigin}\n\n🔮 Study Lore Tip: ${affirmation.loreTip}",
                    author = _activePersona.value.displayName,
                    isPinned = true,
                    collaboratorTag = "Drawn from Daily Oracle"
                )
            )
            addXp(15)
        }
    }

    // Download & Archive Vault
    private val _downloadHistory = MutableStateFlow<List<DownloadRecord>>(emptyList())
    val downloadHistory: StateFlow<List<DownloadRecord>> = _downloadHistory.asStateFlow()

    private val _downloadNotification = MutableStateFlow<String?>(null)
    val downloadNotification: StateFlow<String?> = _downloadNotification.asStateFlow()

    fun clearDownloadNotification() {
        _downloadNotification.value = null
    }

    private fun logDownload(title: String, filename: String, emoji: String) {
        val record = DownloadRecord(title = title, filename = filename, typeEmoji = emoji)
        _downloadHistory.value = listOf(record) + _downloadHistory.value.take(9)
        _downloadNotification.value = "Downloaded '$filename' to Downloads/StudyCoven"
    }

    fun downloadSingleNote(context: Context, note: NoteDocEntity) {
        val cleanTitle = note.title.replace(Regex("[^a-zA-Z0-9_]"), "_").take(24)
        val filename = "Grimoire_${cleanTitle}.md"
        val content = CovenDownloadManager.formatNoteAsMarkdown(note)
        val res = CovenDownloadManager.downloadTextFile(context, filename, content, "text/markdown")
        if (res.isSuccess) {
            logDownload(note.title, filename, "📖")
            addXp(15)
        } else {
            _downloadNotification.value = res.message
        }
    }

    fun downloadAllNotes(context: Context) {
        val currentNotes = notes.value
        val filename = "StudyCoven_AllSpells_Archive.md"
        val content = CovenDownloadManager.formatAllNotesAsMarkdown(currentNotes)
        val res = CovenDownloadManager.downloadTextFile(context, filename, content, "text/markdown")
        if (res.isSuccess) {
            logDownload("All Grimoire Spells (${currentNotes.size})", filename, "📚")
            addXp(30)
        } else {
            _downloadNotification.value = res.message
        }
    }

    fun downloadQuests(context: Context) {
        val currentTasks = tasks.value
        val filename = "StudyCoven_QuestsLog.txt"
        val content = CovenDownloadManager.formatTasksAsText(currentTasks, covenLevel.value, totalXp.value)
        val res = CovenDownloadManager.downloadTextFile(context, filename, content, "text/plain")
        if (res.isSuccess) {
            logDownload("Quests & Syllabi Log (${currentTasks.size})", filename, "📜")
            addXp(20)
        } else {
            _downloadNotification.value = res.message
        }
    }

    fun downloadStudySummary(context: Context) {
        val filename = "StudyCoven_StudyReport.md"
        val content = CovenDownloadManager.formatStudySummaryMarkdown(
            tasks = tasks.value,
            notes = notes.value,
            xp = totalXp.value,
            level = covenLevel.value,
            activeBuff = activeStudyBuff.value,
            persona = activePersona.value
        )
        val res = CovenDownloadManager.downloadTextFile(context, filename, content, "text/markdown")
        if (res.isSuccess) {
            logDownload("Comprehensive Study Report", filename, "🔮")
            addXp(25)
        } else {
            _downloadNotification.value = res.message
        }
    }

    fun downloadFullBackup(context: Context) {
        val filename = "StudyCoven_Backup_${System.currentTimeMillis()}.json"
        val json = CovenDownloadManager.createBackupJson(
            tasks = tasks.value,
            notes = notes.value,
            xp = totalXp.value,
            level = covenLevel.value,
            persona = activePersona.value
        )
        val res = CovenDownloadManager.downloadTextFile(context, filename, json, "application/json")
        if (res.isSuccess) {
            logDownload("Full Coven Offline Backup", filename, "📦")
            addXp(40)
        } else {
            _downloadNotification.value = res.message
        }
    }

    fun restoreFromBackup(jsonString: String, onResult: (Boolean, String) -> Unit) {
        val parsedResult = CovenDownloadManager.parseBackupJson(jsonString)
        parsedResult.fold(
            onSuccess = { backup ->
                viewModelScope.launch {
                    try {
                        if (backup.tasks.isNotEmpty()) {
                            repository.addTasks(backup.tasks)
                        }
                        if (backup.notes.isNotEmpty()) {
                            repository.addNotes(backup.notes)
                        }
                        if (backup.xp > 0) {
                            addXp(backup.xp)
                        }
                        onResult(true, "Restored ${backup.tasks.size} quests & ${backup.notes.size} spells!")
                    } catch (e: Exception) {
                        onResult(false, "Restore failed: ${e.localizedMessage}")
                    }
                }
            },
            onFailure = { err ->
                onResult(false, "Invalid backup: ${err.localizedMessage}")
            }
        )
    }

    fun downloadAffirmation(context: Context, affirmation: WitchyAffirmation) {
        val filename = "StudyCoven_Affirmation_${affirmation.id}.txt"
        val content = buildString {
            appendLine("✨ DAILY WITCHY ORACLE AFFIRMATION ✨")
            appendLine("Theme: ${affirmation.themeTitle}")
            appendLine("\"${affirmation.quote}\"")
            appendLine("— ${affirmation.authorOrOrigin}")
            appendLine()
            appendLine("Study Lore Tip: ${affirmation.loreTip}")
            appendLine("Drawn by: ${activePersona.value.displayName}")
            appendLine("Bestie Study Sanctum 🌙")
        }
        val res = CovenDownloadManager.downloadTextFile(context, filename, content, "text/plain")
        if (res.isSuccess) {
            logDownload("Oracle: ${affirmation.themeTitle}", filename, "✨")
            addXp(15)
        } else {
            _downloadNotification.value = res.message
        }
    }

    // Potion Brewing Mini-Game (Drag and Drop ingredients into Cauldron to get Random Buff)
    private val _cauldronIngredients = MutableStateFlow<List<PotionIngredient>>(emptyList())
    val cauldronIngredients: StateFlow<List<PotionIngredient>> = _cauldronIngredients.asStateFlow()

    private val _brewingState = MutableStateFlow<BrewingState>(BrewingState.Idle)
    val brewingState: StateFlow<BrewingState> = _brewingState.asStateFlow()

    private val _activeStudyBuff = MutableStateFlow<ActiveStudyBuff?>(null)
    val activeStudyBuff: StateFlow<ActiveStudyBuff?> = _activeStudyBuff.asStateFlow()

    private val _brewedPotionsCount = MutableStateFlow(0)
    val brewedPotionsCount: StateFlow<Int> = _brewedPotionsCount.asStateFlow()

    fun addIngredientToCauldron(ingredient: PotionIngredient): Boolean {
        if (_cauldronIngredients.value.size >= 3) return false
        _cauldronIngredients.value = _cauldronIngredients.value + ingredient
        return true
    }

    fun removeIngredientFromCauldron(index: Int) {
        val current = _cauldronIngredients.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _cauldronIngredients.value = current
        }
    }

    fun clearCauldron() {
        _cauldronIngredients.value = emptyList()
        _brewingState.value = BrewingState.Idle
    }

    fun brewCurrentPotion() {
        val current = _cauldronIngredients.value
        if (current.size < 3) {
            _brewingState.value = BrewingState.Error("Drop 3 ingredients into the bubbling cauldron to brew!")
            return
        }

        viewModelScope.launch {
            _brewingState.value = BrewingState.Brewing
            delay(1600) // Bubble & stir animation delay
            val randomBuff = PotionGrimoire.RANDOM_BUFFS.random()
            _activeStudyBuff.value = randomBuff
            _brewedPotionsCount.value += 1
            _cauldronIngredients.value = emptyList()
            _brewingState.value = BrewingState.Success(randomBuff)
            addXp(60)
        }
    }

    // Pomodoro Focus Sprint Timer
    private val _pomodoroSecondsRemaining = MutableStateFlow(25 * 60)
    val pomodoroSecondsRemaining: StateFlow<Int> = _pomodoroSecondsRemaining.asStateFlow()

    private val _isPomodoroRunning = MutableStateFlow(false)
    val isPomodoroRunning: StateFlow<Boolean> = _isPomodoroRunning.asStateFlow()

    private val _pomodoroMode = MutableStateFlow("25m Study Focus")
    val pomodoroMode: StateFlow<String> = _pomodoroMode.asStateFlow()

    private var timerJob: Job? = null

    fun togglePomodoro() {
        val currentlyRunning = _isPomodoroRunning.value
        if (currentlyRunning) {
            _isPomodoroRunning.value = false
            timerJob?.cancel()
        } else {
            _isPomodoroRunning.value = true
            timerJob = viewModelScope.launch {
                while (_pomodoroSecondsRemaining.value > 0 && _isPomodoroRunning.value) {
                    delay(1000)
                    _pomodoroSecondsRemaining.value -= 1
                }
                if (_pomodoroSecondsRemaining.value == 0) {
                    _isPomodoroRunning.value = false
                    addXp(100)
                }
            }
        }
    }

    fun resetPomodoro(minutes: Int, label: String) {
        timerJob?.cancel()
        _isPomodoroRunning.value = false
        _pomodoroSecondsRemaining.value = minutes * 60
        _pomodoroMode.value = label
    }

    // Ambient Soundscapes
    private val soundscapeEngine = com.example.service.AmbientSoundscapeEngine()
    private val _soundscapeState = MutableStateFlow(SoundscapePlaybackState())
    val soundscapeState: StateFlow<SoundscapePlaybackState> = _soundscapeState.asStateFlow()

    private var soundscapeTimerJob: Job? = null

    fun selectSoundscape(type: SoundscapeType) {
        val wasPlaying = _soundscapeState.value.isPlaying
        _soundscapeState.value = _soundscapeState.value.copy(currentType = type)
        if (wasPlaying) {
            soundscapeEngine.setSoundscape(type)
        }
    }

    fun toggleSoundscape(type: SoundscapeType? = null) {
        val current = _soundscapeState.value
        val targetType = type ?: current.currentType
        val newIsPlaying = if (type != null && type != current.currentType) {
            true
        } else {
            !current.isPlaying
        }

        _soundscapeState.value = current.copy(
            currentType = targetType,
            isPlaying = newIsPlaying
        )

        if (newIsPlaying) {
            soundscapeEngine.start(targetType, current.volume)
            startSoundscapeTimerIfSet()
            addXp(10)
        } else {
            soundscapeEngine.stop()
            soundscapeTimerJob?.cancel()
        }
    }

    fun setSoundscapeVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _soundscapeState.value = _soundscapeState.value.copy(volume = clamped)
        soundscapeEngine.setVolume(clamped)
    }

    fun setSoundscapeTimer(minutes: Int) {
        _soundscapeState.value = _soundscapeState.value.copy(
            selectedTimerPreset = minutes,
            timerRemainingSeconds = minutes * 60
        )
        if (_soundscapeState.value.isPlaying && minutes > 0) {
            startSoundscapeTimerIfSet()
        } else if (minutes == 0) {
            soundscapeTimerJob?.cancel()
        }
    }

    private fun startSoundscapeTimerIfSet() {
        soundscapeTimerJob?.cancel()
        val minutes = _soundscapeState.value.selectedTimerPreset
        if (minutes > 0) {
            soundscapeTimerJob = viewModelScope.launch {
                var seconds = minutes * 60
                while (seconds > 0 && _soundscapeState.value.isPlaying) {
                    delay(1000)
                    seconds--
                    _soundscapeState.value = _soundscapeState.value.copy(timerRemainingSeconds = seconds)
                }
                if (seconds == 0 && _soundscapeState.value.isPlaying) {
                    toggleSoundscape()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundscapeEngine.stop()
        soundscapeTimerJob?.cancel()
    }
}
