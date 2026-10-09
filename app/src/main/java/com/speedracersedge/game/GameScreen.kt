
package com.speedracersedge.game

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.min

class GameScreen(context: Context) : FrameLayout(context) {

    private enum class Screen {
        LOADING, TAP_TO_START, LOBBY, SETTINGS, CONTROLS,
        SHOP, FRIENDS, PROFILE, MODE_MENU, RACE_STARTING, INFO
    }

    private var currentScreen = Screen.LOADING
    private var loadingProgress = 0
    private var selectedMode = "ONLINE RACE"
    private var selectedControl = "Touch Steering"
    private var playerReady = false
    private var teamPlayers = 1
    private var coins = 0
    private var gems = 0

    // Declared so the previous pendingInvite compilation error is fixed.
    private var pendingInvite = false
    private var pendingInviteName = "Alex"
    private var shopCategory = "Cars"
    private var pageButtonCount = 0

    private val backgroundView = RacingBackgroundView(context)
    private val content = FrameLayout(context)

    init {
        addView(backgroundView, fullParams())
        addView(content, fullParams())
        showLoading()
    }

    private fun dp(value: Int) =
        (value * resources.displayMetrics.density).toInt()

    private fun fullParams() =
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    private fun frameParams(
        width: Int,
        height: Int,
        gravity: Int = Gravity.TOP or Gravity.START
    ) = LayoutParams(
        if (width == LayoutParams.MATCH_PARENT) width else dp(width),
        if (height == LayoutParams.MATCH_PARENT) height else dp(height),
        gravity
    )

    private fun resetContent() {
        content.removeAllViews()
    }

    private fun addBackground(showRoad: Boolean = true) {
        backgroundView.showRoad = showRoad
        backgroundView.invalidate()
    }

    private fun showLoading() {
        currentScreen = Screen.LOADING
        resetContent()
        addBackground()

        val panel = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(24), dp(24), dp(40))
        }

        panel.addView(
            createText("SPEED RACERS EDGE", 27f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(45))
        )
        panel.addView(
            createText("RACE BEYOND THE LIMIT", 12f, Color.CYAN, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(28))
        )

        val progressText = createText("LOADING 0%", 12f, Color.WHITE, true)
        progressText.gravity = Gravity.CENTER
        panel.addView(progressText, LinearLayout.LayoutParams(-1, dp(28)))

        val track = FrameLayout(context).apply {
            background = roundedBackground(Color.DKGRAY, Color.CYAN, 1)
        }
        val fill = View(context).apply {
            background = roundedBackground(Color.CYAN, Color.CYAN, 0)
        }
        track.addView(fill, FrameLayout.LayoutParams(0, dp(6), Gravity.START))
        panel.addView(track, LinearLayout.LayoutParams(-1, dp(8)))
        content.addView(panel, fullParams())

        loadingProgress = 0
        val task = object : Runnable {
            override fun run() {
                if (currentScreen != Screen.LOADING) return
                loadingProgress = (loadingProgress + 5).coerceAtMost(100)
                progressText.text = "LOADING $loadingProgress%"
                fill.layoutParams = FrameLayout.LayoutParams(
                    (track.width * loadingProgress / 100f).toInt(),
                    dp(6),
                    Gravity.START
                )
                if (loadingProgress < 100) {
                    postDelayed(this, 90L)
                } else {
                    postDelayed({
                        if (currentScreen == Screen.LOADING) showTapToStart()
                    }, 250L)
                }
            }
        }
        postDelayed(task, 250L)
    }

    private fun showTapToStart() {
        currentScreen = Screen.TAP_TO_START
        resetContent()
        addBackground()

        val panel = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setPadding(dp(20), dp(20), dp(20), dp(48))
            isClickable = true
            setOnClickListener { showLobby() }
        }

        panel.addView(
            createText("SPEED RACERS EDGE", 28f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(48))
        )
        panel.addView(
            createText("T A P   T O   S T A R T", 15f, Color.CYAN, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(40))
        )
        content.addView(panel, fullParams())
    }

    private fun showLobby() {
        currentScreen = Screen.LOBBY
        resetContent()
        addBackground()

        val root = FrameLayout(context)
        content.addView(root, fullParams())

        root.addView(
            createButton("👤 ADAM • LV.1", 11f) { showProfile() },
            frameParams(138, 42, Gravity.TOP or Gravity.START).apply {
                leftMargin = dp(10); topMargin = dp(10)
            }
        )

        val currency = createText("🪙 $coins     💎 $gems", 12f, Color.WHITE, true)
        currency.gravity = Gravity.CENTER
        root.addView(
            currency,
            frameParams(150, 42, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                topMargin = dp(10)
            }
        )

        root.addView(
            createButton("⚙ SETTINGS", 10f) { showSettings() },
            frameParams(100, 42, Gravity.TOP or Gravity.END).apply {
                rightMargin = dp(10); topMargin = dp(10)
            }
        )

        root.addView(
            createText("WELCOME TO THE EDGE", 15f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 30, Gravity.TOP).apply { topMargin = dp(63) }
        )

        root.addView(
            createText(selectedMode, 11f, Color.CYAN, true).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 24, Gravity.TOP).apply { topMargin = dp(93) }
        )

        val car = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = roundedBackground(
                Color.argb(115, 4, 12, 25), Color.CYAN, 1
            )
        }
        car.addView(
            createText("🏎️", 72f, Color.WHITE, false).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(104))
        )
        car.addView(
            createText("YOUR RIDE", 10f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(24))
        )
        root.addView(
            car,
            frameParams(205, 142, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                topMargin = dp(135)
            }
        )

        val team = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(6), dp(6), dp(6), dp(6))
            background = roundedBackground(
                Color.argb(225, 5, 15, 32), Color.CYAN, 1
            )
        }
        team.addView(
            createText("TEAM $teamPlayers/4", 12f, Color.WHITE, true),
            LinearLayout.LayoutParams(-1, dp(26))
        )

        for (i in 0..3) {
            val slot = when {
                i == 0 -> "👤 ADAM"
                i < teamPlayers -> "👤 MEMBER ${i + 1}"
                else -> "+ OPEN SLOT"
            }
            team.addView(
                createText(slot, 10f, if (i < teamPlayers) Color.WHITE else Color.LTGRAY, i == 0),
                LinearLayout.LayoutParams(-1, dp(27))
            )
        }

        team.addView(
            createButton("INVITE", 10f) { showFriends() },
            LinearLayout.LayoutParams(-1, dp(32))
        )
        team.addView(
            createButton(if (playerReady) "READY ✓" else "READY", 10f) {
                playerReady = !playerReady
                showLobby()
            },
            LinearLayout.LayoutParams(-1, dp(32))
        )
        root.addView(
            team,
            frameParams(136, 250, Gravity.END or Gravity.CENTER_VERTICAL).apply {
                rightMargin = dp(8); topMargin = dp(30)
            }
        )

        root.addView(
            createButton("SHOP", 12f) { showShop() },
            frameParams(100, 46, Gravity.BOTTOM or Gravity.START).apply {
                leftMargin = dp(10); bottomMargin = dp(14)
            }
        )
        root.addView(
            createButton("FRIENDS", 10f) { showFriends() },
            frameParams(96, 38, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply {
                bottomMargin = dp(18)
            }
        )
        root.addView(
            createButton("SELECT MODE", 11f) { showModeMenu() },
            frameParams(128, 46, Gravity.BOTTOM or Gravity.END).apply {
                rightMargin = dp(10); bottomMargin = dp(14)
            }
        )

        if (pendingInvite) showInvitePopup(root)
    }

    private fun showSettings() {
        currentScreen = Screen.SETTINGS
        resetContent()
        addBackground(false)
        val root = makePage("SETTINGS")
        pageButtonCount = 0

        addPageButton(root, "Gameplay") {
            showInfoPage("GAMEPLAY", "Gameplay options will be connected as racing systems are implemented.")
        }
        addPageButton(root, "Controls") { showControls() }
        addPageButton(root, "Graphics") {
            showInfoPage("GRAPHICS", "Graphics presets will be connected when the 3D renderer is ready.")
        }
        addPageButton(root, "Sound") {
            showInfoPage("SOUND", "Sound settings are not connected yet.")
        }
        addPageButton(root, "Language") {
            showInfoPage("LANGUAGE", "Language selection will be added here.")
        }
        addPageButton(root, "Account") {
            showInfoPage("ACCOUNT", "Account sign-in and cloud saving are not connected yet.")
        }
        addPageButton(root, "About") {
            showInfoPage("ABOUT", "Speed Racers Edge is an Android racing game in development.")
        }
        addBackButton(root)
    }

    private fun showControls() {
        currentScreen = Screen.CONTROLS
        resetContent()
        addBackground(false)
        val root = makePage("CONTROLS")

        root.addView(
            createText("Choose your preferred steering method.", 13f, Color.WHITE, false),
            frameParams(-1, 48).apply { topMargin = dp(65) }
        )

        listOf("Tilt", "Steering Wheel", "Arrow Buttons", "Touch Steering")
            .forEachIndexed { index, option ->
                root.addView(
                    createButton(
                        (if (selectedControl == option) "✓  " else "") + option,
                        13f
                    ) {
                        selectedControl = option
                        showControls()
                    },
                    frameParams(-1, 48).apply {
                        topMargin = dp(120 + index * 58)
                        leftMargin = dp(12); rightMargin = dp(12)
                    }
                )
            }
        addBackButton(root)
    }

    private fun showShop() {
        currentScreen = Screen.SHOP
        resetContent()
        addBackground(false)
        val root = makePage("SHOP")

        root.addView(
            createText("🪙 $coins Race Coins     💎 $gems Gems", 13f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 34).apply { topMargin = dp(62) }
        )

        val categories = listOf(
            "Cars", "Drivers", "Car Skins", "Vehicle Effects",
            "Emotes", "Arrival & Finish Effects", "Coins", "Gems"
        )

        val scroll = ScrollView(context)
        val list = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(12))
        }

        categories.forEach { category ->
            list.addView(
                createButton(
                    (if (shopCategory == category) "✓  " else "") + category,
                    12f
                ) {
                    shopCategory = category
                    showShop()
                },
                LinearLayout.LayoutParams(-1, dp(42)).apply {
                    bottomMargin = dp(6)
                }
            )
        }

        val description = when (shopCategory) {
            "Coins" -> "Race Coins are earned through gameplay."
            "Gems" -> "Gems will require a connected purchase system."
            else -> "$shopCategory will contain unlockable items as the shop is developed."
        }
        list.addView(
            createText(description, 12f, Color.WHITE, false).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(65))
        )

        scroll.addView(list)
        root.addView(
            scroll,
            frameParams(-1, -1).apply {
                topMargin = dp(102); bottomMargin = dp(58)
            }
        )
        addBackButton(root)
    }

    private fun showFriends() {
        currentScreen = Screen.FRIENDS
        resetContent()
        addBackground(false)
        val root = makePage("FRIENDS & TEAM")

        root.addView(
            createText("Invite friends to your team. Maximum team size: 4.", 12f, Color.WHITE, false),
            frameParams(-1, 46).apply {
                topMargin = dp(62); leftMargin = dp(12); rightMargin = dp(12)
            }
        )

        listOf("Alex • ONLINE", "Mike • ONLINE", "John • OFFLINE")
            .forEachIndexed { index, friend ->
                val online = friend.contains("ONLINE")
                root.addView(
                    createButton("👤 $friend", 12f) {
                        if (!online) {
                            Toast.makeText(context, "This friend is offline.", Toast.LENGTH_SHORT).show()
                        } else if (teamPlayers >= 4) {
                            Toast.makeText(context, "Your team is full.", Toast.LENGTH_SHORT).show()
                        } else {
                            showInviteConfirmation(friend.substringBefore(" •"))
                        }
                    },
                    frameParams(-1, 48).apply {
                        topMargin = dp(120 + index * 58)
                        leftMargin = dp(12); rightMargin = dp(12)
                    }
                )
            }
        addBackButton(root)
    }

    private fun showInviteConfirmation(friend: String) {
        AlertDialog.Builder(context)
            .setTitle("Invite $friend?")
            .setMessage(
                "This is a local invitation preview. Real invitations between phones need an online backend."
            )
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("INVITE") { _, _ ->
                if (teamPlayers < 4) teamPlayers++
                Toast.makeText(context, "Local invite preview sent.", Toast.LENGTH_SHORT).show()
                showLobby()
            }
            .show()
    }

    private fun showInvitePopup(root: FrameLayout) {
        val popup = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), dp(8), dp(10), dp(8))
            background = roundedBackground(Color.rgb(8, 20, 40), Color.CYAN, 2)
        }

        popup.addView(
            createText("TEAM INVITATION", 12f, Color.CYAN, true),
            LinearLayout.LayoutParams(-1, dp(24))
        )
        popup.addView(
            createText("$pendingInviteName invited you to join their team.", 11f, Color.WHITE, false),
            LinearLayout.LayoutParams(-1, dp(40))
        )

        val buttons = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
        buttons.addView(
            createButton("ACCEPT", 10f) {
                pendingInvite = false
                if (teamPlayers < 4) teamPlayers++
                showLobby()
            },
            LinearLayout.LayoutParams(0, dp(34), 1f)
        )
        buttons.addView(
            createButton("DECLINE", 10f) {
                pendingInvite = false
                showLobby()
            },
            LinearLayout.LayoutParams(0, dp(34), 1f)
        )
        popup.addView(buttons)

        root.addView(
            popup,
            frameParams(230, 115, Gravity.TOP or Gravity.END).apply {
                topMargin = dp(60); rightMargin = dp(10)
            }
        )
    }

    private fun showProfile() {
        currentScreen = Screen.PROFILE
        resetContent()
        addBackground(false)
        val root = makePage("PLAYER PROFILE")

        root.addView(
            createText("👤", 55f, Color.WHITE, false).apply { gravity = Gravity.CENTER },
            frameParams(-1, 90).apply { topMargin = dp(74) }
        )
        root.addView(
            createText("ADAM", 22f, Color.WHITE, true).apply { gravity = Gravity.CENTER },
            frameParams(-1, 42).apply { topMargin = dp(164) }
        )
        root.addView(
            createText("LEVEL 1\nRace Coins: $coins\nGems: $gems", 14f, Color.WHITE, false).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 100).apply { topMargin = dp(210) }
        )
        addBackButton(root)
    }

    private fun showModeMenu() {
        currentScreen = Screen.MODE_MENU
        resetContent()
        addBackground(false)
        val root = makePage("SELECT MODE")

        listOf("QUICK RACE", "ONLINE RACE", "HEIST", "PRIVATE LOBBY", "SPECIAL EVENTS")
            .forEachIndexed { index, mode ->
                root.addView(
                    createButton((if (selectedMode == mode) "✓  " else "") + mode, 12f) {
                        selectedMode = mode
                        showModeMenu()
                    },
                    frameParams(-1, 47).apply {
                        topMargin = dp(72 + index * 54)
                        leftMargin = dp(12); rightMargin = dp(12)
                    }
                )
            }

        root.addView(
            createButton("CONTINUE", 12f) { showRaceStarting() },
            frameParams(-1, 46, Gravity.BOTTOM).apply {
                bottomMargin = dp(64); leftMargin = dp(12); rightMargin = dp(12)
            }
        )
        addBackButton(root)
    }

    private fun showRaceStarting() {
        currentScreen = Screen.RACE_STARTING
        resetContent()
        addBackground()

        val panel = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = roundedBackground(Color.argb(230, 5, 15, 32), Color.CYAN, 2)
        }
        panel.addView(
            createText("RACE READY", 23f, Color.WHITE, true).apply { gravity = Gravity.CENTER },
            LinearLayout.LayoutParams(-1, dp(44))
        )
        panel.addView(
            createText("MODE: $selectedMode", 13f, Color.CYAN, true).apply { gravity = Gravity.CENTER },
            LinearLayout.LayoutParams(-1, dp(36))
        )
        panel.addView(
            createText(
                "This is a UI preview. Actual racing, matchmaking and online gameplay are not connected yet.",
                12f, Color.WHITE, false
            ).apply { gravity = Gravity.CENTER },
            LinearLayout.LayoutParams(-1, dp(84))
        )
        panel.addView(
            createButton("BACK TO LOBBY", 12f) { showLobby() },
            LinearLayout.LayoutParams(-1, dp(42))
        )
        content.addView(panel, frameParams(300, 250, Gravity.CENTER))
    }

    private fun showInfoPage(title: String, message: String) {
        currentScreen = Screen.INFO
        resetContent()
        addBackground(false)
        val root = makePage(title)

        root.addView(
            createText(message, 14f, Color.WHITE, false).apply {
                gravity = Gravity.CENTER
                setPadding(dp(20), dp(10), dp(20), dp(10))
            },
            frameParams(-1, 180, Gravity.CENTER)
        )
        addBackButton(root)
    }

    private fun makePage(title: String): FrameLayout {
        val root = FrameLayout(context)
        content.addView(root, fullParams())

        root.addView(
            createText(title, 20f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
                background = roundedBackground(Color.argb(220, 5, 15, 32), Color.CYAN, 1)
            },
            frameParams(-1, 46).apply {
                leftMargin = dp(12); rightMargin = dp(12); topMargin = dp(10)
            }
        )
        return root
    }

    private fun addPageButton(root: FrameLayout, label: String, action: () -> Unit) {
        val index = pageButtonCount++
        root.addView(
            createButton(label, 13f, action),
            frameParams(-1, 47).apply {
                topMargin = dp(70 + index * 54)
                leftMargin = dp(12); rightMargin = dp(12)
            }
        )
    }

    private fun addBackButton(root: FrameLayout) {
        root.addView(
            createButton("← BACK", 11f) {
                pageButtonCount = 0
                showLobby()
            },
            frameParams(105, 40, Gravity.BOTTOM or Gravity.START).apply {
                leftMargin = dp(12); bottomMargin = dp(12)
            }
        )
    }

    private fun createText(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean
    ): TextView = TextView(context).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.CENTER_VERTICAL
        typeface = Typeface.create(
            "sans-serif",
            if (bold) Typeface.BOLD else Typeface.NORMAL
        )
        setPadding(dp(4), dp(2), dp(4), dp(2))
    }

    private fun createButton(
        label: String,
        size: Float = 13f,
        action: () -> Unit
    ): TextView = TextView(context).apply {
        text = label
        textSize = size
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        isClickable = true
        isFocusable = true
        setPadding(dp(5), dp(4), dp(5), dp(4))
        background = roundedBackground(Color.argb(225, 5, 19, 38), Color.CYAN, 1)
        setOnClickListener { action() }
    }

    private fun roundedBackground(
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Int
    ) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(9).toFloat()
        setColor(fillColor)
        if (strokeWidth > 0) setStroke(dp(strokeWidth), strokeColor)
    }

    private inner class RacingBackgroundView(context: Context) : View(context) {
        var showRoad = true
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat()
            val h = height.toFloat()
            if (w <= 0f || h <= 0f) return

            val sky = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = android.graphics.LinearGradient(
                    0f, 0f, 0f, h,
                    Color.rgb(3, 8, 23),
                    Color.rgb(8, 35, 58),
                    android.graphics.Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, w, h, sky)

            val buildingWidth = w / 9f
            val buildingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(5, 13, 28)
            }
            val windows = Paint(Paint.ANTI_ALIAS_FLAG)

            for (i in 0..9) {
                val left = i * buildingWidth - buildingWidth * 0.15f
                val buildingHeight = h * (0.13f + ((i * 37) % 20) / 100f)
                val top = h * 0.38f - buildingHeight * 0.35f

                canvas.drawRect(left, top, left + buildingWidth, h * 0.62f, buildingPaint)

                for (row in 0..5) {
                    for (col in 0..2) {
                        val x = left + buildingWidth * (0.2f + col * 0.25f)
                        val y = top + dp(8) + row * dp(13)
                        windows.color = when ((i + row + col) % 4) {
                            0 -> Color.argb(190, 40, 190, 255)
                            1 -> Color.argb(170, 255, 80, 90)
                            else -> Color.argb(100, 55, 120, 160)
                        }
                        canvas.drawRect(x, y, x + dp(3), y + dp(5), windows)
                    }
                }
            }

            paint.color = Color.rgb(4, 24, 43)
            canvas.drawRect(0f, h * 0.56f, w, h * 0.83f, paint)

            for (i in 0..30) {
                val y = h * 0.58f + i * h * 0.007f
                val half = w * (0.08f + i * 0.019f)
                paint.color = if (i % 3 == 0) {
                    Color.argb(100, 45, 185, 255)
                } else {
                    Color.argb(45, 40, 105, 160)
                }
                canvas.drawRect(w / 2f - half, y, w / 2f + half, y + dp(2), paint)
            }

            val bridge = Path().apply {
                moveTo(0f, h * 0.60f)
                lineTo(w * 0.18f, h * 0.51f)
                lineTo(w * 0.82f, h * 0.51f)
                lineTo(w, h * 0.60f)
                lineTo(w, h * 0.64f)
                lineTo(0f, h * 0.64f)
                close()
            }
            paint.color = Color.rgb(12, 26, 43)
            canvas.drawPath(bridge, paint)

            paint.color = Color.rgb(42, 190, 240)
            paint.strokeWidth = dp(2).toFloat()
            paint.style = Paint.Style.STROKE
            canvas.drawLine(0f, h * 0.60f, w, h * 0.60f, paint)
            paint.style = Paint.Style.FILL

            if (showRoad) {
                val road = Path().apply {
                    moveTo(w * 0.43f, h * 0.57f)
                    lineTo(w * 0.57f, h * 0.57f)
                    lineTo(w * 1.1f, h)
                    lineTo(w * -0.1f, h)
                    close()
                }
                paint.color = Color.rgb(8, 12, 22)
                canvas.drawPath(road, paint)

                paint.color = Color.rgb(220, 45, 65)
                canvas.drawLine(w * 0.43f, h * 0.57f, w * 0.08f, h, paint)
                paint.color = Color.rgb(40, 205, 255)
                canvas.drawLine(w * 0.57f, h * 0.57f, w * 0.92f, h, paint)

                paint.color = Color.argb(180, 150, 190, 210)
                for (i in 0..5) {
                    val f = i / 5f
                    val y = h * (0.61f + f * 0.36f)
                    val half = w * (0.015f + f * 0.25f)
                    canvas.drawRect(w / 2f - half, y, w / 2f + half, y + dp(2), paint)
                }
            }

            paint.shader = android.graphics.RadialGradient(
                w / 2f, h / 2f, min(w, h) * 0.8f,
                intArrayOf(Color.TRANSPARENT, Color.argb(150, 0, 0, 0)),
                null,
                android.graphics.Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null
        }
    }
}
