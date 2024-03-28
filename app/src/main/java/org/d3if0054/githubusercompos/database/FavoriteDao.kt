package com.dicoding.githubuser.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert
    fun addToFav(favUser: Favorite)

    @Query("SELECT * FROM user_favorite")
    fun getFavUser(): LiveData<List<Favorite>>

    @Query("SELECT * FROM user_favorite WHERE user_favorite.id = :id")
    fun checkUser(id: Int): Int

    @Query("DELETE FROM user_favorite WHERE user_favorite.id = :id")
    fun removeFavUser(id: Int): Int
}