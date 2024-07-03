package com.example.myrtspcameraapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.seanproctor.onvifcamera.OnvifDevice
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.ktor.server.plugins.contentnegotiation.*


class OnViffActivity: AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var uriInput: EditText
    private lateinit var userInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var playButton: Button
    private lateinit var switchActivityButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.onviff_activity)

        switchActivityButton = findViewById(R.id.switch_activity)
        playerView = findViewById(R.id.player_view)
        uriInput = findViewById(R.id.uri_input)
        userInput = findViewById(R.id.user)
        passwordInput = findViewById(R.id.password)
        playButton = findViewById(R.id.play_button)

        player = ExoPlayer.Builder(this)
            .build()
        playerView.player = player

        switchActivityButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("uri_input", uriInput.text.toString())
            startActivity(intent)
        }

        playButton.setOnClickListener {
            val uri = uriInput.text.toString()
            val username = userInput.text.toString()
            val password = passwordInput.text.toString()

            if (uri.isEmpty()) {
                Toast.makeText(this, "Error: Uri is empty", Toast.LENGTH_LONG).show()
            }
            val context = this
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val device = OnvifDevice.requestDevice(uri, username, password, true)

                    // Get media profiles to find which ones are streams/snapshots
                    val profiles = device.getProfiles()

                    val streamUri = profiles.firstOrNull { it.canStream() }?.let {
                        device.getStreamURI(it)
                    }

                    if (streamUri !== null && streamUri.isNotEmpty()) {
                        val mediaItem = MediaItem.fromUri(streamUri)
                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.play()
                    } else {
                        Toast.makeText(context, "Error: streamUri is missing", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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