package com.yourpackage.iptv.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.media3.ui.PlayerView
import com.yourpackage.iptv.data.database.AppDatabase
import com.yourpackage.iptv.data.models.Channel
import com.yourpackage.iptv.data.repository.PlaylistRepository
import com.yourpackage.iptv.ui.theme.IptvTheme
import com.yourpackage.iptv.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayerActivity : ComponentActivity() {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var channel: Channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channel = intent.getSerializableExtra("CHANNEL") as? Channel ?: return
        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]

        setContent {
            IptvTheme {
                PlayerScreen(viewModel, channel) { finish() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.player.release()
    }

    companion object {
        fun start(ctx: Context, c: Channel) {
            ctx.startActivity(Intent(ctx, PlayerActivity::class.java).putExtra("CHANNEL", c))
        }
    }
}

@Composable
fun PlayerScreen(viewModel: PlayerViewModel, channel: Channel, onBack: () -> Unit) {
    var showOverlay by remember { mutableStateOf(true) }
    val isPlaying by viewModel.isPlaying.collectAsState()
    val db = AppDatabase.getInstance(androidx.compose.ui.platform.LocalContext.current)
    val repo = PlaylistRepository(db)
    val nowNext by repo.getNowNext(channel.epgId ?: "").collectAsState(initial = emptyList())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Box(modifier = Modifier.fillMaxSize()) {
        // Player View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = viewModel.player
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay with channel info and EPG
        if (showOverlay) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000))
                    .clickable { showOverlay = false }
                    .padding(32.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
                if (nowNext.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Now: ${timeFormat.format(Date(nowNext[0].startTime))} - ${nowNext[0].title}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (nowNext.size > 1) {
                        Text(
                            text = "Next: ${timeFormat.format(Date(nowNext[1].startTime))} - ${nowNext[1].title}",
                            color = Color.White.copy(0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            LaunchedEffect(Unit) { delay(5000); showOverlay = false }
        }
    }

    LaunchedEffect(Unit) { viewModel.playChannel(channel.streamUrl) }
}
