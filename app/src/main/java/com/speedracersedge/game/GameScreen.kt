package com.speedracersedge.game

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlin.math.min

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

    private lateinit var progressBar: ProgressBar
    private lateinit var loadingText: TextView

    private var backgroundView: RacingBackgroundView? = null

    init {
        setBackgroundColor(Color.rgb(3, 6, 14))
        showLoadingScreen()
        animateLoading()
    }

    // ============================================================
    // LOADING SCREEN
    // ============================================================

    private fun showLoadingScreen() {

        removeAllViews()

        currentScreen = Screen.LOADING

        val background = RacingBackgroundView(context)
        background.showRoad = false

        backgroundView = background

        addView(
            background,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val overlay = FrameLayout(context)

        addView(
            overlay,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val title = createText("SPEED RACERS EDGE")

        title.textSize = 27f
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        title.setSingleLine(true)
        title.includeFontPadding = false
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)

        val titleParams = frameParams(
            420,
            60,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        )

        titleParams.topMargin = 90

        overlay.addView(
            title,
            titleParams
        )

        val subtitle = createText("THE ROAD HAS NO LIMIT")

        subtitle.textSize = 13f
        subtitle.setTextColor(Color.rgb(180, 205, 235))
        subtitle.gravity = Gravity.CENTER

        val subtitleParams = frameParams(
            330,
            40,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        )

        subtitleParams.topMargin = 148

        overlay.addView(
            subtitle,
            subtitleParams
        )

        loadingText = createText("LOADING GAME...")

        loadingText.textSize = 12f
        loadingText.setTextColor(Color.WHITE)
        loadingText.gravity = Gravity.CENTER

        val loadingTextParams = frameParams(
            300,
            35,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        )

        loadingTextParams.bottomMargin = 75

        overlay.addView(
            loadingText,
            loadingTextParams
        )

        progressBar = ProgressBar(
            context,
            null,
            android.R.attr.progressBarStyleHorizontal
        )

        progressBar.max = 100
        progressBar.progress = 0

        val progressParams = frameParams(
            330,
            7,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        )

        progressParams.bottomMargin = 57

        overlay.addView(
            progressBar,
            progressParams
        )
    }

    private fun animateLoading() {

        loadingProgress = 0

        val runnable = object : Runnable {

            override fun run() {

                if (currentScreen != Screen.LOADING) {
                    return
                }

                loadingProgress = min(
                    loadingProgress + 1,
                    100
                )

                progressBar.progress = loadingProgress

                loadingText.text = when {

                    loadingProgress < 20 ->
                        "LOADING GAME..."

                    loadingProgress < 40 ->
                        "LOADING VEHICLES..."

                    loadingProgress < 60 ->
                        "LOADING GARAGE..."

                    loadingProgress < 80 ->
                        "LOADING MULTIPLAYER..."

                    loadingProgress < 100 ->
                        "FINALIZING..."

                    else ->
                        "READY!"
                }

                if (loadingProgress < 100) {

                    postDelayed(
                        this,
                        60
                    )

                } else {

                    postDelayed(
                        {
                            if (currentScreen == Screen.LOADING) {
                                showTapToStart()
                            }
                        },
                        250
                    )
                }
            }
        }

        post(runnable)
    }

    // ============================================================
    // TAP TO START
    // ============================================================

    private fun showTapToStart() {

        removeAllViews()

        currentScreen = Screen.TAP_TO_START

        val background = RacingBackgroundView(context)

        background.showRoad = true

        backgroundView = background

        addView(
            background,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val overlay = FrameLayout(context)

        addView(
            overlay,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val title = createText("SPEED RACERS EDGE")

        title.textSize = 32f
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        val titleParams = frameParams(
            430,
            70,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        )

        titleParams.topMargin = 95

        overlay.addView(
            title,
            titleParams
        )

        val subtitle = createText("RACE • ESCAPE • DOMINATE")

        subtitle.textSize = 14f
        subtitle.setTextColor(Color.rgb(180, 215, 255))
        subtitle.gravity = Gravity.CENTER

        val subtitleParams = frameParams(
            350,
            40,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        )

        subtitleParams.topMargin = 165

        overlay.addView(
            subtitle,
            subtitleParams
        )

        val tap = createText("TAP TO START")

        tap.textSize = 17f
        tap.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        tap.setTextColor(Color.WHITE)
        tap.gravity = Gravity.CENTER

        val tapBackground = GradientDrawable()

        tapBackground.setColor(
            Color.argb(
                160,
                10,
                25,
                50
            )
        )

        tapBackground.setStroke(
            2,
            Color.rgb(
                50,
                145,
                255
            )
        )

        tapBackground.cornerRadius = 22f

        tap.background = tapBackground

        val tapParams = frameParams(
            220,
            55,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        )

        tapParams.bottomMargin = 80

        overlay.addView(
            tap,
            tapParams
        )

        tap.setOnClickListener {

            showLobby()
        }
    }

    // ============================================================
    // LOBBY
    // ============================================================

    private fun showLobby() {

        removeAllViews()

        currentScreen = Screen.LOBBY

        val background = RacingBackgroundView(context)

        background.showRoad = true

        backgroundView = background

        addView(
            background,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val overlay = FrameLayout(context)

        addView(
            overlay,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        // --------------------------------------------------------
        // TOP LEFT PROFILE
        // --------------------------------------------------------

        val profile = createPanel()

        val profileText = createText(
            "ADAM\nRACER LV.1"
        )

        profileText.textSize = 12f
        profileText.setTextColor(Color.WHITE)

        profile.addView(
            profileText,
            FrameLayout.LayoutParams(
                120,
                55
            )
        )

        val profileParams = frameParams(
            145,
            65,
            Gravity.TOP or Gravity.START
        )

        profileParams.leftMargin = 18
        profileParams.topMargin = 18

        overlay.addView(
            profile,
            profileParams
        )

        // --------------------------------------------------------
        // COINS
        // --------------------------------------------------------

        val coins = createText(
            "🪙  2,500"
        )

        coins.textSize = 14f
        coins.setTextColor(Color.rgb(255, 215, 80))
        coins.gravity = Gravity.CENTER

        val coinsParams = frameParams(
            115,
            45,
            Gravity.TOP or Gravity.START
        )

        coinsParams.leftMargin = 170
        coinsParams.topMargin = 20

        overlay.addView(
            coins,
            coinsParams
        )

        // --------------------------------------------------------
        // GEMS
        // --------------------------------------------------------

        val gems = createText(
            "💎  120"
        )

        gems.textSize = 14f
        gems.setTextColor(Color.rgb(90, 190, 255))
        gems.gravity = Gravity.CENTER

        val gemsParams = frameParams(
            100,
            45,
            Gravity.TOP or Gravity.START
        )

        gemsParams.leftMargin = 275
        gemsParams.topMargin = 20

        overlay.addView(
            gems,
            gemsParams
        )

        // --------------------------------------------------------
        // SETTINGS
        // --------------------------------------------------------

        val settings = createButton(
            "⚙"
        )

        val settingsParams = frameParams(
            55,
            55,
            Gravity.TOP or Gravity.END
        )

        settingsParams.rightMargin = 18
        settingsParams.topMargin = 18

        overlay.addView(
            settings,
            settingsParams
        )

        // --------------------------------------------------------
        // GAME TITLE
        // --------------------------------------------------------

        val lobbyTitle = createText(
            "SPEED RACERS EDGE"
        )

        lobbyTitle.textSize = 22f
        lobbyTitle.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        lobbyTitle.setTextColor(Color.WHITE)
        lobbyTitle.gravity = Gravity.CENTER

        val titleParams = frameParams(
            300,
            45,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        )

        titleParams.topMargin = 24

        overlay.addView(
            lobbyTitle,
            titleParams
        )

        // --------------------------------------------------------
        // CAR DISPLAY
        // --------------------------------------------------------

        val garage = createPanel()

        val car = createText("🏎️")

        car.textSize = 75f
        car.gravity = Gravity.CENTER

        garage.addView(
            car,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        val garageParams = frameParams(
            300,
            180,
            Gravity.CENTER
        )

        garageParams.topMargin = -30

        overlay.addView(
            garage,
            garageParams
        )

        // --------------------------------------------------------
        // PARTY PANEL
        // --------------------------------------------------------

        val partyPanel = createPanel()

        val partyTitle = createText(
            "PARTY  $partyPlayers/4"
        )

        partyTitle.textSize = 14f
        partyTitle.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        partyTitle.setTextColor(Color.WHITE)
        partyTitle.gravity = Gravity.CENTER

        partyPanel.addView(
            partyTitle,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        for (i in 0 until 4) {

            val slot = createText(
                if (i == 0) {
                    "YOU\nREADY"
                } else if (i < partyPlayers) {
                    "PLAYER ${i + 1}\nREADY"
                } else {
                    "+ INVITE"
                }
            )

            slot.textSize = 10f
            slot.gravity = Gravity.CENTER
            slot.setTextColor(
                if (i < partyPlayers) {
                    Color.WHITE
                } else {
                    Color.rgb(120, 175, 220)
                }
            )

            val slotBackground = GradientDrawable()

            slotBackground.setColor(
                Color.argb(
                    130,
                    8,
                    20,
                    40
                )
            )

            slotBackground.setStroke(
                1,
                Color.argb(
                    160,
                    60,
                    140,
                    220
                )
            )

            slotBackground.cornerRadius = 10f

            slot.background = slotBackground

            val slotParams = FrameLayout.LayoutParams(
                120,
                58
            )

            slotParams.leftMargin =
                12 + (i % 2) * 128

            slotParams.topMargin =
                45 + (i / 2) * 68

            partyPanel.addView(
                slot,
                slotParams
            )

            if (i >= partyPlayers) {

                slot.setOnClickListener {

                    showFriends()
                }
            }
        }

        val partyParams = frameParams(
            270,
            195,
            Gravity.TOP or Gravity.END
        )

        partyParams.rightMargin = 18
        partyParams.topMargin = 105

        overlay.addView(
            partyPanel,
            partyParams
        )

        // --------------------------------------------------------
        // SHOP
        // --------------------------------------------------------

        val shop = createButton(
            "SHOP"
        )

        val shopParams = frameParams(
            120,
            50,
            Gravity.BOTTOM or Gravity.START
        )

        shopParams.leftMargin = 18
        shopParams.bottomMargin = 20

        overlay.addView(
            shop,
            shopParams
        )

        shop.setOnClickListener {

            Toast.makeText(
                context,
                "Shop coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // --------------------------------------------------------
        // FRIENDS
        // --------------------------------------------------------

        val friends = createButton(
            "FRIENDS"
        )

        val friendsParams = frameParams(
            125,
            50,
            Gravity.BOTTOM or Gravity.START
        )

        friendsParams.leftMargin = 150
        friendsParams.bottomMargin = 20

        overlay.addView(
            friends,
            friendsParams
        )

        friends.setOnClickListener {

            showFriends()
        }

        // --------------------------------------------------------
        // SELECT MODE
        // --------------------------------------------------------

        val mode = createButton(
            "SELECT MODE"
        )

        val modeParams = frameParams(
            155,
            55,
            Gravity.BOTTOM or Gravity.END
        )

        modeParams.rightMargin = 18
        modeParams.bottomMargin = 20

        overlay.addView(
            mode,
            modeParams
        )

        mode.setOnClickListener {

            showModeMenu()
        }
    }

    // ============================================================
    // FRIENDS
    // ============================================================

    private fun showFriends() {

        val overlay = FrameLayout(context)

        addView(
            overlay,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val panel = createPanel()

        val title = createText(
            "FRIENDS"
        )

        title.textSize = 18f
        title.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        panel.addView(
            title,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                50
            )
        )

        val friends = arrayOf(
            "ALEX        ONLINE",
            "MIKE        ONLINE",
            "JOHN        OFFLINE"
        )

        for (i in friends.indices) {

            val friend = createButton(
                friends[i]
            )

            val friendParams = FrameLayout.LayoutParams(
                260,
                55
            )

            friendParams.leftMargin = 20
            friendParams.topMargin =
                60 + i * 65

            panel.addView(
                friend,
                friendParams
            )

            if (i < 2) {

                friend.setOnClickListener {

                    if (partyPlayers < 4) {

                        partyPlayers++

                        Toast.makeText(
                            context,
                            "Friend invited to party",
                            Toast.LENGTH_SHORT
                        ).show()

                        removeView(overlay)

                        showLobby()
                    }
                }
            }
        }

        val close = createButton(
            "CLOSE"
        )

        val closeParams = FrameLayout.LayoutParams(
            260,
            50
        )

        closeParams.leftMargin = 20
        closeParams.topMargin = 270

        panel.addView(
            close,
            closeParams
        )

        close.setOnClickListener {

            removeView(overlay)
        }

        val panelParams = frameParams(
            300,
            335,
            Gravity.CENTER
        )

        overlay.addView(
            panel,
            panelParams
        )
    }

    // ============================================================
    // MODE MENU
    // ============================================================

    private fun showModeMenu() {

        removeAllViews()

        currentScreen = Screen.MODE_MENU

        val background = RacingBackgroundView(context)

        background.showRoad = true

        backgroundView = background

        addView(
            background,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val overlay = FrameLayout(context)

        addView(
            overlay,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val title = createText(
            "SELECT MODE"
        )

        title.textSize = 25f
        title.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        val titleParams = frameParams(
            300,
            55,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        )

        titleParams.topMargin = 60

        overlay.addView(
            title,
            titleParams
        )

        val modes = arrayOf(
            "QUICK RACE",
            "ONLINE RACE",
            "HEIST",
            "PRIVATE LOBBY",
            "SPECIAL EVENTS"
        )

        for (i in modes.indices) {

            val button = createButton(
                modes[i]
            )

            val params = frameParams(
                280,
                55,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL
            )

            params.topMargin =
                135 + i * 65

            overlay.addView(
                button,
                params
            )

            button.setOnClickListener {

                selectedMode = modes[i]

                if (
                    selectedMode == "ONLINE RACE" ||
                    selectedMode == "QUICK RACE"
                ) {

                    showRaceStarting()

                } else {

                    Toast.makeText(
                        context,
                        "$selectedMode selected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val back = createButton(
            "BACK"
        )

        val backParams = frameParams(
            130,
            50,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        )

        backParams.bottomMargin = 25

        overlay.addView(
            back,
            backParams
        )

        back.setOnClickListener {

            showLobby()
        }
    }

    // ============================================================
    // RACE STARTING
    // ============================================================

    private fun showRaceStarting() {

        removeAllViews()

        currentScreen = Screen.RACE_STARTING

        val background = RacingBackgroundView(context)

        background.showRoad = true

        addView(
            background,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val overlay = FrameLayout(context)

        addView(
            overlay,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )

        val title = createText(
            selectedMode
        )

        title.textSize = 25f
        title.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        val titleParams = frameParams(
            350,
            55,
            Gravity.CENTER
        )

        titleParams.topMargin = -100

        overlay.addView(
            title,
            titleParams
        )

        val players = createText(
            "PLAYERS READY: $partyPlayers/4"
        )

        players.textSize = 15f
        players.setTextColor(
            Color.rgb(
                160,
                210,
                255
            )
        )

        players.gravity = Gravity.CENTER

        val playersParams = frameParams(
            300,
            45,
            Gravity.CENTER
        )

        overlay.addView(
            players,
            playersParams
        )

        val ready = createButton(
            if (playerReady) {
                "READY"
            } else {
                "READY UP"
            }
        )

        val readyParams = frameParams(
            190,
            55,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        )

        readyParams.bottomMargin = 80

        overlay.addView(
            ready,
            readyParams
        )

        ready.setOnClickListener {

            playerReady = !playerReady

            ready.text =
                if (playerReady) {
                    "READY"
                } else {
                    "READY UP"
                }
        }

        val back = createButton(
            "BACK"
        )

        val backParams = frameParams(
            120,
            45,
            Gravity.BOTTOM or Gravity.START
        )

        backParams.leftMargin = 18
        backParams.bottomMargin = 20

        overlay.addView(
            back,
            backParams
        )

        back.setOnClickListener {

            showLobby()
        }
    }

    // ============================================================
    // UI HELPERS
    // ============================================================

    private fun createPanel(): FrameLayout {

        val panel = FrameLayout(context)

        val background = GradientDrawable()

        background.setColor(
            Color.argb(
                180,
                5,
                15,
                30
            )
        )

        background.setStroke(
            1,
            Color.argb(
                160,
                70,
                145,
                220
            )
        )

        background.cornerRadius = 18f

        panel.background = background

        panel.setPadding(
            10,
            8,
            10,
            8
        )

        return panel
    }

    private fun createText(
        text: String
    ): TextView {

        val view = TextView(context)

        view.text = text

        view.setTextColor(Color.WHITE)

        view.gravity =
            Gravity.CENTER_VERTICAL

        return view
    }

    private fun createButton(
        text: String
    ): TextView {

        val button = TextView(context)

        button.text = text

        button.textSize = 13f

        button.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        button.setTextColor(Color.WHITE)

        button.gravity = Gravity.CENTER

        val background = GradientDrawable()

        background.setColor(
            Color.argb(
                190,
                8,
                24,
                48
            )
        )

        background.setStroke(
            2,
            Color.argb(
                190,
                55,
                145,
                255
            )
        )

        background.cornerRadius = 16f

        button.background = background

        button.isClickable = true

        return button
    }

    private fun roundedBackground(
        color: Int,
        strokeColor: Int,
        radius: Float = 16f
    ): GradientDrawable {

        val drawable = GradientDrawable()

        drawable.setColor(color)

        drawable.setStroke(
            2,
            strokeColor
        )

        drawable.cornerRadius = radius

        return drawable
    }

    private fun frameParams(
        width: Int,
        height: Int,
        gravity: Int
    ): FrameLayout.LayoutParams {

        val params =
            FrameLayout.LayoutParams(
                width,
                height
            )

        params.gravity = gravity

        return params
    }

    // ============================================================
    // BRIDGE / CITY / WATER BACKGROUND
    // ============================================================

    private class RacingBackgroundView(
        context: Context
    ) : View(context) {

        private val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private val roadPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private val glowPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private val linePaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private val waterPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        var showRoad = true

        private var animationOffset = 0f

        init {

            setLayerType(
                View.LAYER_TYPE_SOFTWARE,
                null
            )

            post(
                object : Runnable {

                    override fun run() {

                        animationOffset += 2.8f

                        if (animationOffset > 100f) {
                            animationOffset = 0f
                        }

                        invalidate()

                        postDelayed(
                            this,
                            35
                        )
                    }
                }
            )
        }

        override fun onDraw(
            canvas: Canvas
        ) {

            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            if (w <= 0f || h <= 0f) {
                return
            }

            // BRIGHT NIGHT SKY

            paint.shader =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    h * 0.65f,
                    Color.rgb(
                        8,
                        22,
                        48
                    ),
                    Color.rgb(
                        20,
                        48,
                        82
                    ),
                    Shader.TileMode.CLAMP
                )

            canvas.drawRect(
                0f,
                0f,
                w,
                h * 0.68f,
                paint
            )

            paint.shader = null

            // CITY GLOW

            glowPaint.shader =
                LinearGradient(
                    0f,
                    h * 0.15f,
                    w,
                    h * 0.55f,
                    Color.argb(
                        80,
                        30,
                        130,
                        255
                    ),
                    Color.argb(
                        110,
                        255,
                        50,
                        65
                    ),
                    Shader.TileMode.CLAMP
                )

            canvas.drawRect(
                0f,
                0f,
                w,
                h * 0.65f,
                glowPaint
            )

            glowPaint.shader = null

                       glowPaint.shader = null

            // ====================================================
            // DISTANT CITY
            // ====================================================

            val cityBase = h * 0.53f

            var x = 0f
            var buildingIndex = 0

            while (x < w) {

                val buildingWidth =
                    when (buildingIndex % 4) {
                        0 -> 34f
                        1 -> 48f
                        2 -> 28f
                        else -> 60f
                    }

                val buildingHeight =
                    when (buildingIndex % 5) {
                        0 -> h * 0.20f
                        1 -> h * 0.29f
                        2 -> h * 0.16f
                        3 -> h * 0.25f
                        else -> h * 0.34f
                    }

                val top =
                    cityBase - buildingHeight

                paint.color =
                    when (buildingIndex % 3) {
                        0 ->
                            Color.rgb(
                                13,
                                28,
                                52
                            )

                        1 ->
                            Color.rgb(
                                18,
                                34,
                                60
                            )

                        else ->
                            Color.rgb(
                                10,
                                25,
                                48
                            )
                    }

                canvas.drawRect(
                    x,
                    top,
                    x + buildingWidth,
                    cityBase,
                    paint
                )

                // Rooftop light

                paint.color =
                    Color.argb(
                        210,
                        255,
                        55,
                        65
                    )

                canvas.drawRect(
                    x + buildingWidth * 0.25f,
                    top - 4f,
                    x + buildingWidth * 0.75f,
                    top,
                    paint
                )

                // Building windows

                var wy = top + 12f

                while (wy < cityBase - 8f) {

                    var wx = x + 7f

                    while (
                        wx <
                        x + buildingWidth - 6f
                    ) {

                        if (
                            (
                                wx.toInt() +
                                    wy.toInt() +
                                    buildingIndex
                            ) % 4 != 0
                        ) {

                            paint.color =
                                if (
                                    (
                                        buildingIndex +
                                            wx.toInt()
                                    ) % 3 == 0
                                ) {

                                    Color.argb(
                                        230,
                                        255,
                                        190,
                                        75
                                    )

                                } else {

                                    Color.argb(
                                        210,
                                        70,
                                        170,
                                        255
                                    )
                                }

                            canvas.drawRect(
                                wx,
                                wy,
                                wx + 5f,
                                wy + 4f,
                                paint
                            )
                        }

                        wx += 12f
                    }

                    wy += 15f
                }

                x += buildingWidth + 5f
                buildingIndex++
            }

            // ====================================================
            // WATER-ONLY BACKGROUND
            // ====================================================

            if (!showRoad) {

                drawWater(
                    canvas,
                    w,
                    h,
                    cityBase
                )

                return
            }

            // ====================================================
            // WATER ON BOTH SIDES OF THE BRIDGE
            // ====================================================

            val horizonY = h * 0.54f

            waterPaint.shader =
                LinearGradient(
                    0f,
                    horizonY,
                    0f,
                    h,
                    Color.rgb(
                        18,
                        65,
                        105
                    ),
                    Color.rgb(
                        4,
                        25,
                        48
                    ),
                    Shader.TileMode.CLAMP
                )

            // Left side water

            canvas.drawRect(
                0f,
                horizonY,
                w * 0.38f,
                h,
                waterPaint
            )

            // Right side water

            canvas.drawRect(
                w * 0.62f,
                horizonY,
                w,
                h,
                waterPaint
            )

            waterPaint.shader = null

            // ====================================================
            // WATER REFLECTIONS
            // ====================================================

            for (i in 0 until 18) {

                val reflectionY =
                    horizonY +
                        12f +
                        i * 22f

                val reflectionWidth =
                    20f +
                        i * 7f

                paint.color =
                    if (i % 2 == 0) {

                        Color.argb(
                            110,
                            50,
                            150,
                            255
                        )

                    } else {

                        Color.argb(
                            90,
                            255,
                            70,
                            80
                        )
                    }

                // Left reflection

                canvas.drawRect(
                    w * 0.12f -
                        reflectionWidth,
                    reflectionY,
                    w * 0.12f +
                        reflectionWidth,
                    reflectionY + 3f,
                    paint
                )

                // Right reflection

                canvas.drawRect(
                    w * 0.88f -
                        reflectionWidth,
                    reflectionY,
                    w * 0.88f +
                        reflectionWidth,
                    reflectionY + 3f,
                    paint
                )
            }

            // ====================================================
            // BRIDGE
            // ====================================================

            val roadTopWidth =
                w * 0.095f

            val roadBottomWidth =
                w * 0.83f

            val roadBottom =
                h * 1.08f

            roadPaint.style =
                Paint.Style.FILL

            roadPaint.color =
                Color.rgb(
                    24,
                    29,
                    39
                )

            val bridge = Path()

            // Top-left

            bridge.moveTo(
                w / 2f -
                    roadTopWidth,
                horizonY
            )

            // Top-right

            bridge.lineTo(
                w / 2f +
                    roadTopWidth,
                horizonY
            )

            // Bottom-right

            bridge.lineTo(
                w / 2f +
                    roadBottomWidth,
                roadBottom
            )

            // Bottom-left

            bridge.lineTo(
                w / 2f -
                    roadBottomWidth,
                roadBottom
            )

            bridge.close()

            canvas.drawPath(
                bridge,
                roadPaint
            )

            // ====================================================
            // BRIDGE RAILINGS
            // ====================================================

            linePaint.style =
                Paint.Style.STROKE

            linePaint.strokeWidth = 5f

            // Blue left railing

            linePaint.color =
                Color.argb(
                    230,
                    70,
                    155,
                    255
                )

            canvas.drawLine(
                w / 2f -
                    roadTopWidth,
                horizonY,
                w / 2f -
                    roadBottomWidth,
                roadBottom,
                linePaint
            )

            // Red right railing

            linePaint.color =
                Color.argb(
                    230,
                    255,
                    65,
                    75
                )

            canvas.drawLine(
                w / 2f +
                    roadTopWidth,
                horizonY,
                w / 2f +
                    roadBottomWidth,
                roadBottom,
                linePaint
            )

            // ====================================================
            // BRIDGE LIGHT POSTS
            // ====================================================

            for (i in 0 until 12) {

                val depth =
                    (
                        (
                            i +
                                animationOffset /
                                100f
                        ) % 12
                    ) / 12f

                val y =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) * depth

                val roadHalfWidth =
                    roadTopWidth +
                        (
                            roadBottomWidth -
                                roadTopWidth
                        ) * depth

                val leftX =
                    w / 2f -
                        roadHalfWidth *
                        0.82f

                val rightX =
                    w / 2f +
                        roadHalfWidth *
                        0.82f

                paint.color =
                    Color.argb(
                        220,
                        170,
                        220,
                        255
                    )

                // Left light

                canvas.drawCircle(
                    leftX,
                    y,
                    3f +
                        depth * 4f,
                    paint
                )

                // Right light

                canvas.drawCircle(
                    rightX,
                    y,
                    3f +
                        depth * 4f,
                    paint
                )
            }

            // ====================================================
            // CENTER ROAD MARKINGS
            // ====================================================

            val centerX =
                w / 2f

            for (i in 0 until 9) {

                val depth =
                    i / 9f

                val nextDepth =
                    (i + 0.42f) / 9f

                val y1 =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) * depth

                val y2 =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) *
                        min(
                            nextDepth,
                            1f
                        )

                val halfWidth =
                    3f +
                        depth * 14f

                linePaint.color =
                    Color.argb(
                        235,
                        245,
                        245,
                        245
                    )

                linePaint.strokeWidth =
                    2f +
                        depth * 4f

                canvas.drawLine(
                    centerX -
                        halfWidth,
                    y1,
                    centerX +
                        halfWidth,
                    y2,
                    linePaint
                )
            }

            // ====================================================
            // MOVING CITY / ROAD LIGHTS
            // ====================================================

            for (i in 0 until 16) {

                val normalized =
                    (
                        (
                            i * 0.18f
                        ) +
                            animationOffset /
                            1000f
                    ) % 1f

                val y =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) * normalized

                val roadHalfWidth =
                    roadTopWidth +
                        (
                            roadBottomWidth -
                                roadTopWidth
                        ) * normalized

                val sideOffset =
                    roadHalfWidth *
                        0.67f

                paint.color =
                    if (i % 2 == 0) {

                        Color.argb(
                            230,
                            30,
                            125,
                            255
                        )

                    } else {

                        Color.argb(
                            230,
                            255,
                            50,
                            65
                        )
                    }

                canvas.drawCircle(
                    centerX -
                        sideOffset,
                    y,
                    2f +
                        normalized * 4f,
                    paint
                )

                canvas.drawCircle(
                    centerX +
                        sideOffset,
                    y,
                    2f +
                        normalized * 4f,
                    paint
                )
            }

            // ====================================================
            // BRIDGE HORIZON GLOW
            // ====================================================

            glowPaint.shader =
                LinearGradient(
                    0f,
                    horizonY - 50f,
                    0f,
                    horizonY + 50f,
                    Color.argb(
                        0,
                        40,
                        140,
                        255
                    ),
                    Color.argb(
                        160,
                        40,
                        140,
                        255
                    ),
                    Shader.TileMode.CLAMP
                )

            canvas.drawRect(
                0f,
                horizonY - 50f,
                w,
                horizonY + 50f,
                glowPaint
            )

            glowPaint.shader = null

            // ====================================================
            // LIGHT VIGNETTE
            // ====================================================

            paint.shader =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    h,
                    Color.argb(
                        20,
                        0,
                        0,
                        0
                    ),
                    Color.argb(
                        95,
                        0,
                        0,
                        0
                    ),
                    Shader.TileMode.CLAMP
                )

            canvas.drawRect(
                0f,
                0f,
                w,
                h,
                paint
            )

            paint.shader = null
        }

        // ========================================================
        // WATER BACKGROUND WITHOUT ROAD
        // ========================================================

        private fun drawWater(
            canvas: Canvas,
            w: Float,
            h: Float,
            horizonY: Float
        ) {

            waterPaint.shader =
                LinearGradient(
                    0f,
                    horizonY,
                    0f,
                    h,
                    Color.rgb(
                        20,
                        75,
                        115
                    ),
                    Color.rgb(
                        5,
                        28,
                        50
                    ),
                    Shader.TileMode.CLAMP
                )

            canvas.drawRect(
                0f,
                horizonY,
                w,
                h,
                waterPaint
            )

            waterPaint.shader = null

            // Water lines

            for (i in 0 until 20) {

                val y =
                    horizonY +
                        i * 25f

                paint.color =
                    Color.argb(
                        100,
                        60,
                        160,
                        220
                    )

                canvas.drawRect(
                    0f,
                    y,
                    w,
                    y + 2f,
                    paint
                )
            }
        }
    }
}