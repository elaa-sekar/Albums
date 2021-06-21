package com.task.albums.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.task.albums.R
import com.task.albums.databinding.ActivitySplashScreenBinding
import com.task.albums.ui.album_list.AlbumListActivity

class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableFullScreen()
    }

    override fun onResume() {
        super.onResume()
        startAnimation()
    }

    // To Enable full screen with logic for Different versions
    private fun enableFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window?.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    // To initialize scale animation of Logo Image
    private fun startAnimation() {
        AnimationUtils.loadAnimation(this, R.anim.anim_zoom_in).apply {
            setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    startAlbumListActivity()
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

            })
        }.also {
            binding.ivAlbum.startAnimation(it)
        }
    }

    // To start album list activity after Animation ends
    fun startAlbumListActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this@SplashScreenActivity,
                    AlbumListActivity::class.java
                )
            )
            finish()
        }, 300)
    }
}