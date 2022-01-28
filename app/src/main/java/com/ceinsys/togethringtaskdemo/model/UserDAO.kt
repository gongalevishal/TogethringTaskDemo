package com.ceinsys.togethringtaskdemo.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDAO {

    @Insert
    suspend fun insert(vararg users: User): List<Long>

    @Query("SELECT * FROM users_data")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users_data WHERE first_name LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<User>>

    @Query("DELETE FROM users_data")
    suspend fun deleteAll()
}