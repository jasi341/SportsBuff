package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buffup.sdk.models.stream.StreamResultListener
import com.buffup.sdk.models.stream.StreamSummary
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.util.*

class MainActivity : AppCompatActivity(), PositionListener, Player.Listener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var expPlayerObserver: ExoPlayerObserver

    private var isFullscreen = false

    private var startDateTimestamp: Long? = null
    private var isPlayerPlaying = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(R.style.FullscreenTheme)
        binding = ActivityMainBinding.inflate(layoutInflater)


        // hide the status bar and bottom navigation bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(binding.root)
        //hide the toolbar
        supportActionBar?.hide()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        expPlayerObserver = ExoPlayerObserver(this, this)
        binding.buffView.startStream(
            providerId = PROVIDER_ID,
            listener = object : StreamResultListener {
                override fun onSuccess(data: StreamSummary) {
                    startVideo()
                }

                override fun onError(t: Throwable?) {
                    Toast.makeText(this@MainActivity, "Stream doesn't exist", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        )
    }

    private fun startVideo() {
        val demoVideo =
            "https://sandbox-tappp.s3.us-east-2.amazonaws.com/content/videos/full_UTAHvTOR_480.mp4"
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(demoVideo))
        expPlayerObserver.player.setMediaItem(mediaItem)
        expPlayerObserver.player.prepare()
        binding.playerView.useController = true
        expPlayerObserver.player.addListener(this)

        binding.playerView.keepScreenOn = true
        binding.playerView.player = expPlayerObserver.player
        expPlayerObserver.videoProgressTracker = ProgressTracker(expPlayerObserver.player, this)
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        when (state) {
            Player.STATE_READY -> {
                startDateTimestamp = Date().time
            }
        }
    }

    override fun progress(position: Long) {
        binding.buffView.setVideoProgress(position)
        startDateTimestamp?.let { startDateTimestamp ->
            val currentPlaybackTimestamp =
                startDateTimestamp + position
            binding.buffView.provideSync(currentPlaybackTimestamp)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        binding.buffView.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        private const val PROVIDER_ID = "buffRedAppDemo"
        //"buffRedAppDemo"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN,isFullscreen)
        outState.putBoolean(STATE_PLAYER_PLAYING, isPlayerPlaying)
    }
}