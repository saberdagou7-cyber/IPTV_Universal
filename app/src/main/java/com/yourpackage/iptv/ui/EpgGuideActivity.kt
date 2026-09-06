package com.yourpackage.iptv.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourpackage.iptv.data.database.AppDatabase
import com.yourpackage.iptv.data.models.Channel
import com.yourpackage.iptv.data.models.Program
import com.yourpackage.iptv.ui.theme.IptvTheme
import com.yourpackage.iptv.viewmodel.EpgViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EpgGuideActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IptvTheme {
                val db = AppDatabase.getInstance(this@EpgGuideActivity)
                val viewModel = EpgViewModel(db)
                EpgGuideScreen(viewModel) { finish() }
            }
        }
    }
}

@Composable
fun EpgGuideScreen(viewModel: EpgViewModel, onBack: () -> Unit) {
    val channels by viewModel.channels.collectAsState()
    val programs by viewModel.programs.collectAsState()
    val programsByChannel = programs.groupBy { it.channelId }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                "📅 TV Guide",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // EPG List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(channels.filter { it.epgId != null && programsByChannel.containsKey(it.epgId) }) { channel ->
                val channelPrograms = programsByChannel[channel.epgId] ?: emptyList()
                if (channelPrograms.isNotEmpty()) {
                    EpgChannelCard(channel, channelPrograms.take(2), timeFormat)
                }
            }
        }
    }
}

@Composable
fun EpgChannelCard(channel: Channel, programs: List<Program>, timeFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = channel.name,
                color = Color(0xFF03DAC6),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            programs.forEachIndexed { index, program ->
                val label = if (index == 0) "Now:" else "Next:"
                Text(
                    text = "$label ${timeFormat.format(Date(program.startTime))} - ${program.title}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                if (program.description != null) {
                    Text(
                        text = program.description,
                        color = Color.White.copy(0.6f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
                if (index < programs.size - 1) Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
