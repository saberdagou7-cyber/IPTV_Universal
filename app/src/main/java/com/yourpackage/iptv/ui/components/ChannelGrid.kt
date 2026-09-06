package com.yourpackage.iptv.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yourpackage.iptv.data.models.Channel

@Composable
fun ChannelGrid(
    channels: List<Channel>,
    isLoading: Boolean,
    error: String?,
    onChannelClick: (Channel) -> Unit,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    onFavouriteClick: (Channel) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        when {
            isLoading -> ShimmerGrid()
            error != null -> ErrorView(error, onRefresh, onSettingsClick)
            channels.isEmpty() -> EmptyView(onSettingsClick)
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val grouped = channels.groupBy { it.group ?: "General" }
                    grouped.keys.sorted().forEach { group ->
                        item {
                            Text(
                                text = group,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                        items(grouped[group] ?: emptyList()) { channel ->
                            ChannelCard(channel, onChannelClick, onFavouriteClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelCard(
    channel: Channel,
    onClick: (Channel) -> Unit,
    onFavouriteClick: (Channel) -> Unit = {}
) {
    Card(
        onClick = { onClick(channel) },
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = channel.logoUrl ?: channel.posterUrl,
                contentDescription = channel.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
            )
            Text(
                text = channel.name,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
            IconButton(
                onClick = { onFavouriteClick(channel) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (channel.isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun ShimmerGrid() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(8) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.3f)))
            }
        }
    }
}

@Composable
fun ErrorView(error: String, onRefresh: () -> Unit, onSettings: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text("⚠️ Error", color = Color.White, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(error, color = Color.White.copy(0.7f), modifier = Modifier.padding(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onRefresh) { Text("🔄 Retry") }
            Button(onClick = onSettings) { Text("⚙️ Settings") }
        }
    }
}

@Composable
fun EmptyView(onSettings: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        Text("📺 No channels found", color = Color.White, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSettings) { Text("Configure Playlist") }
    }
}
