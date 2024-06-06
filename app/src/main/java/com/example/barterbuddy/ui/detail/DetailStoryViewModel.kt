package com.example.barterbuddy.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.barterbuddy.data.remote.network.ApiConfig
import com.example.barterbuddy.data.remote.response.DetailStoryResponse
import com.example.barterbuddy.data.remote.response.ItemStory
import com.example.barterbuddy.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryViewModel : ViewModel() {
    private val _detailStory = MutableLiveData<ItemStory>()
    val detailStory: LiveData<ItemStory> = _detailStory

    private val _loadingScreen = MutableLiveData<Boolean>()
    val loadingScreen: LiveData<Boolean> = _loadingScreen

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText


    fun getDetailStory(userId: String) {
        _loadingScreen.value = true

        val cilent = ApiConfig.getApiService().getDetailStory(userId)
        cilent.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                _loadingScreen.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _detailStory.value = responseBody.story ?: ItemStory()
                        Log.d(TAG, responseBody.message.toString())
                    }
                } else {
                    _snackBarText.value = Event(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _loadingScreen.value = false
                _snackBarText.value = Event("onFailure: Gagal, ${t.message ?: ""}")
                Log.e(TAG, "onFailure: Gagal")
            }
        })

    }

    companion object {
        private const val TAG = "DetailStoryViewModel"
    }
}