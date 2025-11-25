package com.example.tapcounter2025

import android.media.MediaActionSound
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var countText: TextView
    private lateinit var topScoresText: TextView
    private lateinit var tapButton: ImageButton
    private lateinit var resetButton: Button
    private lateinit var resetHighScoresButton: Button

    private var tapCount = 0
    private var isRunning = false
    private lateinit var timer: CountDownTimer
    private  val topScores = mutableListOf<Int>()

    private lateinit var tapSound: MediaPlayer
    private lateinit var gameOverSound: MediaPlayer

    private val PREFS_NAME = "HighScores"
    private val SCORES_KEY = "TopScores"

    private var screenWidt = 0
    private var screenHight = 0

    private var originalX = 0f
    private var originalY = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        timerText = findViewById(R.id.timerText)
        countText = findViewById(R.id.countText)
        topScoresText = findViewById(R.id.topScoresText)
        tapButton = findViewById(R.id.tapButton)
        resetButton = findViewById(R.id.resetButton)
        resetHighScoresButton = findViewById(R.id.resetHighScoresButton)

        tapSound = MediaPlayer.create(this, R.raw.tap_sound)
        gameOverSound = MediaPlayer.create(this, R.raw.game_over)

        val totalTime = 20 * 1000


        tapButton.setOnClickListener {
            tapCount++
            countText.text = getString(R.string.taps, tapCount)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}