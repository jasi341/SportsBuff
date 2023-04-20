package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buffup.sdk.models.stream.StreamResultListener
import com.buffup.sdk.models.stream.StreamSummary
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import org.koin.core.component.KoinComponent
import java.util.*

class MainActivity : AppCompatActivity(),KoinComponent,PositionListener, Player.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var expPlayerObserver: ExoPlayerObserver


    private var startDateTimestamp: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        expPlayerObserver = ExoPlayerObserver(this,this)

        binding.buffView.startStream(
            providerId = streamID,
            listener = object:StreamResultListener{
                override fun onError(t: Throwable?) {
                    Toast.makeText(this@MainActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(data: StreamSummary) {
                    startVideo()
                }

            }
        )
    }

    private fun startVideo(){
        val demoVideo = url

        val mediaItem:MediaItem = MediaItem.fromUri(Uri.parse(demoVideo))

        expPlayerObserver.player.setMediaItem(mediaItem)
        expPlayerObserver.player.prepare()

        expPlayerObserver.videoProgressTracker = ProgressTracker(
            expPlayerObserver.player,this
        )

    }

    override fun progress(position: Long) {
        binding.buffView.setVideoProgress(position)
        startDateTimestamp?.let { startDateTime ->
            val currentPlayBackTime =
                startDateTime +position
            binding.buffView.provideSync(currentPlayBackTime)
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)

        when(state){
            Player.STATE_READY ->{
                startDateTimestamp = Date().time
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        binding.buffView.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
    }
    companion object{
        private const val streamID ="buffRedAppDemo"
            //"d5ddd610-84b9-43b2-9e4f-b8edcb391e2b"

        private val url = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
    }

}