package com.dicoding.githubuser.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Favorite::class], version = 1)
abstract class FavDatabase : RoomDatabase(){
    abstract fun favUserDao(): FavoriteDao


    companion object{
        @Volatile
        private var INSTANCE : FavDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): FavDatabase?{
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    FavDatabase::class.java, "userFav_database")
                    .build()
            }
            return INSTANCE
        }
    }
}