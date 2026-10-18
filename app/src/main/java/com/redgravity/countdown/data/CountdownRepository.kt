package com.redgravity.countdown.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.redgravity.countdown.model.CountdownCardStyle
import com.redgravity.countdown.model.CountdownCategory
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.model.CountdownVisualTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.Calendar

class CountdownRepository(private val context: Context) {

    private val gson = Gson()
    private val file = File(context.filesDir, "countdowns_v2.json")
    private val mutex = Mutex()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _countdowns = MutableStateFlow<List<CountdownItem>>(emptyList())
    val countdowns: StateFlow<List<CountdownItem>> = _countdowns.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        if (!file.exists()) {
            val initialList = migrateOrSeed()
            _countdowns.value = initialList
            persistToFile(initialList)
        } else {
            try {
                val json = file.readText()
                val type = object : TypeToken<List<CountdownItem>>() {}.type
                val items: List<CountdownItem> = gson.fromJson(json, type) ?: emptyList()
                if (items.isEmpty()) {
                    val seeded = seedSamples()
                    _countdowns.value = seeded
                    persistToFile(seeded)
                } else {
                    _countdowns.value = items.sortedWith(
                        compareByDescending<CountdownItem> { it.isPinned }
                            .thenBy { it.targetEpochMillis }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val fallback = seedSamples()
                _countdowns.value = fallback
                persistToFile(fallback)
            }
        }
    }

    private fun migrateOrSeed(): List<CountdownItem> {
        val legacyPrefs = context.getSharedPreferences("countdown_prefs", Context.MODE_PRIVATE)
        val legacyTitle = legacyPrefs.getString("title", null)
        val legacyTarget = legacyPrefs.getLong("target", -1L)
        val legacyDesc = legacyPrefs.getString("description", "") ?: ""

        val list = mutableListOf<CountdownItem>()

        if (!legacyTitle.isNullOrBlank() && legacyTarget > 0L) {
            list.add(
                CountdownItem(
                    title = legacyTitle,
                    description = legacyDesc,
                    targetEpochMillis = legacyTarget,
                    createdEpochMillis = System.currentTimeMillis() - 7 * 86_400_000L,
                    category = CountdownCategory.MILESTONE,
                    themeId = CountdownVisualTheme.OBSIDIAN_GOLD.id,
                    cardStyle = CountdownCardStyle.ARCHITECTURAL,
                    isPinned = true
                )
            )
        }

        // Add curated artistic sample milestones
        list.addAll(seedSamples())
        return list.distinctBy { it.id }
    }

    private fun seedSamples(): List<CountdownItem> {
        val now = System.currentTimeMillis()
        val c1 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 42)
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val c2 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 120)
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }
        val c3 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 280)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        return listOf(
            CountdownItem(
                title = "Total Solar Eclipse Expedition",
                description = "Observing totality from the volcanic highlands with precision instruments.",
                targetEpochMillis = c1.timeInMillis,
                createdEpochMillis = now - 14 * 86_400_000L,
                category = CountdownCategory.ASTRONOMY,
                themeId = CountdownVisualTheme.OBSIDIAN_GOLD.id,
                cardStyle = CountdownCardStyle.RADIAL_PROGRESS,
                isPinned = true
            ),
            CountdownItem(
                title = "Kyoto Architectural Studio Residence",
                description = "Immersive study of classical woodcraft and minimalist Japanese spatial design.",
                targetEpochMillis = c2.timeInMillis,
                createdEpochMillis = now - 5 * 86_400_000L,
                category = CountdownCategory.TRAVEL,
                themeId = CountdownVisualTheme.AURORA_CYAN.id,
                cardStyle = CountdownCardStyle.NEO_GLASS,
                isPinned = false
            ),
            CountdownItem(
                title = "Studio Vernissage & Exhibition",
                description = "Debut showcase of generative digital sculptures and spatial typography.",
                targetEpochMillis = c3.timeInMillis,
                createdEpochMillis = now - 2 * 86_400_000L,
                category = CountdownCategory.CREATIVE,
                themeId = CountdownVisualTheme.ROSE_NOIR.id,
                cardStyle = CountdownCardStyle.ARCHITECTURAL,
                isPinned = false
            )
        )
    }

    private fun persistToFile(items: List<CountdownItem>) {
        try {
            val json = gson.toJson(items)
            val tempFile = File(context.filesDir, "countdowns_v2.json.tmp")
            tempFile.writeText(json)
            if (tempFile.renameTo(file) || run { file.delete(); tempFile.renameTo(file) }) {
                // successfully saved
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun save(item: CountdownItem) {
        mutex.withLock {
            val current = _countdowns.value.toMutableList()
            val index = current.indexOfFirst { it.id == item.id }
            if (index >= 0) {
                current[index] = item.copy(updatedAtEpochMillis = System.currentTimeMillis())
            } else {
                current.add(0, item)
            }
            val sorted = current.sortedWith(
                compareByDescending<CountdownItem> { it.isPinned }
                    .thenBy { it.targetEpochMillis }
            )
            _countdowns.value = sorted
            persistToFile(sorted)
        }
        WidgetBindingManager.updateAllWidgets(context)
    }

    suspend fun delete(id: String) {
        mutex.withLock {
            val current = _countdowns.value.toMutableList()
            current.removeAll { it.id == id }
            _countdowns.value = current
            persistToFile(current)
        }
        WidgetBindingManager.updateAllWidgets(context)
    }

    suspend fun togglePin(id: String) {
        mutex.withLock {
            val current = _countdowns.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index >= 0) {
                val item = current[index]
                current[index] = item.copy(isPinned = !item.isPinned)
                val sorted = current.sortedWith(
                    compareByDescending<CountdownItem> { it.isPinned }
                        .thenBy { it.targetEpochMillis }
                )
                _countdowns.value = sorted
                persistToFile(sorted)
            }
        }
        WidgetBindingManager.updateAllWidgets(context)
    }

    fun getById(id: String): CountdownItem? {
        return _countdowns.value.firstOrNull { it.id == id }
    }

    fun getAll(): List<CountdownItem> {
        return _countdowns.value
    }

    companion object {
        @Volatile
        private var INSTANCE: CountdownRepository? = null

        fun getInstance(context: Context): CountdownRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CountdownRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
