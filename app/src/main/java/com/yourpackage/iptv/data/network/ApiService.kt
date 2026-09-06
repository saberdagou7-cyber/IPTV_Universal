package com.yourpackage.iptv.data.network

import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getPlaylist(@Url url: String): String

    @GET
    suspend fun getEpg(@Url url: String): String
}
