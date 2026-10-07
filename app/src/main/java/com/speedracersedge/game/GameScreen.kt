package com.speedracersedge.game

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

class GameScreen(context: Context) : FrameLayout(context) {

    private enum class Screen {
        LOADING,
        TAP_TO_START,
        LOBBY,
        MODE_MENU
    }

    private var currentScreen = Screen.LOADING

    private val backgroundColor =
        Color.rgb(7, 12, 25)

    private var loadingProgress = 0

    private val progressBar =
        ProgressBar(
            context,
            null,
            android.R.attr.progressBarStyleHorizontal
        )

    private val loadingText =
        TextView(context)

    private val tapToStart =
        TextView(context)

    private var lobby3DView: Lobby3DView? = null

    init {
        setBackgroundColor(backgroundColor)

        showLoadingScreen()

        postDelayed(
            {
                showTapToStart()
            },
            3000
        )
    }

    // ---------------------------------------------------------
    // LOADING SCREEN
    // ---------------------------------------------------------

    private fun showLoadingScreen() {

        removeAllViews()

        currentScreen = Screen.LOADING

        val container =
            LinearLayout(context)

        container.orientation =
            LinearLayout.VERTICAL

        container.gravity =
            Gravity.CENTER

        container.setPadding(
            40,
            40,
            40,
            40
        )

        val title =
            TextView(context)

        title.text =
            "SPEED RACERS EDGE"

        title.setTextColor(Color.WHITE)

        title.textSize = 30f

        title.typeface =
            Typeface.DEFAULT_BOLD

        title.gravity =
            Gravity.CENTER

        container.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        loadingText.text =
            "INITIALIZING GAME..."

        loadingText.setTextColor(
            Color.LTGRAY
        )

        loadingText.textSize = 15f

        loadingText.gravity =
            Gravity.CENTER

        val textParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        textParams.topMargin = 30

        container.addView(
            loadingText,
            textParams
        )

        progressBar.max = 100

        progressBar.progress = 0

        val progressParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                12
            )

        progressParams.topMargin = 20

        container.addView(
            progressBar,
            progressParams
        )

        addView(
            container,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )

        animateLoading()
    }

    private fun animateLoading() {

        loadingProgress = 0

        val runnable =
            object : Runnable {

                override fun run() {

                    if (currentScreen != Screen.LOADING) {
                        return
                    }

                    loadingProgress += 2

                    progressBar.progress =
                        loadingProgress

                    if (loadingProgress < 100) {

                        postDelayed(
                            this,
                            60
                        )
                    }
                }
            }

        post(runnable)
    }

    // ---------------------------------------------------------
    // TAP TO START
    // ---------------------------------------------------------

    private fun showTapToStart() {

        removeAllViews()

        currentScreen =
            Screen.TAP_TO_START

        val container =
            FrameLayout(context)

        val title =
            TextView(context)

        title.text =
            "SPEED RACERS EDGE"

        title.textSize = 34f

        title.setTextColor(
            Color.WHITE
        )

        title.typeface =
            Typeface.DEFAULT_BOLD

        title.gravity =
            Gravity.CENTER

        container.addView(
            title,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )

        tapToStart.text =
            "TAP TO START"

        tapToStart.textSize = 18f

        tapToStart.setTextColor(
            Color.WHITE
        )

        tapToStart.gravity =
            Gravity.CENTER

        tapToStart.setPadding(
            30,
            20,
            30,
            20
        )

        tapToStart.setOnClickListener {
            showLobby()
        }

        val tapParams =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )

        tapParams.bottomMargin = 45

        container.addView(
            tapToStart,
            tapParams
        )

        addView(
            container,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    // ---------------------------------------------------------
    // PLAYER LOBBY
    // ---------------------------------------------------------

    private fun showLobby() {

        removeAllViews()

        currentScreen =
            Screen.LOBBY

        setBackgroundColor(
            Color.rgb(5, 9, 18)
        )

        // -----------------------------------------------------
        // 3D CENTER
        // -----------------------------------------------------

        lobby3DView =
            Lobby3DView(context)

        val viewerParams =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

        viewerParams.leftMargin = 180
        viewerParams.rightMargin = 230
        viewerParams.topMargin = 90
        viewerParams.bottomMargin = 80

        addView(
            lobby3DView,
            viewerParams
        )

        // -----------------------------------------------------
        // TOP LEFT
        // -----------------------------------------------------

        val profile =
            createButton(
                "👤 PROFILE"
            )

        val profileParams =
            FrameLayout.LayoutParams(
                150,
                60,
                Gravity.TOP or Gravity.START
            )

        profileParams.leftMargin = 20
        profileParams.topMargin = 15

        addView(
            profile,
            profileParams
        )

        val coins =
            createText(
                "🪙 12,450"
            )

        coins.textSize = 17f

        val coinsParams =
            FrameLayout.LayoutParams(
                150,
                60,
                Gravity.TOP or Gravity.START
            )

        coinsParams.leftMargin = 175
        coinsParams.topMargin = 15

        addView(
            coins,
            coinsParams
        )

        // -----------------------------------------------------
        // TOP RIGHT
        // -----------------------------------------------------

        val gems =
            createText(
                "💎 320"
            )

        gems.textSize = 17f

        val gemsParams =
            FrameLayout.LayoutParams(
                130,
                60,
                Gravity.TOP or Gravity.END
            )

        gemsParams.rightMargin = 80
        gemsParams.topMargin = 15

        addView(
            gems,
            gemsParams
        )

        val settings =
            createButton(
                "⚙"
            )

        settings.textSize = 22f

        val settingsParams =
            FrameLayout.LayoutParams(
                60,
                60,
                Gravity.TOP or Gravity.END
            )

        settingsParams.rightMargin = 15
        settingsParams.topMargin = 15

        addView(
            settings,
            settingsParams
        )

        // -----------------------------------------------------
        // LOBBY TITLE
        // -----------------------------------------------------

        val lobbyTitle =
            createText(
                "PLAYER LOBBY"
            )

        lobbyTitle.textSize = 23f

        lobbyTitle.typeface =
            Typeface.DEFAULT_BOLD

        lobbyTitle.gravity =
            Gravity.CENTER

        val titleParams =
            FrameLayout.LayoutParams(
                300,
                60,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL
            )

        titleParams.topMargin = 20

        addView(
            lobbyTitle,
            titleParams
        )

        // -----------------------------------------------------
        // SHOP
        // -----------------------------------------------------

        val shop =
            createButton(
                "🛒 SHOP"
            )

        val shopParams =
            FrameLayout.LayoutParams(
                150,
                65,
                Gravity.BOTTOM or Gravity.START
            )

        shopParams.leftMargin = 20
        shopParams.bottomMargin = 20

        addView(
            shop,
            shopParams
        )

        // -----------------------------------------------------
        // FRIENDS
        // -----------------------------------------------------

        val friendsTitle =
            createText(
                "FRIENDS"
            )

        friendsTitle.textSize = 18f

        friendsTitle.typeface =
            Typeface.DEFAULT_BOLD

        friendsTitle.gravity =
            Gravity.CENTER

        val friendsTitleParams =
            FrameLayout.LayoutParams(
                190,
                50,
                Gravity.TOP or Gravity.END
            )

        friendsTitleParams.rightMargin = 20
        friendsTitleParams.topMargin = 100

        addView(
            friendsTitle,
            friendsTitleParams
        )

        val friends =
            createText(
                "🟢 Alex\n\n" +
                    "🟢 Mike\n\n" +
                    "⚪ John"
            )

        friends.textSize = 16f

        friends.gravity =
            Gravity.CENTER_VERTICAL

        friends.setPadding(
            15,
            10,
            10,
            10
        )

        val friendsParams =
            FrameLayout.LayoutParams(
                190,
                220,
                Gravity.TOP or Gravity.END
            )

        friendsParams.rightMargin = 20
        friendsParams.topMargin = 150

        addView(
            friends,
            friendsParams
        )

        // -----------------------------------------------------
        // SELECT MODE
        // -----------------------------------------------------

        val selectMode =
            createButton(
                "SELECT MODE"
            )

        selectMode.setOnClickListener {
            showModeMenu()
        }

        val modeParams =
            FrameLayout.LayoutParams(
                190,
                65,
                Gravity.BOTTOM or Gravity.END
            )

        modeParams.rightMargin = 20
        modeParams.bottomMargin = 20

        addView(
            selectMode,
            modeParams
        )
    }

    // ---------------------------------------------------------
    // MODE MENU
    // ---------------------------------------------------------

    private fun showModeMenu() {

        currentScreen =
            Screen.MODE_MENU

        val overlay =
            LinearLayout(context)

        overlay.orientation =
            LinearLayout.VERTICAL

        overlay.gravity =
            Gravity.CENTER

        overlay.setPadding(
            30,
            30,
            30,
            30
        )

        overlay.setBackgroundColor(
            Color.argb(
                235,
                5,
                9,
                18
            )
        )

        val title =
            createText(
                "SELECT MODE"
            )

        title.textSize = 26f

        title.typeface =
            Typeface.DEFAULT_BOLD

        title.gravity =
            Gravity.CENTER

        overlay.addView(
            title,
            LinearLayout.LayoutParams(
                350,
                70
            )
        )

        val modes =
            arrayOf(
                "QUICK RACE",
                "ONLINE RACE",
                "HEIST",
                "PRIVATE LOBBY",
                "SPECIAL EVENTS"
            )

        for (mode in modes) {

            val button =
                createButton(mode)

            button.setOnClickListener {

                removeView(overlay)

                currentScreen =
                    Screen.LOBBY
            }

            val params =
                LinearLayout.LayoutParams(
                    350,
                    55
                )

            params.topMargin = 8

            overlay.addView(
                button,
                params
            )
        }

        val close =
            createButton(
                "CLOSE"
            )

        close.setOnClickListener {

            removeView(overlay)

            currentScreen =
                Screen.LOBBY
        }

        val closeParams =
            LinearLayout.LayoutParams(
                350,
                55
            )

        closeParams.topMargin = 20

        overlay.addView(
            close,
            closeParams
        )

        addView(
            overlay,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    // ---------------------------------------------------------
    // UI HELPERS
    // ---------------------------------------------------------

    private fun createText(
        textValue: String
    ): TextView {

        return TextView(context).apply {

            text = textValue

            setTextColor(
                Color.WHITE
            )

            gravity =
                Gravity.CENTER

            setBackgroundColor(
                Color.TRANSPARENT
            )
        }
    }

    private fun createButton(
        textValue: String
    ): Button {

        return Button(context).apply {

            text = textValue

            setTextColor(
                Color.WHITE
            )

            textSize = 14f

            isAllCaps = false

            setBackgroundColor(
                Color.rgb(
                    25,
                    32,
                    50
                )
            )
        }
    }

    override fun onDetachedFromWindow() {

        lobby3DView?.destroy()

        lobby3DView = null

        super.onDetachedFromWindow()
    }
}
