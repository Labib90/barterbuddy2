package com.example.barterbuddy.data.remote.network

import com.example.barterbuddy.BuildConfig
import com.example.barterbuddy.utils.Constant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {

    private var token = ""

    fun setToken(value: String) {
        token = value
    }

    private fun getHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            if(request.header("No-Authentication") == null) {
                if(token.isNotEmpty()) {
                    val finalToken = "Bearer $token"
                    request = request.newBuilder()
                        .addHeader(Constant.AUTH_HEADER, finalToken)
                        .build()
                }
            }
            chain.proceed(request)
        }
    }

    fun getApiService(): ApiServiceDicoding {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(getHeaderInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiServiceDicoding::class.java)
    }
}