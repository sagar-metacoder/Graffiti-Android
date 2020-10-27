package com.app.graffiti.dialog

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null
        fun getClient(): Retrofit? {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient()
                    .newBuilder()
                    .connectTimeout(80000, TimeUnit.SECONDS)
                    .readTimeout(80000, TimeUnit.SECONDS)
                    .writeTimeout(80000, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor).build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl("http://graffitibath.com/graffiti/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build()
            }
            return retrofit
        }
    }
}