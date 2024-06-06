package com.example.barterbuddy.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barterbuddy.data.model.UserPreference
import com.example.barterbuddy.data.remote.network.ApiConfig
import com.example.barterbuddy.data.remote.response.ItemStory
import com.example.barterbuddy.data.remote.response.StoriesResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {
    private val _listStory = MutableLiveData<List<ItemStory>>()
    val listStory: LiveData<List<ItemStory>> = _listStory

    private val _loadingScreen = MutableLiveData<Boolean>()
    val loadingScreen: LiveData<Boolean> = _loadingScreen

    fun getStories() {
        _loadingScreen.value = true
        val cilent = ApiConfig.getApiService().getStories()
        cilent.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _loadingScreen.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _listStory.value = responseBody.listStory ?: emptyList()
                        Log.d(TAG, responseBody.message.toString())
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _loadingScreen.value = false
                Log.e(TAG, "onFailure2: Gagal")
            }
        })
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}