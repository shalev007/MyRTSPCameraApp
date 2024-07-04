package com.example.myrtspcameraapp

import android.content.Intent
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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView

// "rtsp://51.17.227.45:5555/av0_0"

class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var uriInput: EditText
    private lateinit var useTcpSwitch: Switch
    private lateinit var muteSwitch: Switch
    private lateinit var playButton: Button
    private lateinit var switchActivityButton: Button

    private lateinit var advancedToggle: ToggleButton
    private lateinit var advancedSettings: LinearLayout
    private lateinit var bufferMinDurationInput: EditText
    private lateinit var bufferMaxDurationInput: EditText
    private lateinit var bufferPlaybackDurationInput: EditText
    private lateinit var bufferRebufferDurationInput: EditText
    private lateinit var timeoutInput: EditText

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.player_view)
        uriInput = findViewById(R.id.uri_input)
        playButton = findViewById(R.id.play_button)

        switchActivityButton = findViewById(R.id.switch_activity)
        advancedToggle = findViewById<ToggleButton>(R.id.advanced_toggle)
        advancedSettings = findViewById<LinearLayout>(R.id.advanced_settings)
        bufferMinDurationInput = findViewById(R.id.buffer_min_duration)
        bufferMaxDurationInput = findViewById(R.id.buffer_max_duration)
        bufferPlaybackDurationInput = findViewById(R.id.buffer_playback_duration)
        bufferRebufferDurationInput = findViewById(R.id.buffer_rebuffer_duration)
        timeoutInput = findViewById(R.id.timeout)
        useTcpSwitch = findViewById(R.id.transport_switch)
        muteSwitch = findViewById(R.id.mute)

        // Set default values programmatically
        bufferMinDurationInput.setText(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS.toString())
        bufferMaxDurationInput.setText(DefaultLoadControl.DEFAULT_MAX_BUFFER_MS.toString())
        bufferPlaybackDurationInput.setText(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS.toString())
        bufferRebufferDurationInput.setText(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS.toString())
        timeoutInput.setText(RtspMediaSource.DEFAULT_TIMEOUT_MS.toString())

        advancedToggle.setOnCheckedChangeListener { _, isChecked ->
            advancedSettings.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        switchActivityButton.setOnClickListener {
            val intent = Intent(this, OnViffActivity::class.java)
            intent.putExtra("uri_input", uriInput.text.toString())
            startActivity(intent)
        }

        playButton.setOnClickListener {
            val uri = uriInput.text.toString()
            val bufferMinDuration = bufferMinDurationInput.text.toString().toIntOrNull() ?: DefaultLoadControl.DEFAULT_MIN_BUFFER_MS
            val bufferMaxDuration = bufferMaxDurationInput.text.toString().toIntOrNull() ?: DefaultLoadControl.DEFAULT_MAX_BUFFER_MS
            val bufferPlaybackDuration = bufferPlaybackDurationInput.text.toString().toIntOrNull() ?: DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS
            val bufferRebufferDuration = bufferRebufferDurationInput.text.toString().toIntOrNull() ?: DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            val timeout = timeoutInput.text.toString().toIntOrNull() ?: RtspMediaSource.DEFAULT_TIMEOUT_MS

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

                    val playerBuilder = ExoPlayer.Builder(this)
                        .setLoadControl(loadControl)

                    if (useTcpSwitch.isChecked) {
                        val trackSelector = DefaultTrackSelector(this).apply {
                            setParameters(buildUponParameters()
                                .setRendererDisabled(C.TRACK_TYPE_AUDIO, true)
                                .setRendererDisabled(C.TRACK_TYPE_METADATA, true)
                            )
                        }
                        playerBuilder.setTrackSelector(trackSelector)
                    }

                    player = playerBuilder.build()
                    playerView.player = player

                    val mediaItem = MediaItem.fromUri(uri)
                    val rtspDataSourceFactory = RtspMediaSource.Factory()
                        .setForceUseRtpTcp(useTcpSwitch.isChecked)
                        .setTimeoutMs(timeout.toLong())

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

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) {
            player.release()
        }
    }
}