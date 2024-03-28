package org.d3if0054.githubusercompos.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.dicoding.githubuser.data.database.FavDatabase
import com.dicoding.githubuser.data.database.Favorite
import com.dicoding.githubuser.data.database.FavoriteDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) : AndroidViewModel(application) {
    private lateinit var mFavorite: FavoriteDao
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavDatabase.getDatabase(application)
        db?.let {
            mFavorite = it.favUserDao()
        }
    }

    fun getAllUser(): LiveData<List<Favorite>> {
        return mFavorite.getFavUser()
    }

    fun insert(user: Favorite) {
        executor.execute { mFavorite.addToFav(user) }
    }

    fun delete(id: Int) {
        executor.execute { mFavorite.removeFavUser(id) }
    }
}
