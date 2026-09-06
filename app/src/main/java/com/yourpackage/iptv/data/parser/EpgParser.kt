package com.yourpackage.iptv.data.parser

import com.yourpackage.iptv.data.models.Program
import org.xmlpull.v1.XmlPullParserFactory
import java.text.SimpleDateFormat
import java.util.Locale

class EpgParser {
    fun parse(xml: String): List<Program> {
        val programs = mutableListOf<Program>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())
            var eventType = parser.eventType
            var currentChannel: String? = null
            var currentTitle: String? = null
            var currentDesc: String? = null
            var currentStart: String? = null
            var currentStop: String? = null
            val dateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US)

            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "programme" -> {
                                currentChannel = parser.getAttributeValue(null, "channel")
                                currentStart = parser.getAttributeValue(null, "start")
                                currentStop = parser.getAttributeValue(null, "stop")
                            }
                            "title" -> currentTitle = parser.nextText()
                            "desc" -> currentDesc = parser.nextText()
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> {
                        if (parser.name == "programme" && currentChannel != null && currentStart != null && currentStop != null) {
                            val startTime = dateFormat.parse(currentStart!!.replace(" ", "+"))?.time ?: 0
                            val endTime = dateFormat.parse(currentStop!!.replace(" ", "+"))?.time ?: 0
                            programs.add(Program(
                                channelId = currentChannel!!,
                                title = currentTitle ?: "Unknown",
                                description = currentDesc,
                                startTime = startTime,
                                endTime = endTime
                            ))
                            currentChannel = null; currentTitle = null; currentDesc = null; currentStart = null; currentStop = null
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return programs
    }
}
