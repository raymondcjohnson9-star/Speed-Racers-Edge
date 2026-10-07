package com.speedracersedge.game

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.MotionEvent
import android.widget.TextView
import android.view.Gravity
import android.view.SurfaceView

class Lobby3DView(context: Context) : SurfaceView(context) {

    private var previousX = 0f

    private var rotation = 0f

    private val message =
        TextView(context)

    init {

        setBackgroundColor(
            Color.rgb(10, 15, 28)
        )

        message.text =
            "3D VIEW TEST\n\nFilament temporarily disabled"

        message.setTextColor(
            Color.WHITE
        )

        message.textSize = 18f

        message.typeface =
            Typeface.DEFAULT_BOLD

        message.gravity =
            Gravity.CENTER

        addViewToSurface()
    }

    private fun addViewToSurface() {

        val layoutParams =
            android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )

        /*
         * SurfaceView cannot directly contain
         * child views in the normal way.
         *
         * The TextView is therefore only used
         * as a diagnostic object and the surface
         * itself remains the visible background.
         */
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        when (
            event.actionMasked
        ) {

            MotionEvent.ACTION_DOWN -> {

                previousX = event.x

                return true
            }

            MotionEvent.ACTION_MOVE -> {

                val deltaX =
                    event.x - previousX

                rotation += deltaX

                previousX = event.x

                return true
            }

            MotionEvent.ACTION_UP -> {

                performClick()

                return true
            }

            MotionEvent.ACTION_CANCEL -> {

                return true
            }
        }

        return true
    }

    override fun performClick(): Boolean {

        super.performClick()

        return true
    }

    fun destroy() {
        // Nothing to clean up.
        // Filament is intentionally disabled
        // during this diagnostic test.
    }
}
