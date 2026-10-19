package com.redgravity.countdown.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.redgravity.countdown.model.CountdownItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class CountdownRepository(private val context: Context) {

    private val gson = Gson()
    private val file = File(context.filesDir, "countdowns_v3.json")
    private val mutex = Mutex()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _countdowns = MutableStateFlow<List<CountdownItem>>(emptyList())
    val countdowns: StateFlow<List<CountdownItem>> = _countdowns.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        if (!file.exists()) {
            // Fresh install: start empty, no default countdowns
            _countdowns.value = emptyList()
        } else {
            try {
                val json = file.readText()
                val type = object : TypeToken<List<CountdownItem>>() {}.type
                val items: List<CountdownItem> = gson.fromJson(json, type) ?: emptyList()
                _countdowns.value = items.sortedBy { it.targetEpochMillis }
            } catch (e: Exception) {
                e.printStackTrace()
                _countdowns.value = emptyList()
            }
        }
    }

    private fun persistToFile(items: List<CountdownItem>) {
        try {
            val json = gson.toJson(items)
            val tempFile = File(context.filesDir, "countdowns_v3.json.tmp")
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
            val sorted = current.sortedBy { it.targetEpochMillis }
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
