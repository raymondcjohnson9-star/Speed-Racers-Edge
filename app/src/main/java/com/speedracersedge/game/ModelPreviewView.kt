
package com.speedracersedge.game

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.LinearGradient
import android.graphics.RadialGradient
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
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
    private var pendingInvite = false
    private var pendingInviteName = "Alex"
    private var shopCategory = "Cars"
    private var pageButtonCount = 0

    private var poseIndex = 0
    private var emoteIndex = 0
    private var skinIndex = 0
    private var effectsEnabled = true

    private val skinNames = listOf(
        "NEON CYAN", "RACING RED", "VIOLET", "GOLD"
    )

    private val skinColors = listOf(
        Color.rgb(0, 220, 255),
        Color.rgb(255, 45, 75),
        Color.rgb(175, 75, 255),
        Color.rgb(255, 185, 40)
    )

    private val poseNames = listOf("STANDING", "SITTING", "VICTORY")
    private val emoteNames = listOf("WAVE", "FLEX", "DANCE")

    private val backgroundView = GarageBackgroundView(context)
    private val content = FrameLayout(context)

    private val neon = Color.rgb(0, 220, 255)
    private val gold = Color.rgb(255, 197, 65)
    private val panelColor = Color.rgb(5, 13, 29)

    init {
        setBackgroundColor(Color.rgb(3, 7, 17))
        addView(backgroundView, fullParams())
        addView(content, fullParams())
        showLoading()
    }

    private fun dp(value: Int): Int =
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
            setPadding(dp(24), dp(24), dp(24), dp(32))
        }

        panel.addView(
            createText("SPEED RACERS EDGE", 26f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(44))
        )

        panel.addView(
            createText("ENTER THE NEON GARAGE", 11f, neon, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(26))
        )

        val progressText = createText("LOADING 0%", 11f, Color.WHITE, true)
        progressText.gravity = Gravity.CENTER
        panel.addView(progressText, LinearLayout.LayoutParams(-1, dp(26)))

        val track = FrameLayout(context).apply {
            background = roundedBackground(Color.DKGRAY, neon, 1)
        }

        val fill = View(context).apply {
            background = roundedBackground(neon, neon, 0)
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
                    postDelayed(this, 70L)
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
            setPadding(dp(20), dp(20), dp(20), dp(36))
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
            createText("T A P   T O   S T A R T", 14f, neon, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(38))
        )

        content.addView(panel, fullParams())
    }

    private fun showLobby() {
        currentScreen = Screen.LOBBY
        resetContent()
        addBackground(false)

        val root = FrameLayout(context)
        content.addView(root, fullParams())

        // Top navigation.
        root.addView(
            createButton("ADAM • LV.1", 10f) { showProfile() },
            frameParams(112, 36, Gravity.TOP or Gravity.START).apply {
                leftMargin = dp(8)
                topMargin = dp(7)
            }
        )

        val currencyBar = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = roundedBackground(panelColor, neon, 1)
        }

        currencyBar.addView(
            createText("◈ $coins", 11f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(0, -1, 1f)
        )

        currencyBar.addView(
            createText("◆ $gems", 11f, gold, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(0, -1, 1f)
        )

        root.addView(
            currencyBar,
            frameParams(142, 36, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                topMargin = dp(7)
            }
        )

        root.addView(
            createButton("SETTINGS", 9f) { showSettings() },
            frameParams(82, 36, Gravity.TOP or Gravity.END).apply {
                rightMargin = dp(8)
                topMargin = dp(7)
            }
        )

        root.addView(
            createText("NEON GARAGE", 14f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
                letterSpacing = 0.12f
            },
            frameParams(-1, 25, Gravity.TOP).apply {
                topMargin = dp(46)
            }
        )

        root.addView(
            createText("$selectedMode  •  SUPERCAR BAY 01", 9f, neon, true).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 19, Gravity.TOP).apply {
                topMargin = dp(68)
            }
        )

        // The stage draws the supercar and driver directly on Canvas.
        val stage = NeonGarageStage(context)
        root.addView(
            stage,
            frameParams(-1, -1).apply {
                leftMargin = dp(8)
                topMargin = dp(91)
                rightMargin = dp(157)
                bottomMargin = dp(57)
            }
        )

        // Crew panel.
        val team = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(6), dp(7), dp(6), dp(7))
            background = roundedBackground(panelColor, neon, 1)
        }

        team.addView(
            createText("YOUR CREW", 10f, neon, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(23))
        )

        team.addView(
            createText("TEAM $teamPlayers / 4", 10f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(22))
        )

        for (i in 0..3) {
            val label = when {
                i == 0 -> "● ADAM"
                i < teamPlayers -> "● MEMBER ${i + 1}"
                else -> "+ OPEN SLOT"
            }

            val color = when {
                i == 0 -> neon
                i < teamPlayers -> Color.WHITE
                else -> Color.LTGRAY
            }

            team.addView(
                createText(label, 8f, color, i < teamPlayers).apply {
                    gravity = Gravity.CENTER
                },
                LinearLayout.LayoutParams(-1, dp(24))
            )
        }

        team.addView(
            createButton("FRIENDS", 9f) { showFriends() },
            LinearLayout.LayoutParams(-1, dp(31)).apply {
                topMargin = dp(3)
            }
        )

        team.addView(
            createButton(if (playerReady) "READY ✓" else "READY UP", 9f) {
                playerReady = !playerReady
                showLobby()
            },
            LinearLayout.LayoutParams(-1, dp(31)).apply {
                topMargin = dp(4)
            }
        )

        root.addView(
            team,
            frameParams(143, 218, Gravity.END or Gravity.CENTER_VERTICAL).apply {
                rightMargin = dp(8)
                topMargin = dp(14)
            }
        )

        // Main action strip.
        val actions = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(3), dp(3), dp(3), dp(3))
            background = roundedBackground(
                Color.argb(240, 3, 9, 22),
                Color.rgb(30, 75, 100),
                1
            )
        }

        fun actionButton(label: String, action: () -> Unit) {
            actions.addView(
                createButton(label, 8.5f, action),
                LinearLayout.LayoutParams(0, -1, 1f).apply {
                    leftMargin = dp(2)
                    rightMargin = dp(2)
                }
            )
        }

        actionButton("SHOP") { showShop() }

        actionButton(poseNames[poseIndex]) {
            poseIndex = (poseIndex + 1) % poseNames.size
            showLobby()
        }

        actionButton(emoteNames[emoteIndex]) {
            emoteIndex = (emoteIndex + 1) % emoteNames.size
            showLobby()
        }

        actionButton("PAINT") {
            skinIndex = (skinIndex + 1) % skinNames.size
            showLobby()
        }

        actionButton(if (effectsEnabled) "FX ON" else "FX OFF") {
            effectsEnabled = !effectsEnabled
            showLobby()
        }

        actionButton("MODE") { showModeMenu() }

        root.addView(
            actions,
            frameParams(-1, 43, Gravity.BOTTOM or Gravity.START).apply {
                leftMargin = dp(8)
                rightMargin = dp(157)
                bottomMargin = dp(7)
            }
        )

        root.addView(
            createText(
                "${skinNames[skinIndex]} • ${if (effectsEnabled) "NEON FX ACTIVE" else "FX DISABLED"}",
                8f,
                skinColors[skinIndex],
                true
            ).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 17, Gravity.BOTTOM).apply {
                bottomMargin = dp(51)
                rightMargin = dp(157)
                leftMargin = dp(8)
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
            showInfoPage(
                "GAMEPLAY",
                "Gameplay options will be connected as racing systems are implemented."
            )
        }

        addPageButton(root, "Controls") { showControls() }

        addPageButton(root, "Graphics") {
            showInfoPage(
                "GRAPHICS",
                "Graphics presets will be connected when the 3D renderer is ready."
            )
        }

        addPageButton(root, "Sound") {
            showInfoPage("SOUND", "Sound settings are not connected yet.")
        }

        addPageButton(root, "Language") {
            showInfoPage("LANGUAGE", "Language selection will be added here.")
        }

        addPageButton(root, "Account") {
            showInfoPage(
                "ACCOUNT",
                "Account sign-in and cloud saving are not connected yet."
            )
        }

        addPageButton(root, "About") {
            showInfoPage(
                "ABOUT",
                "Speed Racers Edge is an Android racing game in development."
            )
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
                        leftMargin = dp(12)
                        rightMargin = dp(12)
                    }
                )
            }

        addBackButton(root)
    }

    private fun showShop() {
        currentScreen = Screen.SHOP
        resetContent()
        addBackground(false)

        val root = makePage("GARAGE SHOP")

        root.addView(
            createText("◈ $coins Race Coins      ◆ $gems Gems", 12f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 32).apply { topMargin = dp(58) }
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
                LinearLayout.LayoutParams(-1, dp(40)).apply {
                    bottomMargin = dp(5)
                }
            )
        }

        val description = when (shopCategory) {
            "Coins" -> "Race Coins are planned as gameplay rewards."
            "Gems" -> "Gems are premium credits. Purchases are not connected yet."
            "Car Skins" -> "Selected paint: ${skinNames[skinIndex]}."
            "Vehicle Effects" ->
                "Neon effects are ${if (effectsEnabled) "enabled" else "disabled"}."
            "Emotes" -> "Selected driver emote: ${emoteNames[emoteIndex]}."
            else -> "$shopCategory will contain unlockable items as the shop is developed."
        }

        list.addView(
            createText(description, 12f, Color.WHITE, false).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(60))
        )

        scroll.addView(list)

        root.addView(
            scroll,
            frameParams(-1, -1).apply {
                topMargin = dp(96)
                bottomMargin = dp(58)
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
            createText(
                "Invite friends to your team. Maximum team size: 4.",
                12f, Color.WHITE, false
            ),
            frameParams(-1, 46).apply {
                topMargin = dp(62)
                leftMargin = dp(12)
                rightMargin = dp(12)
            }
        )

        listOf("Alex • ONLINE", "Mike • ONLINE", "John • OFFLINE")
            .forEachIndexed { index, friend ->
                val online = friend.contains("ONLINE")

                root.addView(
                    createButton("👤 $friend", 12f) {
                        if (!online) {
                            Toast.makeText(
                                context,
                                "This friend is offline.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (teamPlayers >= 4) {
                            Toast.makeText(
                                context,
                                "Your team is full.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showInviteConfirmation(friend.substringBefore(" •"))
                        }
                    },
                    frameParams(-1, 48).apply {
                        topMargin = dp(120 + index * 58)
                        leftMargin = dp(12)
                        rightMargin = dp(12)
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
                Toast.makeText(
                    context,
                    "Local invite preview sent.",
                    Toast.LENGTH_SHORT
                ).show()
                showLobby()
            }
            .show()
    }

    private fun showInvitePopup(root: FrameLayout) {
        val popup = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), dp(8), dp(10), dp(8))
            background = roundedBackground(
                Color.rgb(8, 20, 40), neon, 2
            )
        }

        popup.addView(
            createText("TEAM INVITATION", 12f, neon, true),
            LinearLayout.LayoutParams(-1, dp(24))
        )

        popup.addView(
            createText(
                "$pendingInviteName invited you to join their team.",
                11f, Color.WHITE, false
            ),
            LinearLayout.LayoutParams(-1, dp(40))
        )

        val buttons = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

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
                topMargin = dp(60)
                rightMargin = dp(10)
            }
        )
    }

    private fun showProfile() {
        currentScreen = Screen.PROFILE
        resetContent()
        addBackground(false)

        val root = makePage("PLAYER PROFILE")

        root.addView(
            createText("👤", 55f, Color.WHITE, false).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 90).apply { topMargin = dp(74) }
        )

        root.addView(
            createText("ADAM", 22f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            frameParams(-1, 42).apply { topMargin = dp(164) }
        )

        root.addView(
            createText(
                "LEVEL 1\nRace Coins: $coins\nGems: $gems",
                14f, Color.WHITE, false
            ).apply {
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

        listOf(
            "QUICK RACE",
            "ONLINE RACE",
            "HEIST",
            "PRIVATE LOBBY",
            "SPECIAL EVENTS"
        ).forEachIndexed { index, mode ->
            root.addView(
                createButton(
                    (if (selectedMode == mode) "✓  " else "") + mode,
                    12f
                ) {
                    selectedMode = mode
                    showModeMenu()
                },
                frameParams(-1, 47).apply {
                    topMargin = dp(72 + index * 54)
                    leftMargin = dp(12)
                    rightMargin = dp(12)
                }
            )
        }

        root.addView(
            createButton("CONTINUE", 12f) { showRaceStarting() },
            frameParams(-1, 46, Gravity.BOTTOM).apply {
                bottomMargin = dp(64)
                leftMargin = dp(12)
                rightMargin = dp(12)
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
            background = roundedBackground(
                Color.argb(230, 5, 15, 32), neon, 2
            )
        }

        panel.addView(
            createText("RACE READY", 23f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(44))
        )

        panel.addView(
            createText("MODE: $selectedMode", 13f, neon, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, dp(36))
        )

        panel.addView(
            createText(
                "This is a UI preview. Actual racing, matchmaking and online gameplay are not connected yet.",
                12f, Color.WHITE, false
            ).apply {
                gravity = Gravity.CENTER
            },
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
                background = roundedBackground(
                    Color.argb(220, 5, 15, 32), neon, 1
                )
            },
            frameParams(-1, 46).apply {
                leftMargin = dp(12)
                rightMargin = dp(12)
                topMargin = dp(10)
            }
        )

        return root
    }

    private fun addPageButton(
        root: FrameLayout,
        label: String,
        action: () -> Unit
    ) {
        val index = pageButtonCount++

        root.addView(
            createButton(label, 13f, action),
            frameParams(-1, 47).apply {
                topMargin = dp(70 + index * 54)
                leftMargin = dp(12)
                rightMargin = dp(12)
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
                leftMargin = dp(12)
                bottomMargin = dp(12)
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
        background = roundedBackground(
            Color.argb(225, 5, 19, 38), neon, 1
        )
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

    // Custom garage illustration.
    private inner class GarageBackgroundView(context: Context) : View(context) {
        var showRoad = true
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()
            if (w <= 0f || h <= 0f) return

            paint.shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(
                    Color.rgb(3, 7, 19),
                    Color.rgb(8, 17, 37),
                    Color.rgb(4, 10, 23)
                ),
                null,
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null

            // Tall garage walls and structural beams.
            paint.color = Color.rgb(8, 15, 31)
            canvas.drawRect(0f, h * 0.12f, w, h * 0.59f, paint)

            paint.color = Color.rgb(16, 27, 45)
            canvas.drawRect(0f, h * 0.14f, w, h * 0.17f, paint)
            canvas.drawRect(0f, h * 0.47f, w, h * 0.49f, paint)

            for (i in 0..7) {
                val x = w * i / 7f
                paint.color = Color.rgb(15, 27, 44)
                canvas.drawRect(x, h * 0.15f, x + dp(5), h * 0.59f, paint)
            }

            // Neon roof strips.
            paint.color = Color.rgb(0, 165, 215)
            canvas.drawRect(w * 0.08f, h * 0.12f, w * 0.42f, h * 0.126f, paint)
            paint.color = Color.rgb(235, 35, 75)
            canvas.drawRect(w * 0.58f, h * 0.12f, w * 0.92f, h * 0.126f, paint)

            // Garage door panels.
            paint.color = Color.rgb(6, 12, 25)
            canvas.drawRoundRect(
                RectF(w * 0.05f, h * 0.21f, w * 0.39f, h * 0.55f),
                dp(5).toFloat(), dp(5).toFloat(), paint
            )
            canvas.drawRoundRect(
                RectF(w * 0.61f, h * 0.21f, w * 0.95f, h * 0.55f),
                dp(5).toFloat(), dp(5).toFloat(), paint
            )

            for (i in 0..5) {
                val y = h * (0.25f + i * 0.045f)
                paint.color = Color.rgb(20, 35, 55)
                canvas.drawRect(w * 0.06f, y, w * 0.38f, y + dp(2), paint)
                canvas.drawRect(w * 0.62f, y, w * 0.94f, y + dp(2), paint)
            }

            // Floor.
            paint.shader = LinearGradient(
                0f, h * 0.53f, 0f, h,
                Color.rgb(13, 25, 43),
                Color.rgb(2, 5, 13),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, h * 0.53f, w, h, paint)
            paint.shader = null

            // Perspective floor markings.
            for (i in 0..12) {
                val y = h * (0.58f + i * 0.036f)
                val half = w * (0.12f + i * 0.055f)
                paint.color = if (i % 3 == 0) {
                    Color.argb(90, 0, 190, 255)
                } else {
                    Color.argb(32, 100, 150, 190)
                }
                canvas.drawLine(
                    w / 2f - half, y,
                    w / 2f + half, y,
                    paint
                )
            }

            paint.color = Color.argb(140, 0, 200, 255)
            paint.strokeWidth = dp(2).toFloat()
            canvas.drawLine(0f, h * 0.62f, w * 0.26f, h * 0.62f, paint)
            canvas.drawLine(w * 0.74f, h * 0.62f, w, h * 0.62f, paint)

            if (showRoad) {
                paint.color = Color.rgb(8, 12, 22)
                canvas.drawRect(0f, h * 0.72f, w, h, paint)
            }

            paint.shader = RadialGradient(
                w / 2f, h / 2f, max(w, h) * 0.72f,
                intArrayOf(Color.TRANSPARENT, Color.argb(150, 0, 0, 0)),
                null,
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null
        }
    }

    // Drawn supercar, driver pose, paint skin, and neon effects.
    private inner class NeonGarageStage(context: Context) : View(context) {
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private var dragX = 0f
        private var rotation = 0f

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()
            if (w <= 0f || h <= 0f) return

            // Stage spotlight.
            p.shader = RadialGradient(
                w * 0.47f, h * 0.55f, min(w, h) * 0.65f,
                intArrayOf(
                    Color.argb(75, 0, 160, 220),
                    Color.argb(25, 0, 70, 120),
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w, h, p)
            p.shader = null

            // A slight side/front change when swiping rotates the display.
            val perspective = cos(Math.toRadians(rotation.toDouble())).toFloat()
            val carScaleX = 0.72f + abs(perspective) * 0.28f
            val centerX = w * 0.50f
            val carY = h * 0.68f

            canvas.save()
            canvas.translate(centerX, carY)
            canvas.scale(carScaleX, 1f)
            canvas.translate(-centerX, -carY)

            drawCar(canvas, centerX, carY, w, h)

            canvas.restore()

            drawDriver(canvas, w * 0.25f, h * 0.58f)

            // Floor glow under the car.
            if (effectsEnabled) {
                p.color = skinColors[skinIndex]
                p.alpha = 70
                p.style = Paint.Style.STROKE
                p.strokeWidth = dp(3).toFloat()
                canvas.drawOval(
                    RectF(w * 0.16f, h * 0.77f, w * 0.82f, h * 0.91f),
                    p
                )
                p.alpha = 255
                p.style = Paint.Style.FILL
            }

            p.color = Color.WHITE
            p.textSize = dp(10).toFloat()
            p.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            canvas.drawText("PREMIUM SUPERCAR", dp(10).toFloat(), h - dp(24).toFloat(), p)

            p.color = skinColors[skinIndex]
            p.textSize = dp(8).toFloat()
            canvas.drawText(
                "${skinNames[skinIndex]}  •  DRAG TO ROTATE",
                dp(10).toFloat(),
                h - dp(10).toFloat(),
                p
            )
        }

        private fun drawCar(
            canvas: Canvas,
            cx: Float,
            cy: Float,
            w: Float,
            h: Float
        ) {
            val bodyColor = skinColors[skinIndex]
            val carW = min(w * 0.75f, dp(350).toFloat())
            val carH = min(h * 0.30f, dp(112).toFloat())
            val left = cx - carW / 2f
            val right = cx + carW / 2f
            val top = cy - carH * 0.56f
            val bottom = cy + carH * 0.30f

            // Shadow.
            p.color = Color.argb(150, 0, 0, 0)
            canvas.drawOval(
                RectF(left - dp(8), bottom - dp(1), right + dp(8), bottom + dp(24)),
                p
            )

            // Neon ground reflection.
            if (effectsEnabled) {
                p.color = bodyColor
                p.alpha = 65
                canvas.drawOval(
                    RectF(left + dp(12), bottom, right - dp(12), bottom + dp(16)),
                    p
                )
                p.alpha = 255
            }

            // Wheels behind body.
            p.color = Color.rgb(2, 4, 8)
            canvas.drawRoundRect(
                RectF(left + carW * 0.13f, cy, left + carW * 0.30f, bottom + dp(13)),
                dp(10).toFloat(), dp(10).toFloat(), p
            )
            canvas.drawRoundRect(
                RectF(right - carW * 0.30f, cy, right - carW * 0.13f, bottom + dp(13)),
                dp(10).toFloat(), dp(10).toFloat(), p
            )

            p.color = Color.rgb(95, 115, 135)
            canvas.drawCircle(left + carW * 0.22f, bottom + dp(1), dp(11).toFloat(), p)
            canvas.drawCircle(right - carW * 0.22f, bottom + dp(1), dp(11).toFloat(), p)

            p.color = Color.rgb(15, 22, 32)
            canvas.drawCircle(left + carW * 0.22f, bottom + dp(1), dp(6).toFloat(), p)
            canvas.drawCircle(right - carW * 0.22f, bottom + dp(1), dp(6).toFloat(), p)

            // Low, wide supercar body.
            val body = Path().apply {
                moveTo(left, cy - carH * 0.02f)
                lineTo(left + carW * 0.06f, cy - carH * 0.30f)
                lineTo(left + carW * 0.23f, top + carH * 0.12f)
                lineTo(left + carW * 0.38f, top)
                lineTo(left + carW * 0.67f, top + carH * 0.02f)
                lineTo(left + carW * 0.84f, cy - carH * 0.20f)
                lineTo(right - carW * 0.04f, cy - carH * 0.06f)
                lineTo(right, cy + carH * 0.13f)
                lineTo(right - carW * 0.05f, bottom)
                lineTo(left + carW * 0.08f, bottom)
                close()
            }

            p.shader = LinearGradient(
                left, top, right, bottom,
                intArrayOf(
                    Color.rgb(220, 238, 250),
                    bodyColor,
                    Color.rgb(15, 35, 55)
                ),
                null,
                Shader.TileMode.CLAMP
            )
            canvas.drawPath(body, p)
            p.shader = null

            // Dark lower side skirt.
            p.color = Color.rgb(7, 12, 22)
            val skirt = Path().apply {
                moveTo(left + carW * 0.08f, cy + carH * 0.13f)
                lineTo(right - carW * 0.04f, cy + carH * 0.12f)
                lineTo(right - carW * 0.09f, bottom)
                lineTo(left + carW * 0.12f, bottom)
                close()
            }
            canvas.drawPath(skirt, p)

            // Windshield and roof glass.
            p.color = Color.rgb(5, 18, 33)
            val glass = Path().apply {
                moveTo(left + carW * 0.29f, top + carH * 0.12f)
                lineTo(left + carW * 0.40f, top + carH * 0.05f)
                lineTo(left + carW * 0.63f, top + carH * 0.06f)
                lineTo(left + carW * 0.73f, cy - carH * 0.18f)
                lineTo(left + carW * 0.33f, cy - carH * 0.18f)
                close()
            }
            canvas.drawPath(glass, p)

            // Glass highlights.
            p.color = Color.argb(150, 90, 200, 240)
            p.strokeWidth = dp(1).toFloat()
            p.style = Paint.Style.STROKE
            canvas.drawLine(
                left + carW * 0.40f, top + carH * 0.10f,
                left + carW * 0.34f, cy - carH * 0.19f, p
            )
            canvas.drawLine(
                left + carW * 0.63f, top + carH * 0.10f,
                left + carW * 0.70f, cy - carH * 0.18f, p
            )
            p.style = Paint.Style.FILL

            // Front splitter.
            p.color = Color.rgb(2, 5, 12)
            canvas.drawRoundRect(
                RectF(left - dp(4), cy + carH * 0.10f, left + carW * 0.18f, cy + carH * 0.22f),
                dp(3).toFloat(), dp(3).toFloat(), p
            )

            // Headlights.
            p.color = Color.WHITE
            canvas.drawRoundRect(
                RectF(left + carW * 0.04f, cy - carH * 0.04f, left + carW * 0.19f, cy + carH * 0.01f),
                dp(3).toFloat(), dp(3).toFloat(), p
            )
            canvas.drawRoundRect(
                RectF(right - carW * 0.18f, cy - carH * 0.04f, right - carW * 0.04f, cy + carH * 0.01f),
                dp(3).toFloat(), dp(3).toFloat(), p
            )

            if (effectsEnabled) {
                p.color = bodyColor
                p.alpha = 175
                p.strokeWidth = dp(2).toFloat()
                p.style = Paint.Style.STROKE
                canvas.drawPath(body, p)
                p.style = Paint.Style.FILL
                p.alpha = 255
            }

            // Front grille.
            p.color = Color.rgb(2, 5, 11)
            canvas.drawRoundRect(
                RectF(left + carW * 0.37f, cy + carH * 0.06f, left + carW * 0.68f, cy + carH * 0.16f),
                dp(4).toFloat(), dp(4).toFloat(), p
            )

            // Side vents.
            p.color = Color.rgb(0, 8, 18)
            canvas.drawRect(
                left + carW * 0.74f, cy + carH * 0.01f,
                left + carW * 0.82f, cy + carH * 0.09f, p
            )

            // Small rear wing.
            p.color = Color.rgb(9, 16, 27)
            canvas.drawRect(
                left + carW * 0.76f, top + carH * 0.02f,
                left + carW * 0.96f, top + carH * 0.08f, p
            )
            canvas.drawRect(
                left + carW * 0.80f, top + carH * 0.08f,
                left + carW * 0.83f, top + carH * 0.20f, p
            )
            canvas.drawRect(
                left + carW * 0.91f, top + carH * 0.08f,
                left + carW * 0.94f, top + carH * 0.20f, p
            )
        }

        private fun drawDriver(canvas: Canvas, x: Float, y: Float) {
            val scale = resources.displayMetrics.density
            val head = dp(10).toFloat()
            val torsoTop = y + dp(14)
            val torsoBottom = y + dp(47)

            p.strokeWidth = dp(5).toFloat()
            p.strokeCap = Paint.Cap.ROUND
            p.color = Color.rgb(225, 230, 240)

            // Head and visor.
            p.color = Color.rgb(220, 180, 145)
            canvas.drawCircle(x, y, head, p)

            p.color = Color.rgb(8, 16, 29)
            canvas.drawArc(
                RectF(x - head, y - head, x + head, y + head),
                185f, 175f, false, p
            )

            // Driver torso.
            p.color = skinColors[skinIndex]
            canvas.drawRoundRect(
                RectF(x - dp(10), torsoTop, x + dp(10), torsoBottom),
                dp(5).toFloat(), dp(5).toFloat(), p
            )

            when (poseIndex) {
                0 -> {
                    // Standing: arms out slightly and legs apart.
                    p.color = Color.rgb(220, 230, 240)
                    canvas.drawLine(
                        x - dp(6).toFloat(), y + dp(22).toFloat(),
                        x - dp(19).toFloat(), y + dp(39).toFloat(), p
                    )
                    canvas.drawLine(
                        x + dp(6).toFloat(), y + dp(22).toFloat(),
                        x + dp(18).toFloat(), y + dp(36).toFloat(), p
                    )
                    canvas.drawLine(
                        x - dp(5).toFloat(), torsoBottom,
                        x - dp(12).toFloat(), y + dp(65).toFloat(), p
                    )
                    canvas.drawLine(
                        x + dp(5).toFloat(), torsoBottom,
                        x + dp(12).toFloat(), y + dp(65).toFloat(), p
                    )
                }

                1 -> {
                    // Sitting pose, legs extended toward the car.
                    p.color = Color.rgb(220, 230, 240)
                    canvas.drawLine(
                        x - dp(6).toFloat(), y + dp(22).toFloat(),
                        x - dp(18).toFloat(), y + dp(37).toFloat(), p
                    )
                    canvas.drawLine(
                        x + dp(6).toFloat(), y + dp(22).toFloat(),
                        x + dp(18).toFloat(), y + dp(35).toFloat(), p
                    )
                    canvas.drawLine(
                        x - dp(5).toFloat(), torsoBottom,
                        x + dp(10).toFloat(), y + dp(58).toFloat(), p
                    )
                    canvas.drawLine(
                        x + dp(5).toFloat(), torsoBottom,
                        x + dp(26).toFloat(), y + dp(58).toFloat(), p
                    )
                }

                else -> {
                    // Victory pose with raised arms.
                    p.color = Color.rgb(220, 230, 240)
                    canvas.drawLine(
                        x - dp(6).toFloat(), y + dp(22).toFloat(),
                        x - dp(18).toFloat(), y + dp(7).toFloat(), p
                    )
                    canvas.drawLine(
                        x + dp(6).toFloat(), y + dp(22).toFloat(),
                        x + dp(18).toFloat(), y + dp(7).toFloat(), p
                    )
                    canvas.drawLine(
                        x - dp(5).toFloat(), torsoBottom,
                        x - dp(12).toFloat(), y + dp(65).toFloat(), p
                    )
                    canvas.drawLine(
                        x + dp(5).toFloat(), torsoBottom,
                        x + dp(12).toFloat(), y + dp(65).toFloat(), p
                    )
                }
            }

            // Emote bubble.
            val bubbleX = x + dp(25)
            val bubbleY = y - dp(13)

            p.color = Color.argb(235, 5, 15, 32)
            canvas.drawRoundRect(
                RectF(
                    bubbleX - dp(4),
                    bubbleY - dp(12),
                    bubbleX + dp(48),
                    bubbleY + dp(12)
                ),
                dp(7).toFloat(), dp(7).toFloat(), p
            )

            p.color = neon
            p.textSize = dp(8).toFloat()
            p.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            canvas.drawText(emoteNames[emoteIndex], bubbleX, bubbleY + dp(3).toFloat(), p)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dragX = event.x
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    val delta = event.x - dragX
                    rotation = (rotation + delta * 0.7f) % 360f
                    dragX = event.x
                    invalidate()
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    performClick()
                    return true
                }

                MotionEvent.ACTION_CANCEL -> return true
            }

            return true
        }

        override fun performClick(): Boolean {
            super.performClick()
            return true
        }
    }
}
