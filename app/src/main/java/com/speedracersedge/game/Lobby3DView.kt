package com.speedracersedge.game

import android.content.Context
import android.graphics.Color
import android.view.SurfaceView
import android.view.View
import android.widget.Toast

import com.google.android.filament.Engine

class Lobby3DView(context: Context) : SurfaceView(context) {

    private var engine: Engine? = null

    init {

        setBackgroundColor(
            Color.rgb(8, 12, 22)
        )

        testFilament()
    }

    private fun testFilament() {

        try {

            /*
             * Force Filament to use OpenGL.
             *
             * This avoids automatic backend
             * selection while we diagnose the
             * crash on the Android device.
             */
            engine =
                Engine.create(
                    Engine.Backend.OPENGL
                )

            Toast.makeText(
                context,
                "Filament OpenGL initialized",
                Toast.LENGTH_SHORT
            ).show()

        } catch (exception: Exception) {

            exception.printStackTrace()

            Toast.makeText(
                context,
                "Filament error: ${exception.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun destroy() {

        engine?.let {

            try {
                it.destroy()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }

        engine = null
    }
}
