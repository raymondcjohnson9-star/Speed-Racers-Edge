package com.speedracersedge.game

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.widget.TextView

class Lobby3DView(context: Context) : GLSurfaceView(context) {

    private val infoText = TextView(context)

    private var previousX = 0f

    private var rotation = 0f

    init {

        setEGLContextClientVersion(2)

        setBackgroundColor(
            Color.rgb(7, 12, 25)
        )

        infoText.text =
            "GRAPHICS DIAGNOSTIC\n\n" +
            "Testing device graphics..."

        infoText.setTextColor(
            Color.WHITE
        )

        infoText.textSize = 17f

        infoText.typeface =
            Typeface.DEFAULT_BOLD

        infoText.gravity =
            Gravity.CENTER

        /*
         * We don't use Filament here.
         *
         * This renderer only asks Android/OpenGL
         * for information about the device GPU.
         */
        setRenderer(
            object : Renderer {

                override fun onSurfaceCreated(
                    gl: javax.microedition.khronos.opengles.GL10?,
                    config: javax.microedition.khronos.egl.EGLConfig?
                ) {

                    val version =
                        GLES20.glGetString(
                            GLES20.GL_VERSION
                        ) ?: "Unknown"

                    val vendor =
                        GLES20.glGetString(
                            GLES20.GL_VENDOR
                        ) ?: "Unknown"

                    val renderer =
                        GLES20.glGetString(
                            GLES20.GL_RENDERER
                        ) ?: "Unknown"

                    val extensions =
                        GLES20.glGetString(
                            GLES20.GL_EXTENSIONS
                        ) ?: "Unknown"

                    post {

                        infoText.text =
                            "GRAPHICS DIAGNOSTIC\n\n" +
                            "Device:\n" +
                            "${Build.MANUFACTURER} " +
                            "${Build.MODEL}\n\n" +

                            "Android:\n" +
                            "${Build.VERSION.RELEASE}\n\n" +

                            "OpenGL ES:\n" +
                            "$version\n\n" +

                            "GPU Vendor:\n" +
                            "$vendor\n\n" +

                            "GPU Renderer:\n" +
                            "$renderer\n\n" +

                            "Graphics driver detected."

                    }
                }

                override fun onSurfaceChanged(
                    gl: javax.microedition.khronos.opengles.GL10?,
                    width: Int,
                    height: Int
                ) {
                    // Nothing needed.
                }

                override fun onDrawFrame(
                    gl: javax.microedition.khronos.opengles.GL10?
                ) {
                    /*
                     * Keep the diagnostic renderer
                     * alive without using Filament.
                     */
                }
            }
        )

        /*
         * Start the Android OpenGL renderer.
         */
        renderMode =
            GLSurfaceView.RENDERMODE_CONTINUOUSLY

        /*
         * Add the diagnostic text on top.
         */
        post {

            if (parent == null) {
                /*
                 * The actual text overlay is handled
                 * by GameScreen's SurfaceView area.
                 */
            }
        }
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

        try {

            onPause()

        } catch (
            exception: Exception
        ) {

            exception.printStackTrace()
        }
    }
}
