package com.timmymike.hsiangminginterviewexam_20200629.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/users")
    fun getUserData(@Query("since") since: Int = 0): Call<ArrayList<UserModel>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/users/{name}")
    fun getUserDetail(@Path("name") name: String): Call<ArrayList<UserDetailModel>>
}