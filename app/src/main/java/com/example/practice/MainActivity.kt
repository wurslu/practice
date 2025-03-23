package com.example.practice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.practice.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeApp()
        }
    }
}

@Composable
fun PracticeApp(
    viewModel: PetViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val uiState by viewModel.uiState.collectAsState()
    var shouldShowTopBar by remember { mutableStateOf(true) }

    shouldShowTopBar = when (currentRoute) {
        BottomBarItem.Home.route -> true
        BottomBarItem.Profile.route -> false
        BottomBarItem.Settings.route -> false
        BottomBarItem.Add.route -> false
        else -> true
    }

    PracticeTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            if (shouldShowTopBar) {
                PracticeTopBar(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                        .windowInsetsPadding(WindowInsets.statusBars)
                )
            }
        }, bottomBar = {
            PracticeBottomNavigation(navController = navController, currentRoute = currentRoute)
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomBarItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = BottomBarItem.Home.route) {
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        uiState.error != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.error ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        uiState.petPost.isNotEmpty() -> {
                            PracticeContent(petPosts = uiState.petPost)
                        }

                        else -> {
                            PracticeContent(petPosts = samplePetPostData)
                        }
                    }
                }
                composable(route = BottomBarItem.Profile.route) {
                    Profile()
                }
                composable(route = BottomBarItem.Settings.route) {
                    Settings()
                }
                composable(route = BottomBarItem.Add.route) {
                    Upload()
                }
            }
        }
    }
}

@Composable
fun PracticeBottomNavigation(
    navController: NavController, currentRoute: String?, modifier: Modifier = Modifier
) {
    val bottomBarItemList = listOf<BottomBarItem>(
        BottomBarItem.Home, BottomBarItem.Add, BottomBarItem.Profile, BottomBarItem.Settings
    )
    NavigationBar(modifier = modifier) {
        bottomBarItemList.forEach { item ->
            NavigationBarItem(selected = item.route == currentRoute,
                onClick = {
                    if (item.route != currentRoute) {
                        navController.navigate(route = item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeTopBar(modifier: Modifier = Modifier) {

    var searchText by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    Surface(
        shadowElevation = 1.dp, tonalElevation = 0.dp, modifier = Modifier
    ) {
        if (isSearchActive) {
            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = { isSearchActive = false },
                active = true,
                onActiveChange = { isSearchActive = it },
                placeholder = { Text("搜索") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        modifier = Modifier.clickable {
                            if (searchText.isNotEmpty()) {
                                searchText = ""
                            }
                            isSearchActive = false
                        })
                },
                modifier = Modifier,
            ) {
                val searchAdvice = "搜索建议"
                ListItem(
                    headlineContent = { Text(searchAdvice) },
                    modifier = Modifier.clickable {
                        searchText = searchAdvice
                        isSearchActive = false
                    }
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.MailOutline,
                    contentDescription = "邮件图标",
                    modifier = Modifier.weight(0.1f),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                SearchBar(
                    query = searchText,
                    onQueryChange = { searchText = it },
                    onSearch = {},
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text("搜索") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    },
                    modifier = Modifier.weight(0.8f),
                ) {

                }
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "用户中心图标",
                    modifier = Modifier.weight(0.1f),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

    }
}

@Composable
fun PracticeContent(petPosts: List<PetPost>, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Surface {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(4.dp),
            modifier = modifier
        ) {
            items(petPosts) { post ->
                PetCard(
                    postDate = post.date,
                    postTitle = post.title,
                    petImage = post.petImage,
                    likeCount = post.likeCount,
                    onShareClick = {
                        val shareText = "https://android-studio.s3.bitiful.net/017-practice-v1.9.7.apk"
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "复制链接到浏览器下载App查看"))
                    }
                )
            }
        }
    }
}

@Composable
fun PetCard(
    postDate: String,
    postTitle: String,
    likeCount: Int,
    @DrawableRes petImage: Int,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(petImage),
            contentDescription = null,
            modifier = Modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = postDate,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = postTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "like count",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = likeCount.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "share",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable {
                        onShareClick()
                    }
                )
            }
        }
    }
}


@Composable
fun Settings(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "设置页面",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun Profile(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "个人信息",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun Upload(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "上传照片页面",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PracticeAppPreview() {
    PracticeTheme {
        PracticeApp()
    }
}