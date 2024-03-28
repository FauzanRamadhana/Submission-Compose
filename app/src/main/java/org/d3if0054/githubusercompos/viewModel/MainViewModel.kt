package org.d3if0054.githubusercompos.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.d3if0054.githubusercompos.model.Items
import org.d3if0054.githubusercompos.retrofit.ApiClient
import org.d3if0054.githubusercompos.utils.Result

class MainViewModel : ViewModel() {

    private val _resultUser: MutableStateFlow<Result<List<Items>>> = MutableStateFlow(
        Result.Loading
    )
    val resultUser: StateFlow<Result<List<Items>>>
        get() = _resultUser

    fun getUser() {
        viewModelScope.launch {
            flowOf(
                ApiClient
                    .githubService
                    .getUserGithub()
            ).catch {
                Log.e("Error", it.message.toString())
            }.collect { users ->
                _resultUser.value = Result.Success(users)
            }
        }
    }

    fun searchUser(username: String) {
        _resultUser.value = Result.Loading
        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getSearchUser(
                        mapOf(
                            "q" to username
                        )
                    )
                emit(response)
            }.catch {
                Log.e("Error", it.message.toString())
                it.printStackTrace()
                _resultUser.value = Result.Error(it.message.toString())
            }.collect {
                _resultUser.value = Result.Success(it.items)
            }
        }
    }

}