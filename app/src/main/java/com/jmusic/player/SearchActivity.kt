package com.jmusic.player

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Handle system bar insets
        setupSystemBars()

        // Initialize views
        initViews()

        // Setup listeners
        setupListeners()
    }

    private fun setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        backBtn = findViewById(R.id.backBtn)
    }

    private fun setupListeners() {
        // Back button - finish activity
        backBtn.setOnClickListener {
            finish()
        }

        // Search text changes (placeholder for future implementation)
        // In a real app, you would add a TextWatcher here to perform search as user types
    }

    override fun onBackPressed() {
        finish()
    }
}
