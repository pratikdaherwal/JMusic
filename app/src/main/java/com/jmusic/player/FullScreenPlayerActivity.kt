package com.jmusic.player

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout

class FullScreenPlayerActivity : AppCompatActivity() {

    private lateinit var rootView: CoordinatorLayout
    private lateinit var albumArt: ImageView
    private lateinit var songTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var progressBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var playPauseBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private lateinit var prevBtn: ImageButton
    private lateinit var toolbar: Toolbar

    private var isPlaying = false
    private var progressAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContentView(R.layout.activity_full_screen_player)

        initViews()
        setupListeners()
        startProgressSimulation()
    }

    private fun initViews() {
        rootView = findViewById(R.id.fullScreenPlayerRoot)
        albumArt = findViewById(R.id.fullAlbumArt)
        songTitle = findViewById(R.id.fullSongTitle)
        artistName = findViewById(R.id.fullArtistName)
        progressBar = findViewById(R.id.fullProgressBar)
        currentTime = findViewById(R.id.currentTime)
        totalTime = findViewById(R.id.totalTime)
        playPauseBtn = findViewById(R.id.fullPlayPauseBtn)
        nextBtn = findViewById(R.id.nextBtn)
        prevBtn = findViewById(R.id.previousBtn)
        toolbar = findViewById(R.id.toolbar)

        // Set initial data (this would come from your music service in real app)
        songTitle.text = intent.getStringExtra("SONG_TITLE") ?: "Song Title"
        artistName.text = intent.getStringExtra("ARTIST_NAME") ?: "Artist Name"
        totalTime.text = "3:45" // Example total time
    }

    private fun setupListeners() {
        // Toolbar navigation (close button) - slide down to finish
        toolbar.setNavigationOnClickListener {
            slideDownAndFinish()
        }

        // Play/Pause button
        playPauseBtn.setOnClickListener {
            togglePlayPause()
        }

        // Next button
        nextBtn.setOnClickListener {
            // Handle next song
        }

        // Previous button
        prevBtn.setOnClickListener {
            // Handle previous song
        }

        // SeekBar listener
        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateCurrentTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Swipe down to close gesture
        setupSwipeDownGesture()
    }

    private fun setupSwipeDownGesture() {
        var startY = 0f
        var currentY = 0f

        rootView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    currentY = event.rawY
                    val deltaY = currentY - startY
                    
                    if (deltaY > 0) { // Only handle downward swipe
                        val translationY = Math.min(deltaY, resources.displayMetrics.heightPixels * 0.5f)
                        val alpha = 1.0f - (translationY / (resources.displayMetrics.heightPixels * 0.5f))
                        
                        rootView.translationY = translationY
                        rootView.alpha = alpha
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val deltaY = currentY - startY
                    
                    if (deltaY > resources.displayMetrics.heightPixels * 0.25f) {
                        // Swipe threshold reached, finish activity
                        slideDownAndFinish()
                    } else {
                        // Reset position
                        rootView.animate()
                            .translationY(0f)
                            .alpha(1.0f)
                            .setDuration(300)
                            .setInterpolator(DecelerateInterpolator())
                            .start()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun slideDownAndFinish() {
        rootView.animate()
            .translationY(resources.displayMetrics.heightPixels.toFloat())
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                finish()
                overridePendingTransition(0, R.anim.fade_out)
            }
            .start()
    }

    private fun togglePlayPause() {
        isPlaying = !isPlaying
        
        if (isPlaying) {
            playPauseBtn.setImageResource(R.drawable.ic_pause)
            startProgressSimulation()
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow)
            stopProgressSimulation()
        }
    }

    private fun startProgressSimulation() {
        stopProgressSimulation()
        
        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 225000 // 3:45 in milliseconds
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                progressBar.progress = progress
                updateCurrentTime(progress)
            }
            start()
        }
    }

    private fun stopProgressSimulation() {
        progressAnimator?.cancel()
        progressAnimator = null
    }

    private fun updateCurrentTime(progress: Int) {
        val totalSeconds = 225 // 3:45
        val currentSeconds = (progress * totalSeconds) / 100
        val minutes = currentSeconds / 60
        val seconds = currentSeconds % 60
        currentTime.text = String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressSimulation()
    }

    override fun onBackPressed() {
        slideDownAndFinish()
    }
}
