package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.buffup.sdk.models.stream.StreamResultListener
import com.buffup.sdk.models.stream.StreamSummary
import com.buffup.sdk.ui.BuffView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import java.util.*

class MainActivity : AppCompatActivity(), PositionListener, Player.Listener {

    private lateinit var expPlayerObserver: ExoPlayerObserver
    private lateinit var buffView: BuffView
    private lateinit var playerView: PlayerView

    private var startDateTimestamp: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(R.style.FullscreenTheme)
        setContentView(R.layout.activity_main)
        initViews()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        expPlayerObserver = ExoPlayerObserver(this, this)
        buffView.startStream(
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

    private fun initViews() {
        buffView = findViewById(R.id.buffView)
        playerView = findViewById(R.id.playerView)
    }

    private fun startVideo() {
        val demoVideo =
            "https://buffup-public.s3.eu-west-2.amazonaws.com/video/fwc18_final_footage.mp4"
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(demoVideo))
        expPlayerObserver.player.setMediaItem(mediaItem)
        expPlayerObserver.player.prepare()
        expPlayerObserver.player.addListener(this)

        playerView.keepScreenOn = true
        playerView.player = expPlayerObserver.player
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
        buffView.setVideoProgress(position)
        startDateTimestamp?.let { startDateTimestamp ->
            val currentPlaybackTimestamp =
                startDateTimestamp + position
            buffView.provideSync(currentPlaybackTimestamp)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        buffView.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        private const val PROVIDER_ID = "buffRedAppDemo"
    }
}