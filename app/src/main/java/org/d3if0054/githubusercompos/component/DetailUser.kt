package org.d3if0054.githubusercompos.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.dicoding.githubuser.data.database.Favorite
import org.d3if0054.githubusercompos.model.Items
import org.d3if0054.githubusercompos.model.ResponseDetailsUser
import org.d3if0054.githubusercompos.viewModel.DetailViewModel

@Composable
fun Detail(
    user: ResponseDetailsUser?,
    navController: NavHostController,
    viewModel: DetailViewModel
) {
    val isFavorite = isFavorite(viewModel = viewModel, user = user)
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        user?.login?.let { viewModel.getFollowers(it) }
    }
    DetailTopBar(navController = navController) { modifier ->
        Column(
            modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (user != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(model = user.avatarUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            FloatingActionButton(
                                onClick = {
                                    if (isFavorite) {
                                        viewModel.removeFromFav(user.id)
                                    } else {
                                        val favUser = Favorite(
                                            id = user.id,
                                            login = user.login,
                                            avatarUrl = user.avatarUrl
                                        )
                                        viewModel.addToFavorite(favUser)
                                    }
                                },
                                shape = CircleShape
                            ) {
                                if (isFavorite) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "favorite"
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                        Text(
                            text = user.login,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = user.name ?: "",
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                        Row {
                            Text(text = "${user.followers} Followers")
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "${user.following} Following")
                        }
                    }
                }
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = {
                            selectedTabIndex = 0
                            viewModel.getFollowers(user.login ?: "")
                        },
                        text = { Text("Followers") }
                    )

                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            viewModel.getFollowing(user.login ?: "")
                        },
                        text = { Text("Following") }
                    )
                }

                when (selectedTabIndex) {
                    0 -> {
                        FollowersList(
                            viewModel.resultFollowersUser ?: emptyList(),
                            navController
                        )
                    }

                    1 -> {
                        FollowingsList(
                            viewModel.resultFollowingUser ?: emptyList(),
                            navController
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FollowersList(followers: List<Items>, navController: NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followers) { user ->
            ItemRow(user = user, navController)
        }
    }
}

@Composable
fun FollowingsList(followings: List<Items>, navController: NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(followings) { user ->
            ItemRow(user = user, navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Detail Profile")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        content(Modifier.padding(padding))
    }
}

@Composable
fun isFavorite(viewModel: DetailViewModel, user: ResponseDetailsUser?): Boolean {
    val favList by viewModel.getAllfavorite().observeAsState()
    var isFavorite = false

    favList?.let {
        for (data in it) {
            if (user != null) {
                if (user.id == data.id) {
                    isFavorite = true
                    break
                }
            }
        }
    }
    return isFavorite
}
