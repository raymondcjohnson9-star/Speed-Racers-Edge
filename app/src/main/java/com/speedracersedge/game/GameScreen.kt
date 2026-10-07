package com.speedracersedge.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View

class GameScreen(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private enum class Screen {
        LOADING,
        TAP_TO_START,
        LOBBY
    }

    private var currentScreen = Screen.LOADING

    private var screenWidth = 0
    private var screenHeight = 0

    private var loadingProgress = 0f

    init {
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        postDelayed({
            currentScreen = Screen.TAP_TO_START
            invalidate()
        }, 2500)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        screenWidth = width
        screenHeight = height

        when (currentScreen) {
            Screen.LOADING -> drawLoadingScreen(canvas)
            Screen.TAP_TO_START -> drawTapToStart(canvas)
            Screen.LOBBY -> drawLobby(canvas)
        }
    }

    private fun drawLoadingScreen(canvas: Canvas) {
        canvas.drawColor(Color.rgb(5, 8, 15))

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 42f

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

        paint.color = Color.rgb(45, 45, 55)

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

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 13f
        paint.color = Color.LTGRAY

        canvas.drawText(
            "TAP TO START",
            screenWidth / 2f,
            screenHeight - 55f,
            paint
        )
    }

    private fun drawLobby(canvas: Canvas) {
        canvas.drawColor(Color.rgb(7, 12, 20))

        // Top-left profile
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

        // Coins
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
            "🪙 12,450",
            190f,
            49f,
            paint
        )

        // Gems
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
        paint.textAlign = Paint.Align.LEFT

        canvas.drawText(
            "💎 320",
            screenWidth - 140f,
            49f,
            paint
        )

        // Main lobby title
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 30f
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "PLAYER LOBBY",
            screenWidth / 2f,
            145f,
            paint
        )

        // Select Mode
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

        // Quick Race
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

        // Shop - bottom left
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

        // Friends panel - right side
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
            "🟢 Alex",
            friendsLeft + 25f,
            175f,
            paint
        )

        canvas.drawText(
            "🟢 Mike",
            friendsLeft + 25f,
            210f,
            paint
        )

        canvas.drawText(
            "🟢 Chris",
            friendsLeft + 25f,
            245f,
            paint
        )

        canvas.drawText(
            "⚪ John",
            friendsLeft + 25f,
            280f,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action != MotionEvent.ACTION_UP) {
            return true
        }

        when (currentScreen) {

            Screen.LOADING -> {
                // Loading screen ignores taps.
            }

            Screen.TAP_TO_START -> {
                currentScreen = Screen.LOBBY
                invalidate()
            }

            Screen.LOBBY -> {
                // Lobby buttons will be connected in the next step.
            }
        }

        return true
    }
}
