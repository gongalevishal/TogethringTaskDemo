package com.ceinsys.togethringtaskdemo.network

import com.ceinsys.togethringtaskdemo.model.User
import com.ceinsys.togethringtaskdemo.model.users.UserResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {

    @GET("api/users?")
    fun getUsers(
        @Query("page") number: Int,
    ): Call<UserResponse>
}