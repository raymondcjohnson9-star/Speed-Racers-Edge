package com.speedracersedge.game

import android.content.Context
import android.view.MotionEvent
import android.view.SurfaceView
import com.google.android.filament.View
import com.google.android.filament.android.UiHelper
import com.google.android.filament.utils.ModelViewer
import kotlin.math.abs

class Lobby3DView(context: Context) : SurfaceView(context) {

    private val modelViewer: ModelViewer
    private val uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)

    private var previousX = 0f
    private var rotation = 0f

    init {
        modelViewer = ModelViewer(this)

        uiHelper.renderCallback = object : UiHelper.RendererCallback {

            override fun onNativeWindowChanged(
                surface: android.view.Surface
            ) {
                modelViewer.onNativeWindowChanged(surface)
            }

            override fun onDetachedFromSurface() {
                modelViewer.onDetachedFromSurface()
            }

            override fun onResized(
                width: Int,
                height: Int
            ) {
                modelViewer.onResized(width, height)
            }
        }

        uiHelper.attachTo(this)

        modelViewer.view.blendMode = View.BlendMode.OPAQUE

        // Dark cinematic background.
        modelViewer.scene.skybox =
            com.google.android.filament.Skybox.Builder()
                .color(0.015f, 0.02f, 0.04f, 1.0f)
                .build(modelViewer.engine)

        isFocusable = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                return true
            }

            MotionEvent.ACTION_MOVE -> {

                val currentX = event.x
                val deltaX = currentX - previousX

                if (abs(deltaX) > 0.1f) {
                    rotation += deltaX
                    previousX = currentX

                    // The rotation value will control the
                    // player/car model once the models are added.
                    rotation =
                        rotation.coerceIn(-180f, 180f)
                }

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
        uiHelper.detach()
        modelViewer.destroy()
    }
}
