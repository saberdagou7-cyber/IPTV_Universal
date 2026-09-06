package com.yourpackage.iptv.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.yourpackage.iptv.data.repository.VodRepository
import com.yourpackage.iptv.ui.theme.IptvTheme
import com.yourpackage.iptv.viewmodel.VodViewModel

class VodDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentId = intent.getStringExtra("CONTENT_ID") ?: return
        
        setContent {
            IptvTheme {
                val db = AppDatabase.getInstance(this@VodDetailActivity)
                val repo = VodRepository(db)
                val viewModel = VodViewModel(repo)
                VodDetailScreen(contentId, repo) { finish() }
            }
        }
    }
}

@Composable
fun VodDetailScreen(contentId: String, repo: VodRepository, onBack: () -> Unit) {
    var content by remember { mutableStateOf<com.yourpackage.iptv.data.models.VodContent?>(null) }
    
    LaunchedEffect(contentId) {
        content = repo.db.vodContentDao().getById(contentId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
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
        }

        if (content != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = content!!.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                if (content!!.year != null) {
                    Text("Year: ${content!!.year}", color = Color.White)
                }
                if (content!!.rating != null) {
                    Text("⭐ Rating: ${content!!.rating}/10", color = Color.Yellow)
                }
                if (content!!.description != null) {
                    Text(
                        text = content!!.description!!,
                        color = Color.White.copy(0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(
                    onClick = { /* Play content */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("▶️ Watch Now")
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF03DAC6))
            }
        }
    }
}
