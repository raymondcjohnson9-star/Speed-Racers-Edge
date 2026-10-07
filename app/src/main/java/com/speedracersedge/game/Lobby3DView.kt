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

    private var engine: Engine? = null
    private var renderer: Renderer? = null
    private var scene: Scene? = null
    private var camera: Camera? = null
    private var filamentView: View? = null
    private var swapChain: SwapChain? = null

    private var cameraEntity: Int = 0

    private val uiHelper =
        UiHelper(
            UiHelper.ContextErrorPolicy.DONT_CHECK
        )

    private var previousX = 0f
    private var rotation = 0f

    private var isDestroyed = false

    private val frameCallback =
        object : Choreographer.FrameCallback {

            override fun doFrame(frameTimeNanos: Long) {

                if (isDestroyed) {
                    return
                }

                renderFrame()

                Choreographer
                    .getInstance()
                    .postFrameCallback(this)
            }
        }

    init {

        isFocusable = true

        uiHelper.renderCallback =
            object : UiHelper.RendererCallback {

                override fun onNativeWindowChanged(
                    surface: Surface
                ) {

                    if (isDestroyed) {
                        return
                    }

                    val currentEngine =
                        engine ?: return

                    swapChain?.let {
                        currentEngine.destroySwapChain(it)
                    }

                    swapChain =
                        currentEngine.createSwapChain(surface)

                    startRendering()
                }

                override fun onDetachedFromSurface() {

                    stopRendering()

                    val currentEngine =
                        engine ?: return

                    swapChain?.let {
                        currentEngine.destroySwapChain(it)
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

                    camera?.setProjection(
                        45.0,
                        width.toDouble() / height.toDouble(),
                        0.1,
                        100.0,
                        Camera.Fov.VERTICAL
                    )
                }
            }

        try {

            initializeFilament()

            uiHelper.attachTo(this)

        } catch (exception: Exception) {

            exception.printStackTrace()

            cleanupFilament()
        }
    }

    private fun initializeFilament() {

        if (isDestroyed) {
            return
        }

        val newEngine =
            Engine.create()

        engine = newEngine

        renderer =
            newEngine.createRenderer()

        scene =
            newEngine.createScene()

        cameraEntity =
            EntityManager
                .get()
                .create()

        camera =
            newEngine.createCamera(cameraEntity)

        filamentView =
            newEngine.createView()

        filamentView?.scene = scene

        filamentView?.camera = camera

        filamentView?.blendMode =
            View.BlendMode.OPAQUE

        camera?.setProjection(
            45.0,
            1.0,
            0.1,
            100.0,
            Camera.Fov.VERTICAL
        )

        camera?.lookAt(
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
    }

    private fun renderFrame() {

        if (isDestroyed) {
            return
        }

        val currentRenderer =
            renderer ?: return

        val currentSwapChain =
            swapChain ?: return

        val currentView =
            filamentView ?: return

        try {

            if (currentRenderer.beginFrame(
                    currentSwapChain
                )
            ) {

                currentRenderer.render(
                    currentView,
                    0
                )

                currentRenderer.endFrame()
            }

        } catch (exception: Exception) {

            exception.printStackTrace()

            stopRendering()
        }
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

        cleanupFilament()
    }

    private fun cleanupFilament() {

        val currentEngine =
            engine ?: return

        swapChain?.let {

            try {
                currentEngine.destroySwapChain(it)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            swapChain = null
        }

        filamentView?.let {

            try {
                currentEngine.destroyView(it)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            filamentView = null
        }

        scene?.let {

            try {
                currentEngine.destroyScene(it)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            scene = null
        }

        if (cameraEntity != 0) {

            try {
                currentEngine.destroyEntity(
                    cameraEntity
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            cameraEntity = 0
        }

        renderer?.let {

            try {
                currentEngine.destroyRenderer(it)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            renderer = null
        }

        try {
            currentEngine.destroy()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        engine = null
        camera = null
    }
}
