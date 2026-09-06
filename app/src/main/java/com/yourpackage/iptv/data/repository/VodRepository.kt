package com.yourpackage.iptv.data.repository

import com.yourpackage.iptv.data.database.AppDatabase
import com.yourpackage.iptv.data.models.VodContent
import kotlinx.coroutines.flow.Flow

class VodRepository(private val db: AppDatabase) {
    private val vodDao = db.vodContentDao()

    fun getMovies(): Flow<List<VodContent>> = vodDao.getByType("movie")
    fun getSeries(): Flow<List<VodContent>> = vodDao.getByType("series")
    fun getFavourites(): Flow<List<VodContent>> = vodDao.getFavourites()
    fun search(query: String): Flow<List<VodContent>> = vodDao.search(query)
    
    suspend fun getContentById(id: String): VodContent? = vodDao.getById(id)
    
    suspend fun toggleFavourite(content: VodContent) {
        content.isFavourite = !content.isFavourite
        vodDao.insertAll(listOf(content))
    }
    
    suspend fun insertContent(content: List<VodContent>) {
        vodDao.insertAll(content)
    }
}
