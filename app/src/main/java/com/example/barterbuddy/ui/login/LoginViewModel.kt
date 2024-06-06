package com.example.barterbuddy.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barterbuddy.data.model.ModelUser
import com.example.barterbuddy.data.model.UserPreference
import com.example.barterbuddy.data.remote.network.ApiConfig
import com.example.barterbuddy.data.remote.payload.PayloadLogin
import com.example.barterbuddy.data.remote.response.ResponseError
import com.example.barterbuddy.data.remote.response.ResponseLogin
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {
    private val _login = MutableLiveData<Boolean>()
    val login: LiveData<Boolean> = _login

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private fun saveUser(user: ModelUser) {
        viewModelScope.launch {
            ApiConfig.setToken(user.tokenAuth)
            pref.saveUser(user)
        }
    }

    fun login(email: String, password: String) {
        val payload = PayloadLogin(email, password)
        val client = ApiConfig.getApiService().login(payload)
        _isLoading.value = true
        client.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val token = responseBody.loginResult?.token as String
                        _login.value = true
                        saveUser(ModelUser(token, true))
                        _snackbarText.value = responseBody.message
                    }
                } else {
                    val responseBody = response.errorBody()
                    _login.value = false
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

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                _isLoading.value = false
                _login.value = false
                _snackbarText.value = t.message ?: "Error !"
                Log.e(TAG, "onFailure: Gagal, ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}