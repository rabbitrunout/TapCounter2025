package com.example.tapcounter2025

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

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
    private val topScores = mutableListOf<Int>()

    private lateinit var tapSound: MediaPlayer
    private lateinit var gameOverSound: MediaPlayer

    private val PREFS_NAME = "HighScores"
    private val SCORES_KEY = "TopScores"

    private var screenWidth = 0
    private var screenHeight = 0

    private var originalX = 0f
    private var originalY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Get screen size after layout is drawn
        val mainLayout = findViewById<View>(R.id.main)
        mainLayout.viewTreeObserver.addOnGlobalLayoutListener(
            object: ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    screenWidth = mainLayout.width
                    screenHeight = mainLayout.height
                    mainLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )

        timerText = findViewById(R.id.timerText)
        countText = findViewById(R.id.countText)
        topScoresText = findViewById(R.id.topScoresText)
        tapButton = findViewById(R.id.tapButton)
        resetButton = findViewById(R.id.resetButton)
        resetHighScoresButton = findViewById(R.id.resetHighScoresButton)

        tapButton.viewTreeObserver.addOnGlobalLayoutListener(
            object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    originalX = tapButton.x
                    originalY = tapButton.y
                    tapButton.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )

        tapSound = MediaPlayer.create(this, R.raw.tap_sound)
        gameOverSound = MediaPlayer.create(this, R.raw.game_over)

        loadTopScores()

        val totalTime = 20 * 1000L

        timer = object : CountDownTimer(totalTime, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerText.text = getString(R.string.time_left, secondsLeft)
            }

            override fun onFinish() {
                timerText.text = getString(R.string.times_up)
                tapButton.isEnabled = false
                isRunning = false
                gameOverSound.start()
                updateTopScores()
            }

        }

        tapButton.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                timer.start()
            }

            tapCount++
            tapSound.start()
            countText.text = getString(R.string.taps, tapCount)

            moveButtonRandomly()
        }

        resetButton.setOnClickListener {
            if (isRunning)
            {
                timer.cancel()
            }
            tapCount = 0
            countText.text = getString(R.string.taps_0)
            timerText.text = getString(R.string.time_left_20)
            tapButton.isEnabled = true
            isRunning = false

            // Move button back to original position
            tapButton.x = originalX
            tapButton.y = originalY
        }

        resetHighScoresButton.setOnClickListener {
            clearHighScores()
            topScoresText.text = getString(R.string.top_5_scores)
            Toast.makeText(this, getString(R.string.high_scores_cleared), Toast.LENGTH_SHORT).show()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        displayTopScores()
    }

    private fun updateTopScores() {
        topScores.add(tapCount)
        topScores.sortDescending()

        if (topScores.size > 5) {
            topScores.removeAt(topScores.lastIndex)
        }

        saveTopScores()
        displayTopScores()
    }

    private fun displayTopScores() {
        val scoreText = StringBuilder(getString(R.string.top_5_scores) + "\n")
        topScores.forEachIndexed { index, score ->
            scoreText.append("${index + 1}: $score\n")
        }
        topScoresText.text = scoreText.toString()
    }

    private fun saveTopScores() {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(SCORES_KEY, topScores.joinToString(","))
        editor.apply()
    }

    private fun loadTopScores() {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedScores = sharedPref.getString(SCORES_KEY, "")

        if (!savedScores.isNullOrEmpty()) {
            topScores.clear()
            topScores.addAll(savedScores.split(",").map { it.toInt() })
        }
    }

    private fun clearHighScores() {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove(SCORES_KEY)
        editor.apply()

        topScores.clear()
    }

    private fun moveButtonRandomly() {
        if (screenWidth == 0 || screenHeight == 0) {
            return
        }

        val buttonWidth = tapButton.width
        val buttonHeight = tapButton.height

        val maxX = screenWidth - buttonWidth
        val maxY = screenHeight - buttonHeight

        val randomX = Random.nextInt(0, maxX)
        val randomY = Random.nextInt(25, maxY)

        tapButton.x = randomX.toFloat()
        tapButton.y = randomY.toFloat()
    }

    override fun onDestroy() {
        super.onDestroy()
        tapSound.release()
        gameOverSound.release()
    }

}