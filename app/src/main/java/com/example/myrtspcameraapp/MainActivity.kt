package com.example.myrtspcameraapp;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    @OptIn(UnstableApi::class)
    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        val playerView = findViewById<PlayerView>(R.id.player_view)
        playerView.player = player
//        val rtspUri = "rtsp://fake.kerberos.io/stream"
        val rtspUri = "rtsp://51.17.227.45:5555/av0_0"
        val mediaItem = MediaItem.fromUri(rtspUri)
//        player?.setMediaItem(mediaItem)
        val mediaSource: MediaSource = RtspMediaSource.Factory().setForceUseRtpTcp(true).createMediaSource(mediaItem)
        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}