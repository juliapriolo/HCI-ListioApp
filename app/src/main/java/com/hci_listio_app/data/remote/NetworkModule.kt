package com.hci_listio_app.data.remote

import com.hci_listio_app.BuildConfig
import com.hci_listio_app.data.remote.api.AuthApiService
import com.hci_listio_app.data.remote.api.ListApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.hci_listio_app.data.remote.api.ProductApiService
import com.hci_listio_app.data.remote.api.CategoryApiService


object NetworkModule {

    private const val DEFAULT_TIMEOUT_SECONDS = 30L

    private val loggingInterceptor: Interceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val baseUrl: String by lazy {
        if (BuildConfig.API_BASE_URL.endsWith("/")) {
            BuildConfig.API_BASE_URL
        } else {
            BuildConfig.API_BASE_URL + "/"
        }
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val listApiService: ListApiService by lazy {
        retrofit.create(ListApiService::class.java)
    }

    val productApiService: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }

    val categoryApiService: CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }


}

