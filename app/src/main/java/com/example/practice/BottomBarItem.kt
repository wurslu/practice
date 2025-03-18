package com.example.practice

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarItem(
    val route: String,
    val icon: ImageVector,
    val title: String
){
    object Home: BottomBarItem("home", Icons.Default.Home,"主页")
    object Settings: BottomBarItem("settings", Icons.Default.Settings,"设置")
    object Add: BottomBarItem("add", Icons.Default.Add,"添加")
    object Profile: BottomBarItem("profile", Icons.Default.Person,"我的")
}