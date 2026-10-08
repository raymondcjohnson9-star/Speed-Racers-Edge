package com.speedracersedge.game

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
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
        MODE_MENU,
        RACE_STARTING
    }

    private var currentScreen = Screen.LOADING
    private var loadingProgress = 0
    private var selectedMode = "ONLINE RACE"
    private var playerReady = false
    private var partyPlayers = 1

    private val progressBar = ProgressBar(
        context,
        null,
        android.R.attr.progressBarStyleHorizontal
    )

    private val loadingText = TextView(context)
    private val tapToStart = TextView(context)

    init {
        setBackgroundColor(Color.rgb(7, 12, 25))
        showLoadingScreen()

        postDelayed({
            showTapToStart()
        }, 3000)
    }

    // =========================================================
    // LOADING
    // =========================================================

    private fun showLoadingScreen() {

        removeAllViews()
        currentScreen = Screen.LOADING

        setBackgroundColor(Color.rgb(7, 12, 25))

        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER
        container.setPadding(40, 40, 40, 40)

        val title = createText("SPEED RACERS EDGE")
        title.textSize = 30f
        title.typeface = Typeface.DEFAULT_BOLD

        container.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        loadingText.text = "INITIALIZING GAME..."
        loadingText.textSize = 15f
        loadingText.setTextColor(Color.LTGRAY)
        loadingText.gravity = Gravity.CENTER

        val textParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        textParams.topMargin = 25

        container.addView(
            loadingText,
            textParams
        )

        progressBar.max = 100
        progressBar.progress = 0

        val progressParams = LinearLayout.LayoutParams(
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

        val runnable = object : Runnable {

            override fun run() {

                if (currentScreen != Screen.LOADING) {
                    return
                }

                loadingProgress += 2
                progressBar.progress = loadingProgress

                if (loadingProgress < 100) {
                    postDelayed(this, 60)
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
        currentScreen = Screen.TAP_TO_START

        setBackgroundColor(Color.rgb(5, 9, 18))

        val container = FrameLayout(context)

        val title = createText("SPEED RACERS EDGE")
        title.textSize = 34f
        title.typeface = Typeface.DEFAULT_BOLD

        container.addView(
            title,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )

        tapToStart.text = "TAP TO START"
        tapToStart.textSize = 18f
        tapToStart.setTextColor(Color.WHITE)
        tapToStart.gravity = Gravity.CENTER
        tapToStart.setPadding(30, 20, 30, 20)

        tapToStart.setOnClickListener {
            showLobby()
        }

        val tapParams = FrameLayout.LayoutParams(
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
    // MAIN LOBBY
    // =========================================================

    private fun showLobby() {

        removeAllViews()
        currentScreen = Screen.LOBBY

        setBackgroundColor(Color.rgb(5, 9, 18))

        // PROFILE

        val profile = createButton("👤 PROFILE")

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

        // COINS

        val coins = createText("🪙 12,450")
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

        // GEMS

        val gems = createText("💎 320")
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

        // SETTINGS

        val settings = createButton("⚙")
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

        // TITLE

        val title = createText("SPEED RACERS EDGE")
        title.textSize = 21f
        title.typeface = Typeface.DEFAULT_BOLD

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

        // GARAGE

        val garage = createPanel()

        val garageTitle = createText("YOUR GARAGE")
        garageTitle.textSize = 18f
        garageTitle.typeface = Typeface.DEFAULT_BOLD

        garage.addView(
            garageTitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                45
            )
        )

        val car = createText("🏎️")
        car.textSize = 85f

        garage.addView(
            car,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                125
            )
        )

        val carName = createText("YOUR RACING CAR")
        carName.textSize = 15f
        carName.typeface = Typeface.DEFAULT_BOLD

        garage.addView(
            carName,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                35
            )
        )

        val playerName = createText("PLAYER 1 • YOU")
        playerName.textSize = 16f

        garage.addView(
            playerName,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                35
            )
        )

        addView(
            garage,
            frameParams(
                400,
                270,
                Gravity.CENTER,
                0,
                0,
                0,
                25
            )
        )

        // PARTY

        val partyPanel = createPanel()

        val partyTitle = createText(
            "YOUR PARTY  $partyPlayers/4"
        )

        partyTitle.textSize = 18f
        partyTitle.typeface = Typeface.DEFAULT_BOLD

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

        for (i in 1 until 4) {

            if (i < partyPlayers) {

                addPartySlot(
                    partyPanel,
                    "PLAYER $i",
                    "PARTY MEMBER",
                    true
                )

            } else {

                addPartySlot(
                    partyPanel,
                    "EMPTY",
                    "INVITE FRIEND",
                    false
                )
            }
        }

        addView(
            partyPanel,
            frameParams(
                285,
                315,
                Gravity.TOP or Gravity.END,
                0,
                85,
                18,
                0
            )
        )

        // FRIENDS

        val friends = createButton("👥 FRIENDS")

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

        // SHOP

        val shop = createButton("🛒 SHOP")

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

        // READY

        val ready = createButton(
            if (playerReady) "✓ READY" else "READY"
        )

        ready.setOnClickListener {

            playerReady = !playerReady
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

        // MODE

        val selectMode = createButton(
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

        // START RACE

        val startRace = createButton("START RACE")
        startRace.textSize = 16f

        startRace.setOnClickListener {

            if (!playerReady) {

                playerReady = true
                showLobby()

            } else {

                showRaceStartingScreen()
            }
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

        val slot = LinearLayout(context)

        slot.orientation = LinearLayout.HORIZONTAL
        slot.gravity = Gravity.CENTER_VERTICAL

        slot.setPadding(
            8,
            4,
            8,
            4
        )

        val avatar = createText(
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

        val info = LinearLayout(context)
        info.orientation = LinearLayout.VERTICAL
        info.gravity = Gravity.CENTER_VERTICAL

        val nameText = createText(name)
        nameText.textSize = 14f
        nameText.gravity =
            Gravity.START or Gravity.CENTER_VERTICAL

        val statusText = createText(status)
        statusText.textSize = 11f
        statusText.gravity =
            Gravity.START or Gravity.CENTER_VERTICAL
        statusText.setTextColor(Color.LTGRAY)

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

            val invite = createButton("INVITE")
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
    // FRIENDS
    // =========================================================

    private fun showFriendsPanel() {

        val overlay = FrameLayout(context)

        overlay.setBackgroundColor(
            Color.argb(
                235,
                3,
                6,
                12
            )
        )

        val panel = createPanel()

        val title = createText("INVITE FRIENDS")
        title.textSize = 24f
        title.typeface = Typeface.DEFAULT_BOLD

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
            "ONLINE",
            overlay
        )

        addFriend(
            panel,
            "Mike",
            "ONLINE",
            overlay
        )

        addFriend(
            panel,
            "John",
            "OFFLINE",
            overlay
        )

        val close = createButton("CLOSE")

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
        status: String,
        overlay: FrameLayout
    ) {

        val row = LinearLayout(context)

        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL

        row.setPadding(
            10,
            5,
            10,
            5
        )

        val info = LinearLayout(context)

        info.orientation = LinearLayout.VERTICAL

        val nameText = createText(name)
        nameText.textSize = 16f
        nameText.typeface = Typeface.DEFAULT_BOLD
        nameText.gravity = Gravity.START

        val statusText = createText(status)
        statusText.textSize = 12f
        statusText.gravity = Gravity.START

        statusText.setTextColor(
            if (status == "ONLINE") {
                Color.GREEN
            } else {
                Color.GRAY
            }
        )

        info.addView(
            nameText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                30
            )
        )

        info.addView(
            statusText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                25
            )
        )

        row.addView(
            info,
            LinearLayout.LayoutParams(
                0,
                60,
                1f
            )
        )

        if (status == "ONLINE") {

            val invite = createButton("INVITE")
            invite.textSize = 11f

            invite.setOnClickListener {

                if (partyPlayers < 4) {

                    partyPlayers += 1

                    removeView(overlay)

                    showLobby()
                }
            }

            row.addView(
                invite,
                LinearLayout.LayoutParams(
                    85,
                    45
                )
            )
        }

        parent.addView(
            row,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                65
            )
        )
    }

    // =========================================================
    // MODE MENU
    // =========================================================

    private fun showModeMenu() {

        removeAllViews()
        currentScreen = Screen.MODE_MENU

        setBackgroundColor(Color.rgb(5, 9, 18))

        val container = LinearLayout(context)

        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER

        container.setPadding(
            35,
            35,
            35,
            35
        )

        val title = createText("SELECT MODE")
        title.textSize = 28f
        title.typeface = Typeface.DEFAULT_BOLD

        container.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
            )
        )

        val modes = listOf(
            "QUICK RACE",
            "ONLINE RACE",
            "HEIST",
            "PRIVATE LOBBY",
            "SPECIAL EVENTS"
        )

        for (mode in modes) {

            val button = createButton(mode)
            button.textSize = 15f

            button.setOnClickListener {

                selectedMode = mode
                showLobby()
            }

            val params = LinearLayout.LayoutParams(
                300,
                55
            )

            params.topMargin = 10
            params.bottomMargin = 10

            container.addView(
                button,
                params
            )
        }

        val back = createButton("BACK TO LOBBY")

        back.setOnClickListener {
            showLobby()
        }

        val backParams = LinearLayout.LayoutParams(
            300,
            55
        )

        backParams.topMargin = 20

        container.addView(
            back,
            backParams
        )

        addView(
            container,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )
    }

    // =========================================================
    // RACE STARTING
    // =========================================================

    private fun showRaceStartingScreen() {

        removeAllViews()
        currentScreen = Screen.RACE_STARTING

        setBackgroundColor(Color.rgb(3, 6, 12))

        val container = LinearLayout(context)

        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER

        container.setPadding(
            30,
            30,
            30,
            30
        )

        val title = createText("MATCH READY")
        title.textSize = 30f
        title.typeface = Typeface.DEFAULT_BOLD

        container.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
            )
        )

        val modeText = createText(selectedMode)
        modeText.textSize = 20f
        modeText.typeface = Typeface.DEFAULT_BOLD

        container.addView(
            modeText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                50
            )
        )

        val playersText = createText(
            "PLAYERS: $partyPlayers/4"
        )

        playersText.textSize = 17f

        container.addView(
            playersText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                45
            )
        )

        val status = createText("GET READY...")
        status.textSize = 22f
        status.typeface = Typeface.DEFAULT_BOLD

        container.addView(
            status,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            )
        )

        val back = createButton("BACK TO LOBBY")

        back.setOnClickListener {
            showLobby()
        }

        val backParams = LinearLayout.LayoutParams(
            220,
            55
        )

        backParams.topMargin = 30

        container.addView(
            back,
            backParams
        )

        addView(
            container,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

    private fun createPanel(): LinearLayout {

        val panel = LinearLayout(context)

        panel.orientation = LinearLayout.VERTICAL
        panel.gravity = Gravity.CENTER

        panel.setPadding(
            15,
            15,
            15,
            15
        )

        panel.background = roundedBackground(
            Color.argb(
                220,
                18,
                26,
                45
            ),
            22
        )

        return panel
    }

    private fun createText(
        text: String
    ): TextView {

        val view = TextView(context)

        view.text = text
        view.setTextColor(Color.WHITE)
        view.gravity = Gravity.CENTER
        view.textSize = 16f

        return view
    }

    private fun createButton(
        text: String
    ): Button {

        val button = Button(context)

        button.text = text
        button.setTextColor(Color.WHITE)
        button.textSize = 13f
        button.isAllCaps = false

        button.background = roundedBackground(
            Color.rgb(
                25,
                42,
                72
            ),
            18
        )

        return button
    }

    private fun roundedBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {

        val drawable = GradientDrawable()

        drawable.setColor(color)

        drawable.cornerRadius =
            radius.toFloat()

        drawable.setStroke(
            1,
            Color.argb(
                100,
                255,
                255,
                255
            )
        )

        return drawable
    }

    private fun frameParams(
        width: Int,
        height: Int,
        gravity: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): FrameLayout.LayoutParams {

        val params = FrameLayout.LayoutParams(
            width,
            height
        )

        params.gravity = gravity

        params.leftMargin = left
        params.topMargin = top
        params.rightMargin = right
        params.bottomMargin = bottom

        return params
    }
}
