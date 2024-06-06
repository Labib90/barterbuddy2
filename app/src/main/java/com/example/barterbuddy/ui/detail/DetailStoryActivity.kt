package com.example.barterbuddy.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.barterbuddy.data.remote.response.ItemStory
import com.example.barterbuddy.databinding.ActivityDetailStoryBinding
import com.google.android.material.snackbar.Snackbar

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    private val detailStoryViewModel by viewModels<DetailStoryViewModel>()

    private val userId by lazy { intent.getStringExtra(USER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        detailStoryViewModel.getDetailStory(userId as String)
    }


    private fun setupView() {
        Glide.with(this@DetailStoryActivity)
            .load(intent.getStringExtra(PHOTO_URL))
            .into(binding.ivDetailPhoto)
        detailStoryViewModel.getDetailStory(userId as String)
    }

    private fun setupViewModel() {
        detailStoryViewModel.detailStory.observe(this) {
            setDetailStory(it)
        }

        detailStoryViewModel.loadingScreen.observe(this) {
            showLoading(it)
        }

        detailStoryViewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled().let { text ->
                Snackbar.make(binding.root, text.toString(), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(value: Boolean) {
        with(binding) {
            pbLoadingScreen.isVisible = value
            tvDetailName.isVisible = !value
            tvDetailDescription.isVisible = !value
        }
    }

    private fun setDetailStory(story: ItemStory) {
        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
        }
    }


    companion object {
        @JvmStatic
        fun start(context: Context, photoUrl: String, userId: String, pair: Pair<View, String>) {
            val starter = Intent(context, DetailStoryActivity::class.java)
                .putExtra(USER_ID, userId)
                .putExtra(PHOTO_URL, photoUrl)

            val optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, pair)
            context.startActivity(starter, optionsCompat.toBundle())
        }

        private const val USER_ID = "userId"
        private const val PHOTO_URL = "photo_url"
    }
}