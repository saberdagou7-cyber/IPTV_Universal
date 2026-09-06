package com.yourpackage.iptv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.iptv.data.database.AppDatabase
import com.yourpackage.iptv.data.models.Channel
import com.yourpackage.iptv.data.models.Program
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EpgViewModel(private val db: AppDatabase) : ViewModel() {
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels
    
    private val _programs = MutableStateFlow<List<Program>>(emptyList())
    val programs: StateFlow<List<Program>> = _programs

    init {
        viewModelScope.launch {
            db.channelDao().getAll().collect { _channels.value = it }
            val today = System.currentTimeMillis()
            val start = today - (today % (24*60*60*1000))
            _programs.value = db.programDao().getAllForDate(start, start + 24*60*60*1000)
        }
    }
}
