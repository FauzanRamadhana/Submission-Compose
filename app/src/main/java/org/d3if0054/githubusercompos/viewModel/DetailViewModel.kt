package org.d3if0054.githubusercompos.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.githubuser.data.database.FavDatabase
import com.dicoding.githubuser.data.database.Favorite
import com.dicoding.githubuser.data.database.FavoriteDao
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.d3if0054.githubusercompos.database.FavoriteRepository
import org.d3if0054.githubusercompos.model.Items
import org.d3if0054.githubusercompos.model.ResponseDetailsUser
import org.d3if0054.githubusercompos.retrofit.ApiClient

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    var resultDetailsUser by mutableStateOf<ResponseDetailsUser?>(null)
    var resultFollowersUser by mutableStateOf<List<Items>?>(null)
    var resultFollowingUser by mutableStateOf<List<Items>?>(null)
    private val favRepo: FavoriteRepository = FavoriteRepository(application)

    private var userDao: FavoriteDao?
    private var userDb: FavDatabase?

    init {
        userDb = FavDatabase.getDatabase(application)
        userDao = userDb?.favUserDao()
    }

    fun getDetailsUser(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getDetailsUser(username)
                emit(response)
            }.catch {
                Log.e("Error", it.message.toString())
                it.printStackTrace()
            }.collect {
                resultDetailsUser = it
            }
        }
    }

    fun getFollowers(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getFollowerUser(username)
                emit(response)
            }.catch {
                Log.e("Error", it.message.toString())
                it.printStackTrace()
            }.collect {
                resultFollowersUser = it
            }
        }
    }

    fun getFollowing(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getFollowingUser(username)
                emit(response)
            }.catch {
                Log.e("Error", it.message.toString())
                it.printStackTrace()
            }.collect {
                resultFollowingUser = it
            }
        }
    }

    fun addToFavorite(user: Favorite) {
        favRepo.insert(user)
    }

    fun getAllfavorite(): LiveData<List<Favorite>> = favRepo.getAllUser()


    fun removeFromFav(id: Int) {
        favRepo.delete(id)
    }

}