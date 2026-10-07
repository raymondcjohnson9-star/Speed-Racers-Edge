package com.speedracersedge.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.VideoView

class GameScreen(context: Context) : FrameLayout(context) {

    private enum class Screen {
        LOADING,
        TAP_TO_START,
        LOBBY
    }

    private var currentScreen = Screen.LOADING

    private val backgroundView = GameCanvasView(context)
    private var videoView: VideoView? = null

    private var screenWidth = 0
    private var screenHeight = 0

    private var loadingProgress = 0f

    private val tapText = TextView(context)

    init {
        setWillNotDraw(false)

        // Canvas UI
        addView(
            backgroundView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        // Look for the future loading video.
        // The app still works normally if the video has not been added yet.
        setupLoadingVideo()

        // Small TAP TO START text
        tapText.text = "TAP TO START"
        tapText.setTextColor(Color.LTGRAY)
        tapText.textSize = 13f
        tapText.typeface = Typeface.DEFAULT_BOLD
        tapText.gravity = Gravity.CENTER
        tapText.visibility = View.GONE

        val tapParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            55
        )

        tapParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        tapParams.bottomMargin = 12

        addView(tapText, tapParams)

        // Simulated game initialization.
        postDelayed({
            finishLoading()
        }, 3000)

        postInvalidateDelayed(30)
    }

    private fun setupLoadingVideo() {

        val resourceId = resources.getIdentifier(
            "loading_chase",
            "raw",
            context.packageName
        )

        // No video yet — use the normal loading screen.
        if (resourceId == 0) {
            return
        }

        val video = VideoView(context)

        val videoParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        video.layoutParams = videoParams

        // Put video behind the Canvas UI.
        addView(video, 0)

        videoView = video

        val videoUri = Uri.parse(
            "android.resource://${context.packageName}/$resourceId"
        )

        video.setVideoURI(videoUri)

        video.setOnPreparedListener { mediaPlayer ->

            mediaPlayer.isLooping = true

            video.start()
        }

        video.setOnErrorListener { _, _, _ ->

            video.visibility = View.GONE

            true
        }
    }

    private fun finishLoading() {

        if (currentScreen != Screen.LOADING) {
            return
        }

        currentScreen = Screen.TAP_TO_START

        videoView?.stopPlayback()
        videoView?.visibility = View.GONE

        tapText.visibility = View.VISIBLE

        backgroundView.invalidate()
    }

    private fun startGame() {

        currentScreen = Screen.LOBBY

        tapText.visibility = View.GONE

        backgroundView.invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action != MotionEvent.ACTION_UP) {
            return true
        }

        when (currentScreen) {

            Screen.LOADING -> {
                // Loading screen cannot be skipped.
            }

            Screen.TAP_TO_START -> {
                startGame()
            }

            Screen.LOBBY -> {
                // Lobby controls will be connected next.
            }
        }

        return true
    }

    private inner class GameCanvasView(context: Context) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            screenWidth = width
            screenHeight = height

            when (currentScreen) {

                Screen.LOADING -> {
                    drawLoadingScreen(canvas)
                }

                Screen.TAP_TO_START -> {
                    drawTapToStart(canvas)
                }

                Screen.LOBBY -> {
                    drawLobby(canvas)
                }
            }
        }

        private fun drawLoadingScreen(canvas: Canvas) {

            /*
             * Transparent dark overlay.
             *
             * If the video exists, this lets the cinematic remain visible
             * underneath while making the game title readable.
             */
            canvas.drawColor(Color.argb(105, 0, 0, 0))

            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = 42f
            paint.color = Color.WHITE

            canvas.drawText(
                "SPEED RACERS EDGE",
                screenWidth / 2f,
                screenHeight * 0.43f,
                paint
            )

            paint.typeface = Typeface.DEFAULT
            paint.textSize = 17f
            paint.color = Color.LTGRAY

            canvas.drawText(
                "LOADING",
                screenWidth / 2f,
                screenHeight * 0.52f,
                paint
            )

            val barWidth = screenWidth * 0.35f
            val barHeight = 5f

            val left = screenWidth / 2f - barWidth / 2f
            val top = screenHeight * 0.57f

            paint.color = Color.argb(
                190,
                45,
                45,
                55
            )

            canvas.drawRoundRect(
                left,
                top,
                left + barWidth,
                top + barHeight,
                5f,
                5f,
                paint
            )

            loadingProgress += 0.01f

            if (loadingProgress > 1f) {
                loadingProgress = 1f
            }

            paint.color = Color.WHITE

            canvas.drawRoundRect(
                left,
                top,
                left + barWidth * loadingProgress,
                top + barHeight,
                5f,
                5f,
                paint
            )

            postInvalidateDelayed(30)
        }

        private fun drawTapToStart(canvas: Canvas) {

            canvas.drawColor(Color.rgb(5, 8, 15))

            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = 42f
            paint.color = Color.WHITE

            canvas.drawText(
                "SPEED RACERS EDGE",
                screenWidth / 2f,
                screenHeight * 0.42f,
                paint
            )
        }

        private fun drawLobby(canvas: Canvas) {

            canvas.drawColor(Color.rgb(7, 12, 20))

            // -------------------------
            // PROFILE
            // -------------------------

            paint.color = Color.rgb(25, 30, 40)

            canvas.drawRoundRect(
                20f,
                18f,
                165f,
                68f,
                12f,
                12f,
                paint
            )

            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.LEFT
            paint.textSize = 16f
            paint.typeface = Typeface.DEFAULT_BOLD

            canvas.drawText(
                "PROFILE",
                38f,
                49f,
                paint
            )

            // -------------------------
            // RACE COINS
            // -------------------------

            paint.color = Color.rgb(25, 30, 40)

            canvas.drawRoundRect(
                175f,
                18f,
                310f,
                68f,
                12f,
                12f,
                paint
            )

            paint.color = Color.WHITE
            paint.textSize = 15f

            canvas.drawText(
                "COINS 12,450",
                190f,
                49f,
                paint
            )

            // -------------------------
            // GEMS
            // -------------------------

            paint.color = Color.rgb(25, 30, 40)

            canvas.drawRoundRect(
                screenWidth - 155f,
                18f,
                screenWidth - 20f,
                68f,
                12f,
                12f,
                paint
            )

            paint.color = Color.WHITE

            canvas.drawText(
                "GEMS 320",
                screenWidth - 140f,
                49f,
                paint
            )

            // -------------------------
            // PLAYER LOBBY
            // -------------------------

            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 30f
            paint.typeface = Typeface.DEFAULT_BOLD

            canvas.drawText(
                "PLAYER LOBBY",
                screenWidth / 2f,
                145f,
                paint
            )

            // -------------------------
            // SELECT MODE
            // -------------------------

            val modeLeft = screenWidth / 2f - 120f
            val modeTop = 190f
            val modeRight = screenWidth / 2f + 120f
            val modeBottom = 245f

            paint.color = Color.rgb(35, 85, 170)

            canvas.drawRoundRect(
                modeLeft,
                modeTop,
                modeRight,
                modeBottom,
                14f,
                14f,
                paint
            )

            paint.color = Color.WHITE
            paint.textSize = 18f

            canvas.drawText(
                "SELECT MODE",
                screenWidth / 2f,
                224f,
                paint
            )

            // -------------------------
            // QUICK RACE
            // -------------------------

            paint.color = Color.rgb(25, 30, 40)

            canvas.drawRoundRect(
                screenWidth / 2f - 105f,
                265f,
                screenWidth / 2f + 105f,
                315f,
                12f,
                12f,
                paint
            )

            paint.color = Color.WHITE
            paint.textSize = 16f

            canvas.drawText(
                "QUICK RACE",
                screenWidth / 2f,
                297f,
                paint
            )

            // -------------------------
            // SHOP
            // -------------------------

            paint.color = Color.rgb(25, 30, 40)

            canvas.drawRoundRect(
                20f,
                screenHeight - 80f,
                135f,
                screenHeight - 25f,
                12f,
                12f,
                paint
            )

            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 15f

            canvas.drawText(
                "SHOP",
                77f,
                screenHeight - 47f,
                paint
            )

            // -------------------------
            // FRIENDS
            // -------------------------

            val friendsLeft = screenWidth - 225f

            paint.color = Color.rgb(18, 23, 32)

            canvas.drawRoundRect(
                friendsLeft,
                105f,
                screenWidth - 20f,
                screenHeight - 25f,
                14f,
                14f,
                paint
            )

            paint.color = Color.WHITE
            paint.textSize = 18f
            paint.typeface = Typeface.DEFAULT_BOLD

            canvas.drawText(
                "FRIENDS",
                friendsLeft + 100f,
                135f,
                paint
            )

            paint.typeface = Typeface.DEFAULT
            paint.textSize = 14f

            canvas.drawText(
                "ONLINE  •  Alex",
                friendsLeft + 100f,
                175f,
                paint
            )

            canvas.drawText(
                "ONLINE  •  Mike",
                friendsLeft + 100f,
                210f,
                paint
            )

            canvas.drawText(
                "ONLINE  •  Chris",
                friendsLeft + 100f,
                245f,
                paint
            )

            canvas.drawText(
                "OFFLINE •  John",
                friendsLeft + 100f,
                280f,
                paint
            )
        }
    }
}
