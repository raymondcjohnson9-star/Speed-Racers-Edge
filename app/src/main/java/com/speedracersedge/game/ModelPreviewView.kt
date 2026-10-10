package com.speedracersedge.game

import android.content.Context
import android.graphics.Color
import android.view.Choreographer
import android.view.Gravity
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.filament.utils.ModelViewer
import java.nio.ByteBuffer

/**
 * Displays a real 3D GLB model using Google Filament.
 *
 * Used by the Inventory and Garage screens.
 */
class ModelPreviewView(
    context: Context,
    private val assetPath: String
) : FrameLayout(context), Choreographer.FrameCallback {

    private var modelViewer: ModelViewer? = null
    private var frameLoopRunning = false
    private var initialized = false

    private val surfaceView = SurfaceView(context)

    private val statusText = TextView(context).apply {
        text = "Loading 3D model..."
        textSize = 14f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setBackgroundColor(Color.rgb(8, 14, 27))
    }

    init {
        setBackgroundColor(Color.rgb(8, 14, 27))

        addView(
            surfaceView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        addView(
            statusText,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        surfaceView.setOnTouchListener { _, event ->
            modelViewer?.onTouchEvent(event) ?: false
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun initializeViewer() {
        if (initialized) return
        initialized = true

        try {
            val viewer = ModelViewer(surfaceView)
            modelViewer = viewer

            val bytes = context.assets.open(assetPath).use {
                it.readBytes()
            }

            if (bytes.isEmpty()) {
                throw IllegalStateException("The model file is empty.")
            }

            val buffer = ByteBuffer.allocateDirect(bytes.size)
                .apply {
                    put(bytes)
                    flip()
                }

            viewer.loadModelGlb(buffer)
            viewer.transformToUnitCube()

            // Add a simple skybox so the model has a visible background.
            viewer.scene.skybox =
                com.google.android.filament.Skybox.Builder()
                    .color(0.025f, 0.045f, 0.085f, 1.0f)
                    .build(viewer.engine)

            statusText.visibility = GONE

            if (isAttachedToWindow) {
                startRendering()
            }
        } catch (error: Exception) {
            statusText.visibility = VISIBLE
            statusText.text =
                "Could not load 3D model\n\n" +
                "$assetPath\n\n" +
                (error.message ?: "Unknown error")
        }
    }

    private fun startRendering() {
        if (frameLoopRunning) return

        frameLoopRunning = true
        Choreographer.getInstance().postFrameCallback(this)
    }

    private fun stopRendering() {
        frameLoopRunning = false
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!frameLoopRunning) return

        try {
            modelViewer?.render(frameTimeNanos)
        } catch (error: Exception) {
            stopRendering()
            statusText.visibility = VISIBLE
            statusText.text =
                "3D rendering error\n" +
                (error.message ?: "Unknown error")
            return
        }

        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!initialized) {
            initializeViewer()
        } else if (modelViewer != null) {
            startRendering()
        }
    }

    override fun onDetachedFromWindow() {
        stopRendering()
        super.onDetachedFromWindow()
    }
}