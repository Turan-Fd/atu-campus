package com.atu.campus.services

import com.atu.campus.data.AtuNews
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AtuNewsService(
    private val newsUrl: String = "https://atu.edu.az/xeberler/-1"
) {
    suspend fun fetchNews(): List<AtuNews> = withContext(Dispatchers.IO) {
        try {
            parseNews(URL(newsUrl).readText(Charsets.UTF_8))
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseNews(html: String): List<AtuNews> {
        val itemRegex = Regex(
            pattern = """<div class="blog-item.*?">.*?<img\s+src="([^"]+)".*?<span><i class="fa fa-calendar"></i>\s*<a href="#">([^<]+)</a>.*?<h3>\s*<a href="([^"]+)".*?>(.*?)</a></h3>.*?<div class="blog-desc">\s*<p>(.*?)</p>""",
            options = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
        )

        return itemRegex.findAll(html)
            .map { match ->
                AtuNews(
                    imageUrl = match.groupValues[1].trim(),
                    date = match.groupValues[2].trim(),
                    url = match.groupValues[3].trim(),
                    title = match.groupValues[4].stripHtml().decodeHtml(),
                    summary = match.groupValues[5].stripHtml().decodeHtml().take(180)
                )
            }
            .filter { it.title.isNotBlank() }
            .take(12)
            .toList()
    }

    private fun String.stripHtml(): String =
        replace(Regex("<[^>]*>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun String.decodeHtml(): String =
        replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .trim()
}
