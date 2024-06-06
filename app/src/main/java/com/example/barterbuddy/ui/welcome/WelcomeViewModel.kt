package com.example.barterbuddy.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.barterbuddy.data.model.ModelUser
import com.example.barterbuddy.data.model.UserPreference

class WelcomeViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUser(): LiveData<ModelUser> {
        return pref.getUser().asLiveData()
    }
}