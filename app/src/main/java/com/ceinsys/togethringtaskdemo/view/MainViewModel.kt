package com.ceinsys.togethringtaskdemo.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ceinsys.togethringtaskdemo.data.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = DataStoreRepository.getInstance(application)
    val readID = repository.readID.asLiveData()
    val readName = repository.readName.asLiveData()
    val readEmail = repository.readEmail.asLiveData()
    val readPhoto = repository.readPhoto.asLiveData()
    val loginDone = repository.isLogin.asLiveData()

    fun saveUserID(id : String){
        viewModelScope.launch (Dispatchers.IO){
            repository.saveID(id)
        }
    }

    fun saveName(name: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveName(name)
        }

    fun saveEmail(email: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveEmail(email)
        }

    fun savePhoto(photo: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePhoto(photo)
        }

    fun saveLogin(isNightMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveLogin(isNightMode)
        }
    }

    fun clearAllStoreData(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllData()
        }
    }
}