package com.speedracersedge.game

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
        Color.rgb(5, 9, 18)

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

    private var selectedMode = "ONLINE RACE"

    private var playerReady = false

    private var partyPlayers = 1

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

    // =========================================================
    // LOADING SCREEN
    // =========================================================

    private fun showLoadingScreen() {

        removeAllViews()

        currentScreen = Screen.LOADING

        setBackgroundColor(
            Color.rgb(7, 12, 25)
        )

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

        title.setTextColor(
            Color.WHITE
        )

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

    // =========================================================
    // TAP TO START
    // =========================================================

    private fun showTapToStart() {

        removeAllViews()

        currentScreen =
            Screen.TAP_TO_START

        setBackgroundColor(
            Color.rgb(5, 9, 18)
        )

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

        tapParams.bottomMargin = 35

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

    // =========================================================
    // MAIN PARTY LOBBY
    // =========================================================

    private fun showLobby() {

        removeAllViews()

        currentScreen =
            Screen.LOBBY

        setBackgroundColor(
            Color.rgb(5, 9, 18)
        )

        // -----------------------------------------------------
        // TOP BAR
        // -----------------------------------------------------

        val profile =
            createButton(
                "👤 PROFILE"
            )

        addView(
            profile,
            frameParams(
                155,
                55,
                Gravity.TOP or Gravity.START,
                18,
                15,
                0,
                0
            )
        )

        val coins =
            createText(
                "🪙 12,450"
            )

        coins.textSize = 17f

        addView(
            coins,
            frameParams(
                150,
                55,
                Gravity.TOP or Gravity.START,
                180,
                15,
                0,
                0
            )
        )

        val gems =
            createText(
                "💎 320"
            )

        gems.textSize = 17f

        addView(
            gems,
            frameParams(
                120,
                55,
                Gravity.TOP or Gravity.END,
                0,
                15,
                75,
                0
            )
        )

        val settings =
            createButton(
                "⚙"
            )

        settings.textSize = 22f

        addView(
            settings,
            frameParams(
                60,
                55,
                Gravity.TOP or Gravity.END,
                0,
                15,
                15,
                0
            )
        )

        // -----------------------------------------------------
        // GAME TITLE
        // -----------------------------------------------------

        val title =
            createText(
                "SPEED RACERS EDGE"
            )

        title.textSize = 21f

        title.typeface =
            Typeface.DEFAULT_BOLD

        addView(
            title,
            frameParams(
                330,
                55,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                0,
                15,
                0,
                0
            )
        )

        // -----------------------------------------------------
        // CENTER GARAGE / PLAYER AREA
        // -----------------------------------------------------

        val garage =
            createPanel()

        val garageTitle =
            createText(
                "YOUR GARAGE"
            )

        garageTitle.textSize = 18f

        garageTitle.typeface =
            Typeface.DEFAULT_BOLD

        garage.addView(
            garageTitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                45
            )
        )

        val car =
            createText(
                "🏎️"
            )

        car.textSize = 85f

        car.gravity =
            Gravity.CENTER

        garage.addView(
            car,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                125
            )
        )

        val carName =
            createText(
                "YOUR RACING CAR"
            )

        carName.textSize = 15f

        carName.typeface =
            Typeface.DEFAULT_BOLD

        garage.addView(
            carName,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                35
            )
        )

        val playerName =
            createText(
                "PLAYER 1 • YOU"
            )

        playerName.textSize = 16f

        garage.addView(
            playerName,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                35
            )
        )

        val garageParams =
            FrameLayout.LayoutParams(
                400,
                270,
                Gravity.CENTER
            )

        garageParams.bottomMargin = 25

        addView(
            garage,
            garageParams
        )

        // -----------------------------------------------------
        // PARTY PANEL
        // -----------------------------------------------------

        val partyPanel =
            createPanel()

        val partyTitle =
            createText(
                "YOUR PARTY  $partyPlayers/4"
            )

        partyTitle.textSize = 18f

        partyTitle.typeface =
            Typeface.DEFAULT_BOLD

        partyPanel.addView(
            partyTitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                45
            )
        )

        addPartySlot(
            partyPanel,
            "YOU",
            "PLAYER 1 • LEADER",
            true
        )

        addPartySlot(
            partyPanel,
            "EMPTY",
            "INVITE FRIEND",
            false
        )

        addPartySlot(
            partyPanel,
            "EMPTY",
            "INVITE FRIEND",
            false
        )

        addPartySlot(
            partyPanel,
            "EMPTY",
            "INVITE FRIEND",
            false
        )

        val partyParams =
            FrameLayout.LayoutParams(
                285,
                315,
                Gravity.TOP or Gravity.END
            )

        partyParams.rightMargin = 18
        partyParams.topMargin = 85

        addView(
            partyPanel,
            partyParams
        )

        // -----------------------------------------------------
        // FRIENDS BUTTON
        // -----------------------------------------------------

        val friends =
            createButton(
                "👥 FRIENDS"
            )

        friends.setOnClickListener {
            showFriendsPanel()
        }

        addView(
            friends,
            frameParams(
                150,
                55,
                Gravity.BOTTOM or Gravity.END,
                0,
                0,
                18,
                95
            )
        )

        // -----------------------------------------------------
        // SHOP
        // -----------------------------------------------------

        val shop =
            createButton(
                "🛒 SHOP"
            )

        addView(
            shop,
            frameParams(
                150,
                60,
                Gravity.BOTTOM or Gravity.START,
                18,
                0,
                0,
                18
            )
        )

        // -----------------------------------------------------
        // READY
        // -----------------------------------------------------

        val ready =
            createButton(
                if (playerReady) {
                    "✓ READY"
                } else {
                    "READY"
                }
            )

        ready.setOnClickListener {

            playerReady =
                !playerReady

            showLobby()
        }

        addView(
            ready,
            frameParams(
                155,
                60,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                -100,
                0,
                0,
                18
            )
        )

        // -----------------------------------------------------
        // SELECT MODE
        // -----------------------------------------------------

        val selectMode =
            createButton(
                "MODE: $selectedMode"
            )

        selectMode.setOnClickListener {
            showModeMenu()
        }

        addView(
            selectMode,
            frameParams(
                210,
                60,
                Gravity.BOTTOM or Gravity.END,
                0,
                0,
                18,
                18
            )
        )

        // -----------------------------------------------------
        // START RACE
        // -----------------------------------------------------

        val startRace =
            createButton(
                "START RACE"
            )

        startRace.textSize = 16f

        startRace.setOnClickListener {

            if (!playerReady) {

                playerReady = true

                showLobby()

                return@setOnClickListener
            }

            showRaceStartingScreen()
        }

        addView(
            startRace,
            frameParams(
                190,
                60,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                100,
                0,
                0,
                18
            )
        )
    }

    // =========================================================
    // PARTY SLOT
    // =========================================================

    private fun addPartySlot(
        parent: LinearLayout,
        name: String,
        status: String,
        occupied: Boolean
    ) {

        val slot =
            LinearLayout(context)

        slot.orientation =
            LinearLayout.HORIZONTAL

        slot.gravity =
            Gravity.CENTER_VERTICAL

        slot.setPadding(
            8,
            4,
            8,
            4
        )

        val avatar =
            createText(
                if (occupied) "👤" else "＋"
            )

        avatar.textSize = 22f

        slot.addView(
            avatar,
            LinearLayout.LayoutParams(
                45,
                55
            )
        )

        val info =
            LinearLayout(context)

        info.orientation =
            LinearLayout.VERTICAL

        info.gravity =
            Gravity.CENTER_VERTICAL

        val nameText =
            createText(name)

        nameText.gravity =
            Gravity.START or Gravity.CENTER_VERTICAL

        nameText.textSize = 14f

        val statusText =
            createText(status)

        statusText.gravity =
            Gravity.START or Gravity.CENTER_VERTICAL

        statusText.textSize = 11f

        statusText.setTextColor(
            Color.LTGRAY
        )

        info.addView(
            nameText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                25
            )
        )

        info.addView(
            statusText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                22
            )
        )

        slot.addView(
            info,
            LinearLayout.LayoutParams(
                0,
                55,
                1f
            )
        )

        if (!occupied) {

            val invite =
                createButton(
                    "INVITE"
                )

            invite.textSize = 10f

            invite.setOnClickListener {
                showFriendsPanel()
            }

            slot.addView(
                invite,
                LinearLayout.LayoutParams(
                    75,
                    45
                )
            )
        }

        parent.addView(
            slot,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
            )
        )
    }

    // =========================================================
    // FRIENDS / INVITE PANEL
    // =========================================================

    private fun showFriendsPanel() {

        val overlay =
            FrameLayout(context)

        overlay.setBackgroundColor(
            Color.argb(
                235,
                3,
                6,
                12
            )
        )

        val panel =
            createPanel()

        val title =
            createText(
                "INVITE FRIENDS"
            )

        title.textSize = 24f

        title.typeface =
            Typeface.DEFAULT_BOLD

        panel.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
            )
        )

        addFriend(
            panel,
            "Alex",
            "ONLINE"
        )

        addFriend(
            panel,
            "Mike",
            "ONLINE"
        )

        addFriend(
            panel,
            "John",
            "OFFLINE"
        )

        val close =
            createButton(
                "CLOSE"
            )

        close.setOnClickListener {
            removeView(overlay)
        }

        panel.addView(
            close,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            )
        )

        overlay.addView(
            panel,
            FrameLayout.LayoutParams(
                380,
                330,
                Gravity.CENTER
            )
        )

        addView(
            overlay,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun addFriend(
        parent: LinearLayout,
        name: String,
        status: String
    ) {

        val row =
            LinearLayout(context)

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER_VERTICAL

        val friend =
            createText(
                "🟢  $name\n     $status"
            )

 
