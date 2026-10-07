package com.speedracersedge.game

import android.content.Context
import android.view.MotionEvent
import android.view.SurfaceView
import com.google.android.filament.Camera
import com.google.android.filament.Engine
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.SwapChain
import com.google.android.filament.View
import com.google.android.filament.android.UiHelper

class Lobby3DView(context: Context) : SurfaceView(context) {

    private val engine = Engine.create()
    private val renderer = engine.createRenderer()
    private val scene = engine.createScene()
    private val camera = engine.createCamera()

    private val filamentView = engine.createView()

    private var swapChain: SwapChain? = null

    private val uiHelper =
        UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)

    private var previousX = 0f
    private var rotation = 0f

    init {

        filamentView.scene = scene
        filamentView.camera = camera

        filamentView.blendMode = View.BlendMode.OPAQUE

        camera.setProjection(
            45.0,
            1.0,
            0.1,
            100.0,
            Camera.Fov.VERTICAL
        )

        camera.lookAt(
            0.0, 2.0, 7.0,
            0.0, 1.0, 0.0,
            0.0, 1.0, 0.0
        )

        uiHelper.renderCallback =
            object : UiHelper.RendererCallback {

                override fun onNativeWindowChanged(
                    surface: android.view.Surface
                ) {
                    swapChain?.let {
                        engine.destroySwapChain(it)
                    }

                    swapChain =
                        engine.createSwapChain(surface)
                }

                override fun onDetachedFromSurface() {
                    swapChain?.let {
                        engine.destroySwapChain(it)
                    }

                    swapChain = null
                }

                override fun onResized(
                    width: Int,
                    height: Int
                ) {
                    if (height > 0) {
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
            }

        uiHelper.attachTo(this)

        isFocusable = true
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

        uiHelper.detach()

        swapChain?.let {
            engine.destroySwapChain(it)
            swapChain = null
        }

        engine.destroyRenderer(renderer)
        engine.destroyView(filamentView)
        engine.destroyScene(scene)
        engine.destroyCamera(camera)

        engine.destroy()
    }
}
