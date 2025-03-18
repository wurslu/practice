package com.example.practice

import androidx.annotation.DrawableRes
import kotlinx.serialization.Serializable

data class PetPost(
    val id: Int,
    val date: String,
    val title: String,
    val likeCount: Int,
    @DrawableRes val petImage: Int,
)

@Serializable
data class ApiPetPost(
    val id: Int, val date: String, val content: String, val likes: Int, val imageUrl: String
)

@Serializable
data class ApiPetPostResponse(
    val success: Boolean, val total: Int, val data: List<ApiPetPost>
)

val samplePetPostData = listOf<PetPost>(
    PetPost(1, "2025-01-31", "今天看到一只小小鸟", 498, R.drawable.bird),
    PetPost(2, "2025-02-21", "出来玩好开心", 662, R.drawable.bird2),
    PetPost(3, "2025-02-23", "不会有人运气这么好吧", 126, R.drawable.bird3),
    PetPost(4, "2025-02-25", "笑口常开", 162, R.drawable.bird4),
    PetPost(5, "2025-02-28", "我要是也会飞就好了", 998, R.drawable.bird5),
    PetPost(6, "2025-03-01", "小小鸟，抓抓抓", 872, R.drawable.bird6),
    PetPost(7, "2025-03-04", "谁敢说小鸟不美丽！！！", 41, R.drawable.bird7),
    PetPost(8, "2025-03-05", "呀呀呀！咋下雪啦", 941, R.drawable.snow),
    PetPost(9, "2025-03-11", "一览众山小", 611, R.drawable.mountain),

    )
