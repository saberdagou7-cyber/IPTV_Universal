package com.yourpackage.iptv.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourpackage.iptv.data.SettingsManager
import com.yourpackage.iptv.data.database.AppDatabase
import com.yourpackage.iptv.data.repository.PlaylistRepository
import com.yourpackage.iptv.ui.components.ChannelGrid
import com.yourpackage.iptv.ui.theme.IptvTheme
import com.yourpackage.iptv.viewmodel.PlaylistViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    private lateinit var viewModel: PlaylistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        val db = AppDatabase.getInstance(this)
        viewModel = PlaylistViewModel(PlaylistRepository(db))

        lifecycleScope.launch {
            settingsManager.playlistUrlFlow.collect { url ->
                if (url.isBlank()) {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    finish()
                } else {
                    settingsManager.epgUrlFlow.collect { epgUrl ->
                        viewModel.refresh(url, epgUrl)
                    }
                }
            }
        }

        setContent {
            IptvTheme {
                val channels by viewModel.channels.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val error by viewModel.error.collectAsState()
                var selectedTab by remember { mutableStateOf(0) }
                val tabs = listOf("Live TV", "Movies", "Series", "Favorites")

                val filtered = channels.filter {
                    when (selectedTab) {
                        0 -> it.group?.contains("movie", true) != true && it.group?.contains("series", true) != true
                        1 -> it.group?.contains("movie", true) == true
                        2 -> it.group?.contains("series", true) == true
                        3 -> it.isFavourite
                        else -> true
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "📺 IPTV Pro",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF03DAC6)
                        )
                        Row {
                            IconButton(onClick = { startActivity(Intent(this@MainActivity, EpgGuideActivity::class.java)) }) {
                                Icon(Icons.Default.Event, contentDescription = "EPG Guide", tint = Color(0xFF03DAC6))
                            }
                            IconButton(onClick = { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF03DAC6))
                            }
                        }
                    }

                    // Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        edgePadding = 16.dp
                    ) {
                        tabs.forEachIndexed { idx, title ->
                            Tab(
                                selected = selectedTab == idx,
                                onClick = { selectedTab = idx },
                                text = { Text(title) }
                            )
                        }
                    }

                    // Content
                    ChannelGrid(
                        channels = filtered,
                        isLoading = isLoading,
                        error = error,
                        onChannelClick = { PlayerActivity.start(this@MainActivity, it) },
                        onRefresh = {
                            lifecycleScope.launch {
                                settingsManager.playlistUrlFlow.collect { url ->
                                    settingsManager.epgUrlFlow.collect { epgUrl ->
                                        viewModel.refresh(url, epgUrl)
                                    }
                                }
                            }
                        },
                        onSettingsClick = { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) },
                        onFavouriteClick = { viewModel.toggleFavourite(it) }
                    )
                }
            }
        }
    }
}
