package com.yourpackage.iptv.data.repository

import com.yourpackage.iptv.data.database.AppDatabase
import com.yourpackage.iptv.data.models.Channel
import com.yourpackage.iptv.data.models.Program
import com.yourpackage.iptv.data.network.RetrofitClient
import com.yourpackage.iptv.data.parser.EpgParser
import com.yourpackage.iptv.data.parser.M3UParser
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val db: AppDatabase) {
    private val channelDao = db.channelDao()
    private val programDao = db.programDao()
    private val api = RetrofitClient.apiService

    suspend fun refreshPlaylist(playlistUrl: String, epgUrl: String? = null) {
        val content = api.getPlaylist(playlistUrl)
        val channels = M3UParser().parse(content)
        channelDao.clear()
        channelDao.insertAll(channels)
        if (epgUrl != null) {
            try {
                val epgContent = api.getEpg(epgUrl)
                val programs = EpgParser().parse(epgContent)
                programDao.clear()
                programDao.insertAll(programs)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getChannels(): Flow<List<Channel>> = channelDao.getAll()
    fun getFavouriteChannels(): Flow<List<Channel>> = channelDao.getFavourites()
    fun getNowNext(channelId: String): Flow<List<Program>> = programDao.getNowNext(channelId, System.currentTimeMillis())
    fun search(query: String): Flow<List<Channel>> = channelDao.search(query)
    
    suspend fun toggleFavourite(channel: Channel) {
        channel.isFavourite = !channel.isFavourite
        channelDao.insertAll(listOf(channel))
    }
}
