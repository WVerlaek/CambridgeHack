package com.wverlaek.cambridgehack.network

import com.wverlaek.cambridgehack.database.models.github.Repo
import com.wverlaek.cambridgehack.database.models.github.User
import com.wverlaek.cambridgehack.util.Constants
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by WVerl on 20-1-2018.
 */
interface GithubService {
    @GET("users/{user}?client_id=73b2c4b86a769623ca66&client_secret=01cfde68999384ad3e5f4e1967bdb5094822a136")
    fun getUser(@Path("user") user: String): Call<User>

    @GET("users/{user}/repos?client_id=73b2c4b86a769623ca66&client_secret=01cfde68999384ad3e5f4e1967bdb5094822a136")
    fun getRepos(@Path("user") user: String): Call<List<Repo>>

    companion object {
        fun create(): GithubService {
            return Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(GithubService::class.java)
        }
    }
}