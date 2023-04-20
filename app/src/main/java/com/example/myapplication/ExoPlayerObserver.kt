package com.example.myapplication

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class ExoPlayerObserver(lifecycleOwner: LifecycleOwner, private val context: Context) :
    LifecycleObserver {

    var player: SimpleExoPlayer
    lateinit var videoProgressTracker: ProgressTracker

    private val loadControl by lazy { DefaultLoadControl() }

    init {
        player = SimpleExoPlayer.Builder(context).setLoadControl(loadControl).build()
        player.repeatMode = Player.REPEAT_MODE_ALL
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseVideo() {
        player.playWhenReady = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumeVideo() {
        player.playWhenReady = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        videoProgressTracker.unsubscribe()
        player.release()
    }
}
