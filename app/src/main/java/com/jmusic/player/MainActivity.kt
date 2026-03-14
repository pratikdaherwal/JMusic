package com.jmusic.player

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MainActivity : AppCompatActivity() {

    // Mini Player Views
    private lateinit var miniAlbumArt: View
    private lateinit var miniSongTitle: TextView
    private lateinit var miniArtistName: TextView
    private lateinit var miniProgressBar: SeekBar
    private lateinit var miniPlayPauseBtn: ImageButton
    private lateinit var miniPlayerContainer: View
    private lateinit var searchBtn: ImageButton

    // Sample song data
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private var progress = 30
    
    // Swipe gesture variables
    private var startY = 0f
    private var currentY = 0f
    private val SWIPE_THRESHOLD = 100f // Minimum swipe distance to trigger full screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handle system bar insets
        setupSystemBars()

        // Initialize mini player views
        initMiniPlayerViews()

        // Setup mini player click listeners and swipe gestures
        setupMiniPlayerListeners()

        // Setup sample song clicks
        setupSampleSongs()
        
        // Setup search button
        setupSearchButton()

        // Start progress simulation
        startProgressSimulation()
    }

    private fun setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }
    }

    private fun initMiniPlayerViews() {
        miniPlayerContainer = findViewById(R.id.miniPlayerContainer)
        miniAlbumArt = findViewById(R.id.miniAlbumArt)
        miniSongTitle = findViewById(R.id.miniSongTitle)
        miniArtistName = findViewById(R.id.miniArtistName)
        miniProgressBar = findViewById(R.id.miniProgressBar)
        miniPlayPauseBtn = findViewById(R.id.miniPlayPauseBtn)
        searchBtn = findViewById(R.id.searchBtn)

        // Set initial progress
        miniProgressBar.progress = progress
    }

    private fun setupMiniPlayerListeners() {
        // Click on mini player to open full screen
        miniPlayerContainer.setOnClickListener {
            openFullScreenPlayer()
        }

        // Play/Pause button
        miniPlayPauseBtn.setOnClickListener {
            togglePlayPause()
        }

        // Progress bar changes
        miniProgressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    this@MainActivity.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Swipe up gesture to open full screen player
        setupSwipeUpGesture()
    }
    
    private fun setupSwipeUpGesture() {
        miniPlayerContainer.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    currentY = event.rawY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val deltaY = startY - currentY
                    
                    if (deltaY > SWIPE_THRESHOLD) {
                        // Swipe up detected, open full screen player
                        openFullScreenPlayer()
                        true
                    } else {
                        false // Let onClick handle it
                    }
                }
                else -> false
            }
        }
    }

    private fun setupSearchButton() {
        searchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            // No animation, just normal transition
        }
    }

    private fun setupSampleSongs() {
        findViewById<View>(R.id.sampleSong1).setOnClickListener {
            updateSongInfo("Sample Song 1", "Artist Name")
            isPlaying = true
            updatePlayPauseButton()
        }

        findViewById<View>(R.id.sampleSong2).setOnClickListener {
            updateSongInfo("Sample Song 2", "Artist Name")
            isPlaying = true
            updatePlayPauseButton()
        }
    }

    private fun updateSongInfo(title: String, artist: String) {
        miniSongTitle.text = title
        miniArtistName.text = artist
    }

    private fun togglePlayPause() {
        isPlaying = !isPlaying
        updatePlayPauseButton()
    }

    private fun updatePlayPauseButton() {
        if (isPlaying) {
            miniPlayPauseBtn.setImageResource(R.drawable.ic_pause)
        } else {
            miniPlayPauseBtn.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    private fun openFullScreenPlayer() {
        val intent = Intent(this, FullScreenPlayerActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_up, R.anim.fade_out)
    }

    private fun startProgressSimulation() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlaying) {
                    progress = (progress + 1) % 100
                    miniProgressBar.progress = progress
                }
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
