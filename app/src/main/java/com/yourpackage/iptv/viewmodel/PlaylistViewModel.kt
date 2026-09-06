package com.yourpackage.iptv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.iptv.data.models.Channel
import com.yourpackage.iptv.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(private val repository: PlaylistRepository) : ViewModel() {
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { loadChannels() }

    fun loadChannels() {
        viewModelScope.launch {
            repository.getChannels().collect { _channels.value = it }
        }
    }

    fun refresh(playlistUrl: String, epgUrl: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshPlaylist(playlistUrl, epgUrl)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavourite(channel: Channel) {
        viewModelScope.launch {
            repository.toggleFavourite(channel)
        }
    }
    
    fun search(query: String) {
        viewModelScope.launch {
            repository.search(query).collect { _channels.value = it }
        }
    }
}
