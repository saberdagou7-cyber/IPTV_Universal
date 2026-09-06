package com.yourpackage.iptv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    val player: ExoPlayer by lazy { ExoPlayer.Builder(context).build() }
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    fun playChannel(streamUrl: String) {
        try {
            val mediaSource = HlsMediaSource.Factory(player).createMediaSource(MediaItem.fromUri(streamUrl))
            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
            _isPlaying.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun pause() {
        player.pause()
        _isPlaying.value = false
    }
    
    fun resume() {
        player.play()
        _isPlaying.value = true
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
