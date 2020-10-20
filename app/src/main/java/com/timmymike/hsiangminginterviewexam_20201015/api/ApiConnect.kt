package com.timmymike.hsiangminginterviewexam_20201015.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConnect {

    var URL = "https://api.github.com/"

    private var apiService: ApiService? = null

    fun getService(): ApiService {
        if (apiService == null) {
            apiService = init()
        }
        return apiService ?: init()
    }

    private fun init(): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(100, TimeUnit.SECONDS)
            .connectTimeout(100, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}