package org.d3if0054.githubusercompos.retrofit


import org.d3if0054.githubusercompos.model.Items
import org.d3if0054.githubusercompos.model.ResponseDetailsUser
import org.d3if0054.githubusercompos.model.ResponseUserGithub
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface GithubService {
    @GET("users")
    suspend fun getUserGithub(): MutableList<Items>

    @GET("users/{username}")
    suspend fun getDetailsUser(
        @Path("username") username: String,
    ): ResponseDetailsUser

    @GET("users/{username}/followers")
    suspend fun getFollowerUser(
        @Path("username") username: String,
    ): MutableList<Items>

    @GET("users/{username}/following")
    suspend fun getFollowingUser(
        @Path("username") username: String,
    ): MutableList<Items>

    @GET("search/users")
    suspend fun getSearchUser(
        @QueryMap params: Map<String, String>,
    ): ResponseUserGithub
}