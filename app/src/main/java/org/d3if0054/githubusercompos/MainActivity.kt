package org.d3if0054.githubusercompos

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.d3if0054.githubusercompos.component.Detail
import org.d3if0054.githubusercompos.component.ItemRow
import org.d3if0054.githubusercompos.component.ListFavorite
import org.d3if0054.githubusercompos.component.SearchBar
import org.d3if0054.githubusercompos.component.Settings
import org.d3if0054.githubusercompos.ui.theme.GithubUserComposTheme
import org.d3if0054.githubusercompos.utils.Result
import org.d3if0054.githubusercompos.utils.SettingPreferences
import org.d3if0054.githubusercompos.viewModel.DarkViewModel
import org.d3if0054.githubusercompos.viewModel.DetailViewModel
import org.d3if0054.githubusercompos.viewModel.MainViewModel


class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private val detailViewModel: DetailViewModel by viewModels()
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUser()
        setContent {
            GithubUserComposTheme(darkTheme = isDarkMode(dataStore = dataStore)) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            Home(viewModel, navController)
                        }
                        composable("detail/{username}") {
                            val username = it.arguments?.getString("username")
                            if (username != null) {
                                detailViewModel.getDetailsUser(username)
                            }
                            val detailUser = detailViewModel.resultDetailsUser
                            Detail(
                                user = detailUser,
                                navController = navController,
                                detailViewModel
                            )
                        }
                        composable("settings") {
                            Settings(dataStore, navController)
                        }
                        composable("favorite") {
                            ListFavorite(viewModel = detailViewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, content: @Composable (Modifier) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate("favorite")
                    }) {
                        Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Favorite")
                    }
                    IconButton(onClick = {
                        navController.navigate("settings")
                    }) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        content(Modifier.padding(padding))
    }
}

@Composable
fun Home(viewModel: MainViewModel, navController: NavHostController) {
    var searchUser by remember {
        mutableStateOf("")
    }

    val resultState by viewModel.resultUser.collectAsState()

    MainScreen(navController) {
        Column(
            modifier = it
                .padding(top = 8.dp)
                .fillMaxSize()
        ) {
            SearchBar(searchUser, onSearchTextChanged = { searchText ->
                searchUser = searchText
            }, onSearch = {
                viewModel.searchUser(searchUser)
            })

            when (val result = resultState) {
                is Result.Loading -> {
                }

                is Result.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(result.data) { user ->
                            ItemRow(user = user, navController)
                        }
                    }
                }

                is Result.Error -> {
                    if (result.errorMessage.contains("HTTP 422")) {
                        Text(
                            text = "User Github tidak ditemukan",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Text(
                            text = "Error: ${result.errorMessage}",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun isDarkMode(dataStore: DataStore<Preferences>): Boolean {
    val pref = SettingPreferences.getInstance(dataStore)
    val darkViewModel = DarkViewModel(pref)
    val darkModeState by darkViewModel.getThemeSettings().observeAsState(isSystemInDarkTheme())

    return darkModeState
}
