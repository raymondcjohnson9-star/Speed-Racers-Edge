package com.speedracersedge.game

import android.content.Context
import android.view.Choreographer
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceView

import com.google.android.filament.Camera
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.SwapChain
import com.google.android.filament.View
import com.google.android.filament.android.UiHelper

class Lobby3DView(context: Context) : SurfaceView(context) {

    private val engine: Engine = Engine.create()

    private val renderer: Renderer =
        engine.createRenderer()

    private val scene: Scene =
        engine.createScene()

    private val cameraEntity: Int =
        EntityManager.get().create()

    private val camera: Camera =
        engine.createCamera(cameraEntity)

    private val filamentView: View =
        engine.createView()

    private var swapChain: SwapChain? = null

    private val uiHelper =
        UiHelper(
            UiHelper.ContextErrorPolicy.DONT_CHECK
        )

    private var previousX = 0f

    private var rotation = 0f

    private var isDestroyed = false

    private val frameCallback =
        object : Choreographer.FrameCallback {

            override fun doFrame(
                frameTimeNanos: Long
            ) {

                if (isDestroyed) {
                    return
                }

                renderFrame()

                if (!isDestroyed) {
                    Choreographer
                        .getInstance()
                        .postFrameCallback(this)
                }
            }
        }

    init {

        isFocusable = true

        filamentView.scene = scene
        filamentView.camera = camera

        filamentView.blendMode =
            View.BlendMode.OPAQUE

        camera.setProjection(
            45.0,
            1.0,
            0.1,
            100.0,
            Camera.Fov.VERTICAL
        )

        camera.lookAt(
            0.0,
            2.0,
            7.0,

            0.0,
            1.0,
            0.0,

            0.0,
            1.0,
            0.0
        )

        uiHelper.renderCallback =
            object : UiHelper.RendererCallback {

                override fun onNativeWindowChanged(
                    surface: Surface
                ) {

                    if (isDestroyed) {
                        return
                    }

                    swapChain?.let {
                        engine.destroySwapChain(it)
                    }

                    swapChain =
                        engine.createSwapChain(surface)

                    startRendering()
                }

                override fun onDetachedFromSurface() {

                    stopRendering()

                    swapChain?.let {
                        engine.destroySwapChain(it)
                    }

                    swapChain = null
                }

                override fun onResized(
                    width: Int,
                    height: Int
                ) {

                    if (width <= 0 || height <= 0) {
                        return
                    }

                    camera.setProjection(
                        45.0,
                        width.toDouble() /
                            height.toDouble(),
                        0.1,
                        100.0,
                        Camera.Fov.VERTICAL
                    )
                }
            }

        uiHelper.attachTo(this)
    }

    private fun startRendering() {

        if (isDestroyed) {
            return
        }

        Choreographer
            .getInstance()
            .removeFrameCallback(
                frameCallback
            )

        Choreographer
            .getInstance()
            .postFrameCallback(
                frameCallback
            )
    }

    private fun stopRendering() {

        Choreographer
            .getInstance()
            .removeFrameCallback(
                frameCallback
            )
    }

    private fun renderFrame() {

        if (isDestroyed) {
            return
        }

        val currentSwapChain =
            swapChain ?: return

        try {

            if (renderer.beginFrame(
                    currentSwapChain
                )
            ) {

                renderer.render(
                    filamentView
                )

                renderer.endFrame()
            }

        } catch (exception: Exception) {

            exception.printStackTrace()

            stopRendering()
        }
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        when (event.actionMasked) {

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

        if (isDestroyed) {
            return
        }

        isDestroyed = true

        stopRendering()

        uiHelper.detach()

        swapChain?.let {

            engine.destroySwapChain(it)

            swapChain = null
        }

        engine.destroyRenderer(renderer)

        engine.destroyView(filamentView)

        engine.destroyScene(scene)

        engine.destroyEntity(cameraEntity)

        engine.destroy()
    }
}
