package com.ceinsys.togethringtaskdemo.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreRepository(private val context: Context) {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "my_task"
    )

    //Create some keys
    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: DataStoreRepository? = null

        fun getInstance(context: Context): DataStoreRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = DataStoreRepository(context)
                INSTANCE = instance
                instance
            }
        }

        val USER_ID_KEY = stringPreferencesKey("USER_ID")
        val USER_NAME_KEY = stringPreferencesKey("USER_NAME")
        val USER_EMAIL_KEY = stringPreferencesKey("USER_EMAIL")
        val USER_PHOTO_KEY = stringPreferencesKey("USER_PHOTO")
        val USER_IS_LOGIN = booleanPreferencesKey("IS_LOGIN")
    }

    suspend fun saveID(id: String){
        context.dataStore.edit {
            it[USER_ID_KEY] = id
        }
    }

    suspend fun saveName(name: String) {
        context.dataStore.edit {
            it[USER_NAME_KEY] = name
        }
    }

    suspend fun saveEmail(email: String){
        context.dataStore.edit {
            it[USER_EMAIL_KEY] = email
        }
    }

    suspend fun savePhoto(photo: String){
        context.dataStore.edit {
            it[USER_PHOTO_KEY] = photo
        }
    }

    suspend fun saveLogin(isLogin: Boolean){
        context.dataStore.edit {
            it[USER_IS_LOGIN] = isLogin
        }
    }

    val readID :Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID_KEY] ?: ""
        }

    val readName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY] ?: ""
        }

    val readEmail: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL_KEY] ?: ""
        }

    val readPhoto: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PHOTO_KEY] ?: ""
        }

    val isLogin: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            preference[USER_IS_LOGIN] ?: false
        }


    suspend fun clearAllData() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
