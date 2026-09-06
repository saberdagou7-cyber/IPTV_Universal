package com.yourpackage.iptv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.yourpackage.iptv.data.models.VodContent

@Composable
fun VodGrid(
    content: List<VodContent>,
    isLoading: Boolean,
    onContentClick: (VodContent) -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
            Text("Loading...", color = Color.White)
        }
    } else if (content.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
            Text("No content available", color = Color.White)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(content) { item ->
                VodContentCard(item, onContentClick)
            }
        }
    }
}

@Composable
fun VodContentCard(
    content: VodContent,
    onClick: (VodContent) -> Unit
) {
    Card(
        onClick = { onClick(content) },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = content.backdropUrl ?: content.posterUrl,
                contentDescription = content.title,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize().background(Color(0x88000000)))
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = content.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (content.year != null) {
                    Text(
                        text = "${content.year}",
                        color = Color.White.copy(0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (content.rating != null) {
                    Text(
                        text = "⭐ ${content.rating}",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
