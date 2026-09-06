package com.yourpackage.iptv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yourpackage.iptv.data.models.Channel
import com.yourpackage.iptv.data.models.Program
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EpgGuideScreen(
    channels: List<Channel>,
    programs: List<Program>
) {
    val programsByChannel = programs.groupBy { it.channelId }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Text(
            text = "📅 TV Guide",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Simple EPG view - show first few channels with their current program
        channels.filter { it.epgId != null && programsByChannel.containsKey(it.epgId) }
            .take(10)
            .forEach { channel ->
                val channelPrograms = programsByChannel[channel.epgId] ?: emptyList()
                if (channelPrograms.isNotEmpty()) {
                    EpgChannelItem(channel, channelPrograms.take(2), timeFormat)
                }
            }
    }
}

@Composable
fun EpgChannelItem(
    channel: Channel,
    programs: List<Program>,
    timeFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel info
            Row(
                modifier = Modifier.weight(0.3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = channel.name,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2A2A2A))
                )
                Text(
                    text = channel.name,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Program info
            Column(
                modifier = Modifier.weight(0.7f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                programs.forEachIndexed { index, program ->
                    val startTime = timeFormat.format(Date(program.startTime))
                    val endTime = timeFormat.format(Date(program.endTime))
                    Text(
                        text = "${if (index == 0) "Now" else "Next"}: $startTime - $endTime ${program.title}",
                        color = Color.White.copy(0.8f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
