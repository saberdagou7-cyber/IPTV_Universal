package com.yourpackage.iptv.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yourpackage.iptv.data.SettingsManager
import com.yourpackage.iptv.ui.theme.IptvTheme
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        setContent {
            IptvTheme {
                SettingsScreen(settingsManager) { finish() }
            }
        }
    }
}

@Composable
fun SettingsScreen(settingsManager: SettingsManager, onBack: () -> Unit) {
    var playlistUrl by remember { mutableStateOf("") }
    var epgUrl by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        settingsManager.playlistUrlFlow.collect { playlistUrl = it }
        settingsManager.epgUrlFlow.collect { epgUrl = it }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "⚙️ Settings",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
        
        // Preset buttons
        val presets = mapOf(
            "Pluto TV" to "https://iptv-org.github.io/iptv/playlists/pluto.m3u",
            "Plex" to "https://iptv-org.github.io/iptv/playlists/plex.m3u"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { (name, url) ->
                Button(
                    onClick = { playlistUrl = url },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(name)
                }
            }
        }
        
        // Input fields
        OutlinedTextField(
            value = playlistUrl,
            onValueChange = { playlistUrl = it },
            label = { Text("M3U Playlist URL") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                unfocusedLabelColor = Color.White.copy(0.7f)
            )
        )
        
        OutlinedTextField(
            value = epgUrl,
            onValueChange = { epgUrl = it },
            label = { Text("EPG/XMLTV URL (optional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                unfocusedLabelColor = Color.White.copy(0.7f)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                scope.launch {
                    settingsManager.saveUrls(playlistUrl, epgUrl)
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("💾 Save & Return")
        }
    }
}
