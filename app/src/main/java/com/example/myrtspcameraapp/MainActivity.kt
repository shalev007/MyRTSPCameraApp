package com.example.myrtspcameraapp;

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView


class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPlayer()
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @OptIn(UnstableApi::class)
    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        val playerView = findViewById<PlayerView>(R.id.player_view)
        val uriInput = findViewById<EditText>(R.id.uri_input)
        val transportSwitch = findViewById<Switch>(R.id.transport_switch)
        val playButton = findViewById<Button>(R.id.play_button)
//        val rtspUri = "rtsp://51.17.227.45:5555/av0_0"
        playerView.player = player
        playButton.setOnClickListener {
            val uri = uriInput.text.toString()
            val useTcp = transportSwitch.isChecked

            if (uri.isNotEmpty()) {
                val mediaItem = MediaItem.fromUri(uri)
                val rtspDataSourceFactory = RtspMediaSource.Factory()
                    .setForceUseRtpTcp(useTcp)

                val mediaSource = rtspDataSourceFactory.createMediaSource(mediaItem)

                player?.setMediaSource(mediaSource)
                player?.prepare()
                player?.play()

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}