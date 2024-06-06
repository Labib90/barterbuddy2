package com.example.barterbuddy.ui.welcome

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.barterbuddy.data.model.UserPreference
import com.example.barterbuddy.databinding.ActivityWelcomeBinding
import com.example.barterbuddy.ui.ViewModelFactory
import com.example.barterbuddy.ui.login.LoginPageActivity
import com.example.barterbuddy.ui.main.MainActivity
import com.example.barterbuddy.utils.Constant
import com.example.barterbuddy.utils.dataStore
import kotlinx.coroutines.*

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val welcomeViewModel by viewModels<WelcomeViewModel> {
            ViewModelFactory(
                UserPreference.getInstance(dataStore)
            )
        }

        var isLogin = false

        welcomeViewModel.getUser().observe(this) { model ->
            isLogin = if(model.isLogin) {
                UserPreference.setToken(model.tokenAuth)
                true
            } else {
                false
            }
        }

        activityScope.launch {
            delay(Constant.DELAY_SPLASH_SCREEN)
            runOnUiThread {
                if(isLogin) {
                    MainActivity.start(this@WelcomeActivity)
                } else {
                    LoginPageActivity.start(this@WelcomeActivity)
                }
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.coroutineContext.cancelChildren()
    }
}