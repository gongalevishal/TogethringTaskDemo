package com.ceinsys.togethringtaskdemo.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ceinsys.togethringtaskdemo.model.User
import com.ceinsys.togethringtaskdemo.model.UserDataBase
import com.ceinsys.togethringtaskdemo.model.users.UserResponse
import com.ceinsys.togethringtaskdemo.network.UserApiService
import com.ceinsys.togethringtaskdemo.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserViewModel(application: Application) : AndroidViewModel(application) {

    var users = MutableLiveData<List<User>>()
    val userLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    var listFirst: List<User> = arrayListOf()
    var listSecond: List<User> = arrayListOf()

    fun refresh() {

        if (Constants.isNetworkAvailable(getApplication())) {
            fetchFromRemote()
        } else {
            fetchFromDatabase()
        }
    }

    fun refreshBypassCache() {
        if (Constants.isNetworkAvailable(getApplication())) {
            fetchFromRemote()
        } else {
            fetchFromDatabase()
        }
    }

    private fun fetchFromRemote() {
        loading.value = true

        val userCall: Call<UserResponse>? = UserApiService.getService()?.getUsers(1)
        userCall?.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                loading.value = false
                listFirst = response.body()!!.data


//                storeUserLocally(list)
//                Toast.makeText(
//                    getApplication(),
//                    "User retrived from remote",
//                    Toast.LENGTH_SHORT
//                ).show()
                fetchFromRemoteTwo()
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userLoadError.value = true
                loading.value = false
                t.printStackTrace()
            }
        })
    }

    private fun fetchFromRemoteTwo() {
        loading.value = true

        val userCall: Call<UserResponse>? = UserApiService.getService()?.getUsers(2)
        userCall?.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                loading.value = false
                listSecond = response.body()!!.data
                val finalList: List<User> = merge(listFirst, listSecond)

                storeUserLocally(finalList)
                Toast.makeText(
                    getApplication(),
                    "User retrived from remote",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userLoadError.value = true
                loading.value = false
                t.printStackTrace()
            }
        })
    }

    private fun storeUserLocally(userList: List<User>) {
        viewModelScope.launch {
            val dao = UserDataBase(getApplication()).UserDAO()
            dao.deleteAll()
            val result = dao.insert(*userList.toTypedArray())

            var i = 0
            while (i < userList.size) {
                userList[i].UUId = result[i].toInt()
                ++i
            }

            userRetrived(userList)
        }
    }

    private fun fetchFromDatabase() {
        loading.value = true

        viewModelScope.launch {
            val user = UserDataBase(getApplication()).UserDAO().getAllUsers()
            userRetrived(user)
            Toast.makeText(getApplication(), "User retrived from database", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun userRetrived(userList: List<User>) {
        users.value = userList
        loading.value = false
    }


    fun <T> merge(first: List<T>, second: List<T>): List<T> {
        return object : ArrayList<T>() {
            init {
                addAll(first)
                addAll(second)
            }
        }
    }
}