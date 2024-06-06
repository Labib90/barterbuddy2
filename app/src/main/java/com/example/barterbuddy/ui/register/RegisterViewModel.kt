package com.example.barterbuddy.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.barterbuddy.data.remote.network.ApiConfig
import com.example.barterbuddy.data.remote.payload.PayloadRegister
import com.example.barterbuddy.data.remote.response.ResponseError
import com.example.barterbuddy.data.remote.response.ResponseRegister
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _register = MutableLiveData<Boolean>()
    val register: LiveData<Boolean> = _register

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        val payload = PayloadRegister(name, email, password)
        val client = ApiConfig.getApiService().register(payload)
        _isLoading.value = true
        client.enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                _isLoading.value = false
                _register.value = response.isSuccessful
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _snackbarText.value = responseBody.message
                    }
                } else {
                    val responseBody = response.errorBody()
                    if (responseBody != null) {
                        val mapper =
                            Gson().fromJson(responseBody.string(), ResponseError::class.java)
                        _snackbarText.value = mapper.message
                        Log.e(TAG, "onFailure2: ${mapper.message}")
                    } else {
                        _snackbarText.value = response.message()
                        Log.e(TAG, "onFailure2: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                _isLoading.value = false
                _register.value = false
                _snackbarText.value = t.message ?: "Error !"
                Log.e(TAG, "onFailure: Gagal, ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}