package com.yourpackage.iptv.data.parser

import com.yourpackage.iptv.data.models.Channel
import java.util.UUID

class M3UParser {
    fun parse(content: String): List<Channel> {
        val lines = content.lines()
        val channels = mutableListOf<Channel>()
        var currentExtInf: String? = null
        for (line in lines) {
            when {
                line.startsWith("#EXTINF:") -> currentExtInf = line
                line.isNotBlank() && !line.startsWith("#") -> {
                    val url = line.trim()
                    val channel = buildChannel(currentExtInf, url)
                    if (channel != null) channels.add(channel)
                    currentExtInf = null
                }
            }
        }
        return channels
    }

    private fun buildChannel(extInf: String?, url: String): Channel? {
        if (extInf == null) return null
        val tvgLogo = extract(extInf, "tvg-logo=\\"(.*?)\\"")
        val group = extract(extInf, "group-title=\\"(.*?)\\"")
        val name = extract(extInf, ",(.*?)$") ?: "Unknown"
        val epgId = extract(extInf, "tvg-id=\\"(.*?)\\"")
        val poster = extract(extInf, "tvg-poster=\\"(.*?)\\"")
        val year = extract(extInf, "tvg-year=\\"(.*?)\\"")
        val plot = extract(extInf, "tvg-plot=\\"(.*?)\\"")
        return Channel(
            id = UUID.randomUUID().toString(),
            name = name,
            logoUrl = tvgLogo ?: poster,
            group = group,
            streamUrl = url,
            epgId = epgId,
            posterUrl = poster,
            year = year,
            plot = plot
        )
    }

    private fun extract(text: String, pattern: String): String? {
        return Regex(pattern).find(text)?.groupValues?.get(1)
    }
}
