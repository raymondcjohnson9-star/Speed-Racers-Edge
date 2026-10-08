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
                    Color.rgb(
                        120,
                        175,
                        220
                    )
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

            val slotParams =
                FrameLayout.LayoutParams(
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

            val friendParams =
                FrameLayout.LayoutParams(
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

        val closeParams =
            FrameLayout.LayoutParams(
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
    // RACING BACKGROUND
    //
    // VISUAL ORDER:
    //
    // CITY
    //      ↓
    // WATER      WATER
    //      ↓
    //       BRIDGE
    //      ↓
    // ROAD + RAILINGS
    //      ↓
    // MOVING LIGHTS
    //
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

        private var animationOffset = 0f

        var showRoad = true

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

            // ====================================================
            // NIGHT SKY
            // ====================================================

            paint.shader =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    h * 0.58f,
                    Color.rgb(
                        5,
                        14,
                        34
                    ),
                    Color.rgb(
                        23,
                        57,
                        92
                    ),
                    Shader.TileMode.CLAMP
                )

            canvas.drawRect(
                0f,
                0f,
                w,
                h * 0.60f,
                paint
            )

            paint.shader = null

            // ====================================================
            // CITY HORIZON GLOW
            // ====================================================

            glowPaint.shader =
                LinearGradient(
                    0f,
                    h * 0.25f,
                    w,
                    h * 0.58f,
                    Color.argb(
                        90,
                        30,
                        120,
                        255
                    ),
                    Color.argb(
                        110,
                        255,
                        45,
                        65
                    ),
                    Shader.TileMode.CLAMP
                )

            canvas.drawRect(
                0f,
                0f,
                w,
                h * 0.60f,
                glowPaint
            )

            glowPaint.shader = null

            // ====================================================
            // CITY
            // ====================================================

            val cityBase =
                h * 0.54f

            var cityX = 0f
            var buildingIndex = 0

            while (cityX < w) {

                val buildingWidth =
                    when (buildingIndex % 5) {
                        0 -> 38f
                        1 -> 55f
                        2 -> 30f
                        3 -> 70f
                        else -> 45f
                    }

                val buildingHeight =
                    when (buildingIndex % 6) {
                        0 -> h * 0.22f
                        1 -> h * 0.31f
                        2 -> h * 0.17f
                        3 -> h * 0.27f
                        4 -> h * 0.20f
                        else -> h * 0.35f
                    }

                val buildingTop =
                    cityBase -
                        buildingHeight

                paint.color =
                    when (buildingIndex % 3) {

                        0 ->
                            Color.rgb(
                                10,
                                24,
                                47
                            )

                        1 ->
                            Color.rgb(
                                15,
                                32,
                                58
                            )

                        else ->
                            Color.rgb(
                                8,
                                20,
                                42
                            )
                    }

                canvas.drawRect(
                    cityX,
                    buildingTop,
                    cityX + buildingWidth,
                    cityBase,
                    paint
                )

                // Rooftop light

                paint.color =
                    Color.argb(
                        220,
                        255,
                        55,
                        70
                    )

                canvas.drawRect(
                    cityX +
                        buildingWidth * 0.25f,
                    buildingTop - 4f,
                    cityX +
                        buildingWidth * 0.75f,
                    buildingTop,
                    paint
                )

                // Windows

                var windowY =
                    buildingTop + 12f

                while (
                    windowY <
                    cityBase - 8f
                ) {

                    var windowX =
                        cityX + 7f

                    while (
                        windowX <
                        cityX +
                        buildingWidth -
                        6f
                    ) {

                        if (
                            (
                                windowX.toInt() +
                                windowY.toInt() +
                                buildingIndex
                            ) % 4 != 0
                        ) {

                            paint.color =
                                if (
                                    (
                                        buildingIndex +
                                        windowX.toInt()
                                    ) % 3 == 0
                                ) {

                                    Color.argb(
                                        230,
                                        255,
                                        190,
                                        70
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
                                windowX,
                                windowY,
                                windowX + 5f,
                                windowY + 4f,
                                paint
                            )
                        }

                        windowX += 12f
                    }

                    windowY += 15f
                }

                cityX +=
                    buildingWidth + 5f

                buildingIndex++
            }

            // ====================================================
            // HORIZON
            // ====================================================

            val horizonY =
                h * 0.545f

            // ====================================================
            // WATER BASE
            //
            // Entire lower area starts as water.
            // The bridge is then drawn over the middle.
            // This guarantees:
            //
            // WATER | BRIDGE | WATER
            // ====================================================

            waterPaint.shader =
                LinearGradient(
                    0f,
                    horizonY,
                    0f,
                    h,
                    Color.rgb(
                        22,
                        78,
                        118
                    ),
                    Color.rgb(
                        3,
                        23,
                        45
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

            // ====================================================
            // WATER REFLECTIONS
            // ====================================================

            for (i in 0 until 30) {

                val depth =
                    i / 30f

                val y =
                    horizonY +
                        depth *
                        (h - horizonY)

                val width =
                    15f +
                        depth * 75f

                val leftCenter =
                    w * 0.16f

                val rightCenter =
                    w * 0.84f

                paint.color =
                    if (i % 3 == 0) {

                        Color.argb(
                            120,
                            70,
                            165,
                            235
                        )

                    } else if (i % 3 == 1) {

                        Color.argb(
                            90,
                            255,
                            70,
                            90
                        )

                    } else {

                        Color.argb(
                            75,
                            120,
                            210,
                            255
                        )
                    }

                canvas.drawRect(
                    leftCenter - width,
                    y,
                    leftCenter + width,
                    y + 2f,
                    paint
                )

                canvas.drawRect(
                    rightCenter - width,
                    y,
                    rightCenter + width,
                    y + 2f,
                    paint
                )
            }

            // ====================================================
            // WATER HIGHLIGHTS
            // ====================================================

            for (i in 0 until 18) {

                val y =
                    horizonY +
                        18f +
                        i * 30f

                val offset =
                    (
                        animationOffset *
                        (0.15f + i * 0.01f)
                    ) % 80f

                paint.color =
                    Color.argb(
                        80,
                        80,
                        180,
                        255
                    )

                canvas.drawRect(
                    -40f + offset,
                    y,
                    80f + offset,
                    y + 2f,
                    paint
                )

                canvas.drawRect(
                    w - 80f - offset,
                    y,
                    w + 40f - offset,
                    y + 2f,
                    paint
                )
            }

            // ====================================================
            // IF ROAD IS DISABLED
            // ====================================================

            if (!showRoad) {

                drawWaterOnlyDetails(
                    canvas,
                    w,
                    h,
                    horizonY
                )

                drawVignette(
                    canvas,
                    w,
                    h
                )

                return
            }

            // ====================================================
            // BRIDGE / ROAD
            // ====================================================

            val roadTopWidth =
                w * 0.075f

            val roadBottomWidth =
                w * 0.84f

            val roadBottom =
                h * 1.10f

            roadPaint.style =
                Paint.Style.FILL

            roadPaint.color =
                Color.rgb(
                    23,
                    28,
                    38
                )

            val bridge =
                Path()

            bridge.moveTo(
                w / 2f -
                    roadTopWidth,
                horizonY
            )

            bridge.lineTo(
                w / 2f +
                    roadTopWidth,
                horizonY
            )

            bridge.lineTo(
                w / 2f +
                    roadBottomWidth,
                roadBottom
            )

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
            // ROAD EDGE GLOW
            // ====================================================

            linePaint.style =
                Paint.Style.STROKE

            linePaint.strokeWidth = 7f

            linePaint.color =
                Color.argb(
                    45,
                    70,
                    150,
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

            linePaint.color =
                Color.argb(
                    45,
                    255,
                    60,
                    70
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
            // BLUE LEFT RAILING
            // ====================================================

            linePaint.strokeWidth = 5f

            linePaint.color =
                Color.rgb(
                    55,
                    145,
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

            // ====================================================
            // RED RIGHT RAILING
            // ====================================================

            linePaint.color =
                Color.rgb(
                    255,
                    55,
                    70
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
            // RAILING POSTS
            // ====================================================

            for (i in 0 until 15) {

                val depth =
                    i / 15f

                val y =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) *
                        depth

                val roadHalfWidth =
                    roadTopWidth +
                        (
                            roadBottomWidth -
                                roadTopWidth
                        ) *
                        depth

                val leftX =
                    w / 2f -
                        roadHalfWidth

                val rightX =
                    w / 2f +
                        roadHalfWidth

                val postHeight =
                    5f +
                        depth * 20f

                linePaint.strokeWidth =
                    2f +
                        depth * 2f

                linePaint.color =
                    Color.rgb(
                        65,
                        160,
                        255
                    )

                canvas.drawLine(
                    leftX,
                    y,
                    leftX,
                    y - postHeight,
                    linePaint
                )

                linePaint.color =
                    Color.rgb(
                        255,
                        70,
                        80
                    )

                canvas.drawLine(
                    rightX,
                    y,
                    rightX,
                    y - postHeight,
                    linePaint
                )
            }

            // ====================================================
            // BRIDGE LIGHT POSTS
            // ====================================================

            for (i in 0 until 12) {

                val depth =
                    (
                        (
                            i +
                            animationOffset / 100f
                        ) % 12
                    ) / 12f

                val y =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) *
                        depth

                val roadHalfWidth =
                    roadTopWidth +
                        (
                            roadBottomWidth -
                                roadTopWidth
                        ) *
                        depth

                val leftX =
                    w / 2f -
                        roadHalfWidth *
                        0.78f

                val rightX =
                    w / 2f +
                        roadHalfWidth *
                        0.78f

                val radius =
                    2f +
                        depth * 5f

                paint.color =
                    Color.argb(
                        230,
                        160,
                        220,
                        255
                    )

                canvas.drawCircle(
                    leftX,
                    y,
                    radius,
                    paint
                )

                canvas.drawCircle(
                    rightX,
                    y,
                    radius,
                    paint
                )

                // Light glow

                paint.color =
                    Color.argb(
                        35,
                        100,
                        190,
                        255
                    )

                canvas.drawCircle(
                    leftX,
                    y,
                    radius * 3f,
                    paint
                )

                canvas.drawCircle(
                    rightX,
                    y,
                    radius * 3f,
                    paint
                )
            }

            // ====================================================
            // CENTER ROAD MARKINGS
            // ====================================================

            val centerX =
                w / 2f

            for (i in 0 until 12) {

                val depth =
                    i / 12f

                val nextDepth =
                    min(
                        (i + 0.45f) / 12f,
                        1f
                    )

                val y1 =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) *
                        depth

                val y2 =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) *
                        nextDepth

                val width =
                    2f +
                        depth * 10f

                linePaint.strokeWidth =
                    2f +
                        depth * 4f

                linePaint.color =
                    Color.argb(
                        240,
                        240,
                        245,
                        250
                    )

                canvas.drawLine(
                    centerX - width,
                    y1,
                    centerX + width,
                    y2,
                    linePaint
                )
            }

            // ====================================================
            // MOVING BLUE / RED LIGHTS
            // ====================================================

            for (i in 0 until 22) {

                val normalized =
                    (
                        i * 0.12f +
                        animationOffset / 85f
                    ) % 1f

                val y =
                    horizonY +
                        (
                            roadBottom -
                                horizonY
                        ) *
                        normalized

                val roadHalfWidth =
                    roadTopWidth +
                        (
                            roadBottomWidth -
                                roadTopWidth
                        ) *
                        normalized

                val sideOffset =
                    roadHalfWidth *
                    0.62f

                val radius =
                    2f +
                        normalized * 5f

                paint.color =
                    if (i % 2 == 0) {

                        Color.rgb(
                            40,
                            140,
                            255
                        )

                    } else {

                        Color.rgb(
                            255,
                            55,
                            70
                        )
                    }

                canvas.drawCircle(
                    centerX -
                        sideOffset,
                    y,
                    radius,
                    paint
                )

                canvas.drawCircle(
                    centerX +
                        sideOffset,
                    y,
                    radius,
                    paint
                )
            }

            // ====================================================
            // HORIZON BRIDGE GLOW
            // ====================================================

            glowPaint.shader =
                LinearGradient(
                    0f,
                    horizonY - 70f,
                    0f,
                    horizonY + 70f,
                    Color.argb(
                        0,
                        40,
                        140,
                        255
                    ),
                    Color.argb(
                        170,
                        40,
                        140,
                        255
                    ),
                    Shader.TileMode.CLAMP
                )

            canvas.drawRect(
                0f,
                horizonY - 70f,
                w,
                horizonY + 70f,
                glowPaint
            )

            glowPaint.shader = null

            // ====================================================
            // FINAL VIGNETTE
            // ====================================================

            drawVignette(
                canvas,
                w,
                h
            )
        }

        // ========================================================
        // WATER-ONLY DETAILS
        // ========================================================

        private fun drawWaterOnlyDetails(
            canvas: Canvas,
            w: Float,
            h: Float,
            horizonY: Float
        ) {

            for (i in 0 until 22) {

                val y =
                    horizonY +
                        i * 24f

                val movement =
                    (
                        animationOffset *
                        0.4f +
                        i * 17f
                    ) % 120f

                paint.color =
                    Color.argb(
                        80,
                        80,
                        180,
                        240
                    )

                canvas.drawRect(
                    -100f + movement,
                    y,
                    60f + movement,
                    y + 2f,
                    paint
                )

                canvas.drawRect(
                    w - 60f - movement,
                    y,
                    w + 100f - movement,
                    y + 2f,
                    paint
                )
            }
        }

        // ========================================================
        // VIGNETTE
        // ========================================================

        private fun drawVignette(
            canvas: Canvas,
            w: Float,
            h: Float
        ) {

            paint.shader =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    h,
                    Color.argb(
                        10,
                        0,
                        0,
                        0
                    ),
                    Color.argb(
                        100,
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
    }
}