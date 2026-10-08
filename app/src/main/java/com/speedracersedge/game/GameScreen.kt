package com.speedracersedge.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.LinearGradient
import android.graphics.Shader
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
import kotlin.math.max
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

    private val progressBar = ProgressBar(
        context,
        null,
        android.R.attr.progressBarStyleHorizontal
    )

    private val loadingText = TextView(context)
    private val tapToStart = TextView(context)

    init {
        setBackgroundColor(Color.rgb(3, 6, 14))
        showLoadingScreen()

        // Longer initial loading sequence.
        postDelayed({
            if (currentScreen == Screen.LOADING) {
                showTapToStart()
            }
        }, 6000)
    }

    // ------------------------------------------------------------
    // LOADING SCREEN
    // ------------------------------------------------------------

    private fun showLoadingScreen() {
        removeAllViews()
        currentScreen = Screen.LOADING
        setBackgroundColor(Color.rgb(3, 6, 14))

        val background = RacingBackgroundView(context)
        background.showRoad = false

        addView(
            background,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val darkOverlay = View(context)
        darkOverlay.setBackgroundColor(Color.argb(120, 0, 0, 0))

        addView(
            darkOverlay,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val title = createText("SPEED RACERS EDGE")
        title.textSize = 30f
        title.typeface = Typeface.DEFAULT_BOLD

        addView(
            title,
            frameParams(
                360,
                60,
                Gravity.CENTER,
                0,
                -80,
                0,
                0
            )
        )

        val subtitle = createText("LOADING GAME...")
        subtitle.textSize = 14f
        subtitle.setTextColor(Color.LTGRAY)

        addView(
            subtitle,
            frameParams(
                300,
                45,
                Gravity.CENTER,
                0,
                0,
                0,
                0
            )
        )

        // Bottom loading area.
        val bottomLoading = LinearLayout(context)
        bottomLoading.orientation = LinearLayout.VERTICAL
        bottomLoading.setPadding(22, 0, 22, 22)

        loadingText.text = "INITIALIZING GAME..."
        loadingText.textSize = 13f
        loadingText.setTextColor(Color.WHITE)
        loadingText.gravity = Gravity.START

        bottomLoading.addView(
            loadingText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                32
            )
        )

        progressBar.max = 100
        progressBar.progress = 0

        bottomLoading.addView(
            progressBar,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                10
            )
        )

        addView(
            bottomLoading,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                70,
                Gravity.BOTTOM
            )
        )

        animateLoading()
    }

    private fun animateLoading() {
        loadingProgress = 0

        val runnable = object : Runnable {
            override fun run() {
                if (currentScreen != Screen.LOADING) return

                loadingProgress += 1
                progressBar.progress = loadingProgress

                loadingText.text = when {
                    loadingProgress < 20 -> "LOADING GAME..."
                    loadingProgress < 40 -> "LOADING VEHICLES..."
                    loadingProgress < 60 -> "LOADING GARAGE..."
                    loadingProgress < 80 -> "LOADING MULTIPLAYER..."
                    else -> "FINALIZING..."
                }

                if (loadingProgress < 100) {
                    postDelayed(this, 55)
                }
            }
        }

        post(runnable)
    }

    // ------------------------------------------------------------
    // TAP TO START
    // ------------------------------------------------------------

    private fun showTapToStart() {
        removeAllViews()
        currentScreen = Screen.TAP_TO_START
        setBackgroundColor(Color.rgb(3, 6, 14))

        val background = RacingBackgroundView(context)
        background.showRoad = true

        addView(
            background,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val overlay = View(context)
        overlay.setBackgroundColor(Color.argb(90, 0, 0, 0))

        addView(
            overlay,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val title = createText("SPEED RACERS EDGE")
        title.textSize = 34f
        title.typeface = Typeface.DEFAULT_BOLD

        addView(
            title,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )

        tapToStart.text = "TAP TO START"
        tapToStart.textSize = 17f
        tapToStart.setTextColor(Color.WHITE)
        tapToStart.gravity = Gravity.CENTER
        tapToStart.setPadding(30, 16, 30, 16)

        tapToStart.setOnClickListener {
            showLobby()
        }

        val tapParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            65,
            Gravity.BOTTOM
        )

        tapParams.bottomMargin = 20

        addView(tapToStart, tapParams)
    }

    // ------------------------------------------------------------
    // LOBBY
    // ------------------------------------------------------------

    private fun showLobby() {
        removeAllViews()
        currentScreen = Screen.LOBBY
        setBackgroundColor(Color.rgb(3, 6, 14))

        // Cinematic racing background.
        val background = RacingBackgroundView(context)
        background.showRoad = true

        addView(
            background,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // Dark cinematic overlay.
        val overlay = View(context)
        overlay.setBackgroundColor(Color.argb(105, 0, 0, 0))

        addView(
            overlay,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // Top profile.
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

        // --------------------------------------------------------
        // GARAGE
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // PARTY
        // --------------------------------------------------------

        val partyPanel = createPanel()

        val partyTitle = createText("YOUR PARTY  $partyPlayers/4")
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

        // --------------------------------------------------------
        // FRIENDS
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // SHOP
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // READY
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // MODE
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // START RACE
        // --------------------------------------------------------

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

    // ------------------------------------------------------------
    // PARTY SLOT
    // ------------------------------------------------------------

    private fun addPartySlot(
        parent: LinearLayout,
        name: String,
        status: String,
        occupied: Boolean
    ) {
        val slot = LinearLayout(context)
        slot.orientation = LinearLayout.HORIZONTAL
        slot.gravity = Gravity.CENTER_VERTICAL
        slot.setPadding(8, 4, 8, 4)

        val avatar = createText(
            if (occupied) "👤" else "＋"
        )

        avatar.textSize = 22f

        slot.addView(
            avatar,
            LinearLayout.LayoutParams(45, 55)
        )

        val info = LinearLayout(context)
        info.orientation = LinearLayout.VERTICAL
        info.gravity = Gravity.CENTER_VERTICAL

        val nameText = createText(name)
        nameText.textSize = 14f
        nameText.gravity = Gravity.START or Gravity.CENTER_VERTICAL

        val statusText = createText(status)
        statusText.textSize = 11f
        statusText.gravity = Gravity.START or Gravity.CENTER_VERTICAL
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

    // ------------------------------------------------------------
    // FRIENDS PANEL
    // ------------------------------------------------------------

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
        row.setPadding(10, 5, 10, 5)

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

    // ------------------------------------------------------------
    // MODE MENU
    // ------------------------------------------------------------

    private fun showModeMenu() {
        removeAllViews()
        currentScreen = Screen.MODE_MENU
        setBackgroundColor(Color.rgb(3, 6, 14))

        val background = RacingBackgroundView(context)
        background.showRoad = true

        addView(
            background,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val overlay = View(context)
        overlay.setBackgroundColor(Color.argb(120, 0, 0, 0))

        addView(
            overlay,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val container = LinearLayout(context)

        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER
        container.setPadding(35, 35, 35, 35)

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

    // ------------------------------------------------------------
    // MATCH READY
    // ------------------------------------------------------------

    private fun showRaceStartingScreen() {
        removeAllViews()
        currentScreen = Screen.RACE_STARTING
        setBackgroundColor(Color.rgb(3, 6, 12))

        val background = RacingBackgroundView(context)
        background.showRoad = true

        addView(
            background,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val overlay = View(context)
        overlay.setBackgroundColor(Color.argb(125, 0, 0, 0))

        addView(
            overlay,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val container = LinearLayout(context)

        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER
        container.setPadding(30, 30, 30, 30)

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

    // ------------------------------------------------------------
    // UI HELPERS
    // ------------------------------------------------------------

    private fun createPanel(): LinearLayout {
        val panel = LinearLayout(context)

        panel.orientation = LinearLayout.VERTICAL
        panel.gravity = Gravity.CENTER
        panel.setPadding(15, 15, 15, 15)

        panel.background = roundedBackground(
            Color.argb(
                225,
                12,
                20,
                38
            ),
            22
        )

        return panel
    }

    private fun createText(text: String): TextView {
        val view = TextView(context)

        view.text = text
        view.setTextColor(Color.WHITE)
        view.gravity = Gravity.CENTER
        view.textSize = 16f

        return view
    }

    private fun createButton(text: String): Button {
        val button = Button(context)

        button.text = text
        button.setTextColor(Color.WHITE)
        button.textSize = 13f
        button.isAllCaps = false

        button.background = roundedBackground(
            Color.rgb(
                20,
                38,
                68
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
        drawable.cornerRadius = radius.toFloat()

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

    // ============================================================
    // CINEMATIC RACING BACKGROUND
    // ============================================================

    private class RacingBackgroundView(
        context: Context
    ) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

        var showRoad = true

        private var animationOffset = 0f

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)

            post(object : Runnable {
                override fun run() {
                    animationOffset += 2.5f

                    if (animationOffset > 80f) {
                        animationOffset = 0f
                    }

                    invalidate()
                    postDelayed(this, 35)
                }
            })
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            if (w <= 0f || h <= 0f) return

            // ----------------------------------------------------
            // NIGHT SKY
            // ----------------------------------------------------

            paint.shader = LinearGradient(
                0f,
                0f,
                0f,
                h,
                Color.rgb(2, 5, 15),
                Color.rgb(7, 13, 28),
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

            // ----------------------------------------------------
            // RED / BLUE CITY GLOW
            // ----------------------------------------------------

            glowPaint.shader = LinearGradient(
                0f,
                0f,
                w,
                0f,
                Color.argb(0, 0, 70, 255),
                Color.argb(90, 255, 20, 50),
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(
                0f,
                0f,
                w,
                h * 0.72f,
                glowPaint
            )

            glowPaint.shader = LinearGradient(
                w,
                0f,
                0f,
                0f,
                Color.argb(0, 255, 20, 40),
                Color.argb(75, 0, 90, 255),
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(
                0f,
                0f,
                w,
                h * 0.72f,
                glowPaint
            )

            glowPaint.shader = null

            // ----------------------------------------------------
            // DISTANT CITY LIGHTS
            // ----------------------------------------------------

            paint.style = Paint.Style.FILL

            val buildingHeight = h * 0.22f
            val buildingTop = h * 0.38f

            var x = 0f

            while (x < w) {
                val buildingWidth = max(
                    22f,
                    w * 0.035f
                )

                val seed = ((x / buildingWidth).toInt() % 5)

                val buildingExtra = when (seed) {
                    0 -> 35f
                    1 -> 65f
                    2 -> 15f
                    3 -> 50f
                    else -> 80f
                }

                paint.color = Color.rgb(
                    5,
                    10,
                    22
                )

                canvas.drawRect(
                    x,
                    buildingTop - buildingExtra,
                    x + buildingWidth,
                    h * 0.58f,
                    paint
                )

                // Building windows.
                var wy = buildingTop - buildingExtra + 12f

                while (wy < h * 0.55f) {
                    if (((wy.toInt() + x.toInt()) % 3) != 0) {
                        paint.color = if (
                            ((x.toInt() + wy.toInt()) % 2) == 0
                        ) {
                            Color.argb(
                                150,
                                40,
                                120,
                                255
                            )
                        } else {
                            Color.argb(
                                130,
                                255,
                                45,
                                55
                            )
                        }

                        canvas.drawRect(
                            x + 7f,
                            wy,
                            x + 11f,
                            wy + 3f,
                            paint
                        )
                    }

                    wy += 15f
                }

                x += buildingWidth + 3f
            }

            if (!showRoad) {
                return
            }

            // ----------------------------------------------------
            // ROAD
            // ----------------------------------------------------

            val horizonY = h * 0.52f
            val roadBottom = h * 1.08f
            val roadTopWidth = w * 0.10f
            val roadBottomWidth = w * 0.95f

            roadPaint.style = Paint.Style.FILL
            roadPaint.color = Color.rgb(
                8,
                10,
                17
            )

            val road = Path()

            road.moveTo(
                w / 2f - roadTopWidth,
                horizonY
            )

            road.lineTo(
                w / 2f + roadTopWidth,
                horizonY
            )

            road.lineTo(
                w / 2f + roadBottomWidth,
                roadBottom
            )

            road.lineTo(
                w / 2f - roadBottomWidth,
                roadBottom
            )

            road.close()

            canvas.drawPath(
                road,
                roadPaint
            )

            // ----------------------------------------------------
            // ROAD EDGE GLOW
            // ----------------------------------------------------

            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 4f

            linePaint.color = Color.argb(
                210,
                20,
                100,
                255
            )

            canvas.drawLine(
                w / 2f - roadTopWidth,
                horizonY,
                w / 2f - roadBottomWidth,
                roadBottom,
                linePaint
            )

            linePaint.color = Color.argb(
                210,
                255,
                35,
                50
            )

            canvas.drawLine(
                w / 2f + roadTopWidth,
                horizonY,
                w / 2f + roadBottomWidth,
                roadBottom,
                linePaint
            )

            // ----------------------------------------------------
            // CENTER ROAD MARKINGS
            // ----------------------------------------------------

            val centerX = w / 2f

            for (i in 0 until 8) {
                val depth = i / 8f
                val nextDepth = (i + 0.45f) / 8f

                val y1 = horizonY +
                    (roadBottom - horizonY) *
                    depth

                val y2 = horizonY +
                    (roadBottom - horizonY) *
                    min(nextDepth, 1f)

                val halfWidth1 =
                    3f + depth * 13f

                linePaint.color = Color.argb(
                    210,
                    225,
                    225,
                    225
                )

                linePaint.strokeWidth =
                    2f + depth * 4f

                canvas.drawLine(
                    centerX - halfWidth1,
                    y1,
                    centerX + halfWidth1,
                    y2,
                    linePaint
                )
            }

            // ----------------------------------------------------
            // SPEED LIGHTS
            // ----------------------------------------------------

            for (i in 0 until 14) {
                val normalized =
                    ((i * 0.19f) + animationOffset / 1000f) % 1f

                val y =
                    horizonY +
                        (roadBottom - horizonY) *
                        normalized

                val roadHalfWidth =
                    roadTopWidth +
                        (roadBottomWidth - roadTopWidth) *
                        normalized

                val sideOffset =
                    roadHalfWidth * 0.72f

                paint.color =
                    if (i % 2 == 0) {
                        Color.argb(
                            180,
                            20,
                            100,
                            255
                        )
                    } else {
                        Color.argb(
                            180,
                            255,
                            35,
                            55
                        )
                    }

                canvas.drawCircle(
                    centerX - sideOffset,
                    y,
                    2f + normalized * 3f,
                    paint
                )

                canvas.drawCircle(
                    centerX + sideOffset,
                    y,
                    2f + normalized * 3f,
                    paint
                )
            }

            // ----------------------------------------------------
            // HORIZON LIGHT
            // ----------------------------------------------------

            paint.color = Color.argb(
                160,
                50,
                120,
                255
            )

            canvas.drawRect(
                0f,
                horizonY - 2f,
                w,
                horizonY + 2f,
                paint
            )

            // ----------------------------------------------------
            // DARK VIGNETTE
            // ----------------------------------------------------

            paint.shader = LinearGradient(
                0f,
                0f,
                0f,
                h,
                Color.argb(80, 0, 0, 0),
                Color.argb(160, 0, 0, 0),
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
