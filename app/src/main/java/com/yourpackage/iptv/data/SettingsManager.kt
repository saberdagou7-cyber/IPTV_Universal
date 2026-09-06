package com.yourpackage.iptv.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        private val PLAYLIST_URL = stringPreferencesKey("playlist_url")
        private val EPG_URL = stringPreferencesKey("epg_url")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
    }
    
    val playlistUrlFlow: Flow<String> = context.dataStore.data.map { it[PLAYLIST_URL] ?: "" }
    val epgUrlFlow: Flow<String> = context.dataStore.data.map { it[EPG_URL] ?: "" }
    val userIdFlow: Flow<String> = context.dataStore.data.map { it[USER_ID] ?: "" }
    val userEmailFlow: Flow<String> = context.dataStore.data.map { it[USER_EMAIL] ?: "" }
    
    suspend fun saveUrls(playlist: String, epg: String) {
        context.dataStore.edit { prefs ->
            prefs[PLAYLIST_URL] = playlist
            prefs[EPG_URL] = epg
        }
    }
    
    suspend fun saveUser(userId: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[USER_EMAIL] = email
        }
    }
    
    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID)
            prefs.remove(USER_EMAIL)
        }
    }
}
