package com.areyer.leaguefriends.ui.screens

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.areyer.leaguefriends.network.repositories.SummonerRepo
import com.areyer.leaguefriends.ui.theme.LeagueFriendsTheme
import com.areyer.leaguefriends.ui.viewmodels.HomeViewModel
import com.areyer.leaguefriends.ui.viewmodels.HomeViewModelFactory
import com.areyer.leaguefriends.workers.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(SummonerRepo(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationWorker.scheduleSummonerQuery(applicationContext)

        setContent {
            LeagueFriendsTheme {
                Surface(modifier = Modifier
                    .fillMaxSize()) {
                    SummonerScreen(homeViewModel, applicationContext)
                }
            }
        }
    }
}

@Composable
fun SummonerScreen(homeViewModel: HomeViewModel, context: Context) {
    var text by remember { mutableStateOf("") }
    val state by homeViewModel.state.collectAsState()
    val selectedSummoner = remember { mutableStateOf("") }
    val openDialog = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = "Remove summoner?", modifier = Modifier.padding(16.dp)) },
            confirmButton = {
                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        openDialog.value = false
                        homeViewModel.removeSummoner(selectedSummoner.value)
                    }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = { openDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            TextField(
                value = text,
                modifier = Modifier.padding(top = 16.dp),
                onValueChange = { text = it },
                label = { Text("Add a Summoner") }
            )
        }
        item {
            Button(onClick = {
                    homeViewModel.viewModelScope.launch(Dispatchers.IO) {
                        loading.value = true
                        if (!homeViewModel.addSummoner(text)) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to add summoner '$text'",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                        loading.value = false
                    }
                 },
                modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)) {
                Text("Add")
            }
        }
        if (loading.value) {
            item {
                CircularProgressIndicator(modifier = Modifier.size(30.dp))
            }
        }
        items(state) { summonerInfo ->
            SummonerEntry(name = summonerInfo.name, online = summonerInfo.online,
                openDialog, selectedSummoner)
        }
    }
}

@Composable
fun SummonerEntry(name: String, online: Boolean, openDialog: MutableState<Boolean>,
        selectedSummoner: MutableState<String>) {
    LazyRow(
        Modifier
            .clickable {
                selectedSummoner.value = name
                openDialog.value = true // show the remove warning dialog
            }
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            Text(text = name, fontSize = 20.sp, modifier = Modifier.padding(end = 16.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (online) Color.Green else Color.Red)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Text(text = "Hello")
}