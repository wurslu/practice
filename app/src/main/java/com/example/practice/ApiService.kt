package com.example.practice

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object KtorClient {
    private const val BASE_URL = "https://api.wurslu.com/api"
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }
    val baseUrl = BASE_URL
}

object ApiService {
    suspend fun getPost(): List<PetPost> {
        val response: ApiPetPostResponse =
            KtorClient.client.get("${KtorClient.baseUrl}/pet-posts").body()

        return response.data.map { apiPost ->
            val resourceId = when (apiPost.imageUrl) {
                "bird" -> R.drawable.bird
                "bird2" -> R.drawable.bird2
                "bird3" -> R.drawable.bird3
                "bird4" -> R.drawable.bird4
                "bird5" -> R.drawable.bird5
                "bird6" -> R.drawable.bird6
                "bird7" -> R.drawable.bird7
                "snow" -> R.drawable.snow
                "mountain" -> R.drawable.mountain
                else -> R.drawable.bird // 默认图片
            }

            PetPost(
                id = apiPost.id,
                date = apiPost.date,
                title = apiPost.content,
                likeCount = apiPost.likes,
                petImage = resourceId
            )
        }
    }
}