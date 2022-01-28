package com.ceinsys.togethringtaskdemo.network

import com.ceinsys.togethringtaskdemo.utils.Constants.BASE_URL
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class UserApiService {
    companion object{
        var retrofit: Retrofit? = null
        var timeout = 10
        var unit = TimeUnit.MINUTES


        fun getClient(): Retrofit? {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient()?.build())
                .build()
            return retrofit
        }


        fun getUnsafeOkHttpClient(): OkHttpClient.Builder? {
            return try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory
                val dispatcher = Dispatcher()
                dispatcher.maxRequests = 1
                dispatcher.queuedCalls()
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val builder: OkHttpClient.Builder =
                    OkHttpClient.Builder().addInterceptor(interceptor).dispatcher(dispatcher)
                        .connectTimeout(timeout.toLong(), unit)
                        .writeTimeout(timeout.toLong(), unit)
                        .readTimeout(timeout.toLong(), unit)
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
                builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        fun getService(): UserApi? {
            return getClient()?.create(UserApi::class.java)
        }

    }
}