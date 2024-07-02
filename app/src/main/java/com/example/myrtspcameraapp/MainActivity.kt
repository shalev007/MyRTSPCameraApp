package com.example.myrtspcameraapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.PlayerView


class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var uriInput: EditText
    private lateinit var useTcp: Switch
    private lateinit var playButton: Button

    private lateinit var advancedToggle: ToggleButton
    private lateinit var advancedSettings: LinearLayout
    private lateinit var bufferMinDurationInput: EditText
    private lateinit var bufferMaxDurationInput: EditText
    private lateinit var bufferPlaybackDurationInput: EditText
    private lateinit var bufferRebufferDurationInput: EditText
    private lateinit var timeoutDurationInput: EditText

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.player_view)
        uriInput = findViewById(R.id.uri_input)
        playButton = findViewById(R.id.play_button)

        advancedToggle = findViewById<ToggleButton>(R.id.advanced_toggle)
        advancedSettings = findViewById<LinearLayout>(R.id.advanced_settings)
        bufferMinDurationInput = findViewById(R.id.buffer_min_duration)
        bufferMaxDurationInput = findViewById(R.id.buffer_max_duration)
        bufferPlaybackDurationInput = findViewById(R.id.buffer_playback_duration)
        bufferRebufferDurationInput = findViewById(R.id.buffer_rebuffer_duration)
        timeoutDurationInput = findViewById(R.id.timeout_duration)
        useTcp = findViewById(R.id.transport_switch)

        // Set default values programmatically
        bufferMinDurationInput.setText(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS.toString())
        bufferMaxDurationInput.setText(DefaultLoadControl.DEFAULT_MAX_BUFFER_MS.toString())
        bufferPlaybackDurationInput.setText(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS.toString())
        bufferRebufferDurationInput.setText(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS.toString())
        timeoutDurationInput.setText("5000") // 5 seconds

        advancedToggle.setOnCheckedChangeListener { _, isChecked ->
            advancedSettings.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        playButton.setOnClickListener {
            val uri = uriInput.text.toString()
            val bufferMinDuration = bufferMinDurationInput.text.toString().toIntOrNull() ?: DefaultLoadControl.DEFAULT_MIN_BUFFER_MS
            val bufferMaxDuration = bufferMaxDurationInput.text.toString().toIntOrNull() ?: DefaultLoadControl.DEFAULT_MAX_BUFFER_MS
            val bufferPlaybackDuration = bufferPlaybackDurationInput.text.toString().toIntOrNull() ?: DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS
            val bufferRebufferDuration = bufferRebufferDurationInput.text.toString().toIntOrNull() ?: DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            val timeoutDuration = timeoutDurationInput.text.toString().toIntOrNull() ?: 5000

            if (uri.isNotEmpty()) {
                try {
                    val loadControl: LoadControl = DefaultLoadControl.Builder()
                        .setBufferDurationsMs(
                            bufferMinDuration,
                            bufferMaxDuration,
                            bufferPlaybackDuration,
                            bufferRebufferDuration
                        )
                        .build()

                    player = ExoPlayer.Builder(this)
                        .setLoadControl(loadControl)
                        .build()
                    playerView.player = player

                    val mediaItem = MediaItem.fromUri(uri)
                    val rtspDataSourceFactory = RtspMediaSource.Factory()
                        .setForceUseRtpTcp(useTcp.isChecked)
                        .setTimeoutMs(timeoutDuration.toLong())
                        .setDebugLoggingEnabled(true)

                    val mediaSource = rtspDataSourceFactory.createMediaSource(mediaItem)

                    player.setMediaSource(mediaSource)
                    player.prepare()
                    player.play()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

//    @SuppressLint("UseSwitchCompatOrMaterialCode")
//    @OptIn(UnstableApi::class)
//    private fun setupPlayer() {
//        val playerView = findViewById<PlayerView>(R.id.player_view)
//        val uriInput = findViewById<EditText>(R.id.uri_input)
//        val transportSwitch = findViewById<Switch>(R.id.transport_switch)
//        val playButton = findViewById<Button>(R.id.play_button)
////        val rtspUri = "rtsp://51.17.227.45:5555/av0_0"
//        // Configure low-latency settings
//        val loadControl: LoadControl = DefaultLoadControl.Builder()
//            .setBufferDurationsMs(
//                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
//                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
//                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
//                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
//            )
//            .build()
//        player = ExoPlayer.Builder(this)
//            .setLoadControl(loadControl)
//            .build()
//
//        playerView.player = player
//        playButton.setOnClickListener {
//            val uri = uriInput.text.toString()
//            val useTcp = transportSwitch.isChecked
//
//            if (uri.isNotEmpty()) {
//                val mediaItem = MediaItem.fromUri(uri)
//                val rtspDataSourceFactory = RtspMediaSource.Factory()
//                    .setTimeoutMs(5000)  // Set timeout to 5 seconds
//                    .setDebugLoggingEnabled(true)  // Enable debug logging for troubleshooting
//                    .setForceUseRtpTcp(useTcp)
//
//                val mediaSource = rtspDataSourceFactory.createMediaSource(mediaItem)
//
//                player?.setMediaSource(mediaSource)
//                player?.prepare()
//                player?.play()
//
//            }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) {
            player.release()
        }
    }
}