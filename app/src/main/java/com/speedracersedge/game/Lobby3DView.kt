package com.speedracersedge.game

import android.content.Context
import android.view.Choreographer
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

    private val engine: Engine =
        Engine.create()

    private val renderer: Renderer =
        engine.createRenderer()

    private val scene: Scene =
        engine.createScene()

    private val cameraEntity: Int =
        EntityManager.get().create()

    private val camera: Camera =
        engine.createCamera(cameraEntity)

    private val view: View =
        engine.createView()

    private var swapChain: SwapChain? = null

    private var destroyed = false

    private val choreographer =
        Choreographer.getInstance()

    private val frameCallback =
        object : Choreographer.FrameCallback {

            override fun doFrame(
                frameTimeNanos: Long
            ) {

                if (destroyed) {
                    return
                }

                renderFrame(
                    frameTimeNanos
                )

                choreographer.postFrameCallback(
                    this
                )
            }
        }

    private val uiHelper =
        UiHelper(
            UiHelper.ContextErrorPolicy.DONT_CHECK
        )

    init {

        setBackgroundColor(
            android.graphics.Color.BLACK
        )

        /*
         * Connect the Filament view
         * to the scene and camera.
         */
        view.scene = scene
        view.camera = camera

        /*
         * Basic camera setup.
         */
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

        /*
         * Surface lifecycle.
         */
        uiHelper.renderCallback =
            object : UiHelper.RendererCallback {

                override fun onNativeWindowChanged(
                    surface: Surface
                ) {

                    if (destroyed) {
                        return
                    }

                    swapChain?.let {
                        engine.destroySwapChain(it)
                    }

                    swapChain =
                        engine.createSwapChain(
                            surface
                        )

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

                    if (
                        width <= 0 ||
                        height <= 0
                    ) {
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

        if (destroyed) {
            return
        }

        choreographer.removeFrameCallback(
            frameCallback
        )

        choreographer.postFrameCallback(
            frameCallback
        )
    }

    private fun stopRendering() {

        choreographer.removeFrameCallback(
            frameCallback
        )
    }

    private fun renderFrame(
        frameTimeNanos: Long
    ) {

        if (destroyed) {
            return
        }

        val currentSwapChain =
            swapChain ?: return

        try {

            if (
                renderer.beginFrame(
                    currentSwapChain,
                    frameTimeNanos
                )
            ) {

                renderer.render(
                    view
                )

                renderer.endFrame()
            }

        } catch (
            exception: Exception
        ) {

            exception.printStackTrace()

            stopRendering()
        }
    }

    fun destroy() {

        if (destroyed) {
            return
        }

        destroyed = true

        stopRendering()

        uiHelper.detach()

        swapChain?.let {
            engine.destroySwapChain(it)
        }

        swapChain = null

        engine.destroyView(
            view
        )

        engine.destroyScene(
            scene
        )

        engine.destroyRenderer(
            renderer
        )

        engine.destroyEntity(
            cameraEntity
        )

        engine.destroy()
    }
}
