package com.example.barterbuddy.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barterbuddy.R
import com.example.barterbuddy.data.model.UserPreference
import com.example.barterbuddy.data.remote.response.ItemStory
import com.example.barterbuddy.databinding.ActivityMainBinding
import com.example.barterbuddy.ui.ViewModelFactory
import com.example.barterbuddy.ui.adapter.StoryAdapter
import com.example.barterbuddy.ui.addstory.AddStoryActivity
import com.example.barterbuddy.ui.login.LoginPageActivity
import com.example.barterbuddy.utils.dataStore


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        binding.root.setOnRefreshListener {
            setupView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_story -> AddStoryActivity.start(this)
            R.id.logout -> showLogoutDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Anda yakin ingin Logout?")
            setPositiveButton("Ya") { _, _ ->
                logout()
            }
            setNegativeButton("Tidak", null)
            create()
            show()
        }
    }

    private fun logout() {
        mainViewModel.logout()
        LoginPageActivity.start(this)
        finish()
    }

    private fun setupView() {
        binding.root.isRefreshing = false
        mainViewModel.getStories()
    }

    private fun setupViewModel() {
        mainViewModel.listStory.observe(this) {
            setRecycleView(it)
        }
        mainViewModel.loadingScreen.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(value: Boolean) {
        binding.pbLoadingScreen.isVisible = value
        binding.rvStory.isVisible = !value
    }

    private fun setRecycleView(list: List<ItemStory>) {
        with(binding) {
            val manager = LinearLayoutManager(this@MainActivity)
            rvStory.apply {
                adapter = StoryAdapter(list)
                layoutManager = manager
            }
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            starter.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(starter)
        }
    }
}