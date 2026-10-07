package com.speedracersedge.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class GameView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var screenWidth = 0
    private var screenHeight = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        screenWidth = width
        screenHeight = height

        drawBackground(canvas)
        drawGameTitle(canvas)
        drawStartButton(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(Color.rgb(7, 12, 25))
    }

    private fun drawGameTitle(canvas: Canvas) {
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 52f
        paint.isFakeBoldText = true

        canvas.drawText(
            "SPEED RACERS EDGE",
            screenWidth / 2f,
            screenHeight * 0.30f,
            paint
        )

        paint.textSize = 22f
        paint.isFakeBoldText = false

        canvas.drawText(
            "RACE. COMPETE. DOMINATE.",
            screenWidth / 2f,
            screenHeight * 0.37f,
            paint
        )
    }

    private fun drawStartButton(canvas: Canvas) {
        val buttonWidth = 500f
        val buttonHeight = 110f

        val left = screenWidth / 2f - buttonWidth / 2f
        val top = screenHeight * 0.55f
        val right = screenWidth / 2f + buttonWidth / 2f
        val bottom = top + buttonHeight

        paint.color = Color.rgb(20, 100, 220)

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            25f,
            25f,
            paint
        )

        paint.color = Color.WHITE
        paint.textSize = 32f
        paint.isFakeBoldText = true

        canvas.drawText(
            "START RACE",
            screenWidth / 2f,
            top + 70f,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_UP) {

            val buttonWidth = 500f
            val buttonHeight = 110f

            val left = screenWidth / 2f - buttonWidth / 2f
            val top = screenHeight * 0.55f
            val right = screenWidth / 2f + buttonWidth / 2f
            val bottom = top + buttonHeight

            if (
                event.x >= left &&
                event.x <= right &&
                event.y >= top &&
                event.y <= bottom
            ) {
                startRace()
            }
        }

        return true
    }

    private fun startRace() {
        invalidate()
    }
}
