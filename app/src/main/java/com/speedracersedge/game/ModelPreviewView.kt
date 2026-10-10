
package com.speedracersedge.game

import android.content.Context
import android.graphics.Color
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.filament.Skybox
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
        setPadding(24, 24, 24, 24)
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

        // ModelViewer.onTouchEvent does not return a Boolean.
        // Return true from the listener to indicate that the touch
        // event was handled.
        surfaceView.setOnTouchListener { _, event: MotionEvent ->
            modelViewer?.onTouchEvent(event)
            true
        }
    }

    private fun initializeViewer() {
        if (initialized) return
        initialized = true

        try {
            val viewer = ModelViewer(surfaceView)
            modelViewer = viewer

            val bytes = context.assets.open(assetPath).use { input ->
                input.readBytes()
            }

            if (bytes.isEmpty()) {
                throw IllegalStateException(
                    "The GLB model file is empty."
                )
            }

            val buffer = ByteBuffer.allocateDirect(bytes.size).apply {
                put(bytes)
                flip()
            }

            viewer.loadModelGlb(buffer)
            viewer.transformToUnitCube()

            viewer.scene.skybox = Skybox.Builder()
                .color(0.025f, 0.045f, 0.085f, 1.0f)
                .build(viewer.engine)

            statusText.visibility = GONE

            if (isAttachedToWindow) {
                startRendering()
            }
        } catch (error: Exception) {
            modelViewer = null
            statusText.visibility = VISIBLE
            statusText.text = buildString {
                append("Could not load 3D model\n\n")
                append(assetPath)
                append("\n\n")
                append(error.message ?: "Unknown error")
            }
        }
    }

    private fun startRendering() {
        if (frameLoopRunning || modelViewer == null) return

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
                "3D rendering error\n\n" +
                (error.message ?: "Unknown error")
            return
        }

        if (frameLoopRunning) {
            Choreographer.getInstance().postFrameCallback(this)
        }
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
