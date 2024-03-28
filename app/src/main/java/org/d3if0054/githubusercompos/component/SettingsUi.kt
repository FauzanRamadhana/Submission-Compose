package org.d3if0054.githubusercompos.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import org.d3if0054.githubusercompos.utils.SettingPreferences
import org.d3if0054.githubusercompos.viewModel.DarkViewModel

@Composable
fun Settings(dataStore: DataStore<Preferences>, navController: NavHostController) {
    val pref = SettingPreferences.getInstance(dataStore)
    val darkViewModel = DarkViewModel(pref)
    val darkModeState by darkViewModel.getThemeSettings().observeAsState(isSystemInDarkTheme())

    TopBar(navController) {
        Column(
            it.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(text = "Dark Mode")
            Switch(
                checked = darkModeState,
                onCheckedChange = { darkViewModel.saveThemeSetting(it) },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController, content: @Composable (Modifier) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Settings")
            },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                })
        }
    ) { padding ->
        content(Modifier.padding(padding))
    }
}