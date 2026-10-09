
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
    private var infoTitle = "INFORMATION"
    private var infoMessage = ""
    private var shopCategory = "Cars"

    private lateinit var progressBar: ProgressBar
    private lateinit var loadingText: TextView

    init {
        setBackgroundColor(Color.rgb(3, 6, 14))
        showLoadingScreen()
        animateLoading()
    }

    private fun fullParams() = LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
    )

    private fun params(w: Int, h: Int, gravity: Int) =
        FrameLayout.LayoutParams(w, h).apply {
            this.gravity = gravity
        }

    private fun addBackground(road: Boolean) {
        addView(
            RacingBackgroundView(context).apply { showRoad = road },
            fullParams()
        )
    }

    private fun showLoadingScreen() {
        removeAllViews()
        currentScreen = Screen.LOADING
        addBackground(false)

        val overlay = FrameLayout(context)
        addView(overlay, fullParams())

        val title = createText("SPEED RACERS EDGE")
        title.textSize = 27f
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        overlay.addView(title, params(420, 60, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 90
        })

        val subtitle = createText("THE ROAD HAS NO LIMIT")
        subtitle.textSize = 13f
        subtitle.gravity = Gravity.CENTER
        subtitle.setTextColor(Color.rgb(180, 205, 235))
        overlay.addView(subtitle, params(330, 40, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 148
        })

        loadingText = createText("LOADING GAME...")
        loadingText.textSize = 12f
        loadingText.gravity = Gravity.CENTER
        overlay.addView(loadingText, params(300, 35, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply {
            bottomMargin = 75
        })

        progressBar = ProgressBar(
            context, null, android.R.attr.progressBarStyleHorizontal
        )
        progressBar.max = 100
        progressBar.progress = 0
        overlay.addView(progressBar, params(330, 7, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply {
            bottomMargin = 57
        })
    }

    private fun animateLoading() {
        loadingProgress = 0
        val task = object : Runnable {
            override fun run() {
                if (currentScreen != Screen.LOADING) return
                loadingProgress = min(loadingProgress + 1, 100)
                progressBar.progress = loadingProgress
                loadingText.text = when {
                    loadingProgress < 20 -> "LOADING GAME..."
                    loadingProgress < 40 -> "LOADING VEHICLES..."
                    loadingProgress < 60 -> "LOADING GARAGE..."
                    loadingProgress < 80 -> "LOADING MULTIPLAYER..."
                    loadingProgress < 100 -> "FINALIZING..."
                    else -> "READY!"
                }
                if (loadingProgress < 100) {
                    postDelayed(this, 35)
                } else {
                    postDelayed({
                        if (currentScreen == Screen.LOADING) showTapToStart()
                    }, 250)
                }
            }
        }
        post(task)
    }

    private fun showTapToStart() {
        removeAllViews()
        currentScreen = Screen.TAP_TO_START
        addBackground(true)

        val overlay = FrameLayout(context)
        addView(overlay, fullParams())

        val title = createText("SPEED RACERS EDGE")
        title.textSize = 32f
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        overlay.addView(title, params(430, 70, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 95
        })

        val subtitle = createText("RACE • ESCAPE • DOMINATE")
        subtitle.textSize = 14f
        subtitle.gravity = Gravity.CENTER
        subtitle.setTextColor(Color.rgb(180, 215, 255))
        overlay.addView(subtitle, params(350, 40, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 165
        })

        val tap = createButton("TAP TO START")
        overlay.addView(tap, params(220, 55, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply {
            bottomMargin = 80
        })
        tap.setOnClickListener { showLobby() }
    }

    // LOBBY

    private fun showLobby() {
        removeAllViews()
        currentScreen = Screen.LOBBY
        addBackground(true)

        val overlay = FrameLayout(context)
        addView(overlay, fullParams())

        val profile = createButton("👤 ADAM • LV.1")
        overlay.addView(profile, params(165, 48, Gravity.TOP or Gravity.START).apply {
            leftMargin = 12
            topMargin = 14
        })
        profile.setOnClickListener { showProfile() }

        val coinButton = createButton("🪙 $coins")
        overlay.addView(coinButton, params(100, 42, Gravity.TOP or Gravity.START).apply {
            leftMargin = 182
            topMargin = 17
        })
        coinButton.setOnClickListener { showShop("Coins") }

        val gemButton = createButton("💎 $gems")
        overlay.addView(gemButton, params(95, 42, Gravity.TOP or Gravity.START).apply {
            leftMargin = 286
            topMargin = 17
        })
        gemButton.setOnClickListener { showShop("Gems") }

        val settings = createButton("⚙")
        overlay.addView(settings, params(48, 48, Gravity.TOP or Gravity.END).apply {
            rightMargin = 12
            topMargin = 14
        })
        settings.setOnClickListener { showSettings() }

        val title = createText("SPEED RACERS EDGE")
        title.textSize = 20f
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        overlay.addView(title, params(300, 42, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 68
        })

        val carPanel = createPanel()
        val car = createText("🏎️")
        car.textSize = 78f
        car.gravity = Gravity.CENTER
        carPanel.addView(car, fullParams())
        overlay.addView(carPanel, params(250, 135, Gravity.CENTER).apply {
            topMargin = -20
        })

        val teamPanel = createPanel()
        val teamTitle = createText("TEAM  $teamPlayers/4")
        teamTitle.textSize = 15f
        teamTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        teamTitle.gravity = Gravity.CENTER
        teamPanel.addView(teamTitle, params(
            LayoutParams.MATCH_PARENT, 38, Gravity.TOP
        ))

        for (i in 0 until 4) {
            val text = when {
                i == 0 -> "👤 ADAM\nTEAM LEADER"
                i < teamPlayers -> "👤 TEAMMATE ${i + 1}\nIN TEAM"
                else -> "+ INVITE FRIEND"
            }
            val slot = createButton(text)
            slot.textSize = 10f
            teamPanel.addView(slot, FrameLayout.LayoutParams(116, 58).apply {
                leftMargin = 10 + (i % 2) * 123
                topMargin = 42 + (i / 2) * 65
            })
            if (i >= teamPlayers) slot.setOnClickListener { showFriends() }
        }

        overlay.addView(teamPanel, params(255, 178, Gravity.TOP or Gravity.END).apply {
            rightMargin = 12
            topMargin = 120
        })

        val shop = createButton("SHOP")
        overlay.addView(shop, params(100, 48, Gravity.BOTTOM or Gravity.START).apply {
            leftMargin = 12
            bottomMargin = 18
        })
        shop.setOnClickListener { showShop("Cars") }

        val friends = createButton("FRIENDS")
        overlay.addView(friends, params(105, 48, Gravity.BOTTOM or Gravity.START).apply {
            leftMargin = 120
            bottomMargin = 18
        })
        friends.setOnClickListener { showFriends() }

        val mode = createButton("SELECT MODE")
        overlay.addView(mode, params(145, 52, Gravity.BOTTOM or Gravity.END).apply {
            rightMargin = 12
            bottomMargin = 18
        })
        mode.setOnClickListener { showModeMenu() }
    }

    // SETTINGS

    private fun showSettings() {
        removeAllViews()
        currentScreen = Screen.SETTINGS
        addBackground(false)
        val overlay = FrameLayout(context)
        addView(overlay, fullParams())
        addPageTitle(overlay, "SETTINGS")

        val categories = arrayOf(
            "Gameplay", "Controls", "Graphics", "Sound",
            "Language", "Account", "About"
        )

        for (i in categories.indices) {
            val item = createButton(categories[i])
            overlay.addView(item, params(270, 48, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                topMargin = 105 + i * 55
            })
            item.setOnClickListener {
                when (categories[i]) {
                    "Controls" -> showControls()
                    "Gameplay" -> showInfoPage("GAMEPLAY", "Race preferences will appear here.")
                    "Graphics" -> showInfoPage("GRAPHICS", "Graphics options will appear here.")
                    "Sound" -> showInfoPage("SOUND", "Sound settings will appear here.")
                    "Language" -> showInfoPage("LANGUAGE", "English")
                    "Account" -> showInfoPage("ACCOUNT", "Account settings will appear here.")
                    "About" -> showInfoPage("ABOUT", "SPEED RACERS EDGE")
                }
            }
        }
        addBackButton(overlay) { showLobby() }
    }

    private fun showControls() {
        removeAllViews()
        currentScreen = Screen.CONTROLS
        addBackground(false)
        val overlay = FrameLayout(context)
        addView(overlay, fullParams())
        addPageTitle(overlay, "CONTROLS")

        val options = arrayOf("Tilt", "Steering Wheel", "Arrow Buttons", "Touch Steering")
        for (i in options.indices) {
            val label = if (selectedControl == options[i]) "✓  ${options[i]}" else options[i]
            val item = createButton(label)
            overlay.addView(item, params(280, 55, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                topMargin = 135 + i * 70
            })
            item.setOnClickListener {
                selectedControl = options[i]
                showControls()
            }
        }

        val note = createText("SELECT YOUR PREFERRED DRIVING CONTROL")
        note.textSize = 11f
        note.gravity = Gravity.CENTER
        overlay.addView(note, params(320, 40, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply {
            bottomMargin = 85
        })
        addBackButton(overlay) { showSettings() }
    }

    // SHOP

    private fun showShop(category: String) {
        shopCategory = category
        removeAllViews()
        currentScreen = Screen.SHOP
        addBackground(false)
        val overlay = FrameLayout(context)
        addView(overlay, fullParams())
        addPageTitle(overlay, "SHOP")

        val balances = createText("🪙 $coins        💎 $gems")
        balances.gravity = Gravity.CENTER
        balances.textSize = 15f
        overlay.addView(balances, params(260, 40, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 65
        })

        val categories = arrayOf(
            "Cars", "Drivers", "Car Skins", "Vehicle Effects",
            "Emotes", "Arrival & Finish Effects", "Coins", "Gems"
        )

        for (i in categories.indices) {
            val item = createButton(
                if (shopCategory == categories[i]) "✓  ${categories[i]}" else categories[i]
            )
            overlay.addView(item, params(280, 43, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                topMargin = 112 + i * 49
            })
            item.setOnClickListener { showShop(categories[i]) }
        }

        addBackButton(overlay) { showLobby() }
    }

    // FRIENDS AND LOCAL INVITATIONS

    private fun showFriends() {
        removeAllViews()
        currentScreen = Screen.FRIENDS
        addBackground(false)
        val overlay = FrameLayout(context)
        addView(overlay, fullParams())
        addPageTitle(overlay, "FRIENDS")

        val friendNames = arrayOf(
            "ALEX  •  ONLINE",
            "MIKE  •  ONLINE",
            "JOHN  •  OFFLINE"
        )

        for (i in friendNames.indices) {
            val friend = createButton(friendNames[i])
            overlay.addView(friend, params(280, 55, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                topMargin = 120 + i * 70
            })

            if (i < 2) {
                friend.setOnClickListener {
                    if (teamPlayers >= 4) {
                        Toast.makeText(context, "TEAM IS FULL", Toast.LENGTH_SHORT).show()
                    } else {
                        showInviteConfirmation(friendNames[i].substringBefore(" •"))
                    }
                }
            }
        }
        addBackButton(overlay) { showLobby() }
    }

    private fun showInviteConfirmation(friendName: String) {
        val dialog = android.app.AlertDialog.Builder(context)
            .setTitle("INVITE TO TEAM")
            .setMessage("Send a team invitation to $friendName?")
            .setPositiveButton("INVITE") { _, _ ->
                teamPlayers = (teamPlayers + 1).coerceAtMost(4)
                Toast.makeText(context, "$friendName invited (local preview)", Toast.LENGTH_SHORT).show()
                showLobby()
            }
            .setNegativeButton("CANCEL", null)
            .create()
        dialog.show()
    }

    private fun showInvitePopup(overlay: FrameLayout) {
        val popup = createPanel()
        val text = createText("TEAM INVITATION\nA friend invited you")
        text.textSize = 12f
        popup.addView(text, params(180, 55, Gravity.TOP or Gravity.CENTER_HORIZONTAL))

        val accept = createButton("ACCEPT")
        popup.addView(accept, params(85, 38, Gravity.BOTTOM or Gravity.START).apply {
            leftMargin = 8
            bottomMargin = 8
        })
        val decline = createButton("DECLINE")
        popup.addView(decline, params(85, 38, Gravity.BOTTOM or Gravity.END).apply {
            rightMargin = 8
            bottomMargin = 8
        })

        overlay.addView(popup, params(205, 110, Gravity.TOP or Gravity.END).apply {
            topMargin = 72
            rightMargin = 12
        })

        accept.setOnClickListener {
            pendingInvite = false
            teamPlayers = (teamPlayers + 1).coerceAtMost(4)
            showLobby()
        }
        decline.setOnClickListener {
            pendingInvite = false
            showLobby()
        }
    }

    // PROFILE

    private fun showProfile() {
        removeAllViews()
        currentScreen = Screen.PROFILE
        addBackground(false)
        val overlay = FrameLayout(context)
        addView(overlay, fullParams())
        addPageTitle(overlay, "PLAYER PROFILE")

        val details = createPanel()
        val text = createText(
            "👤  ADAM\n\nLEVEL 1\n\nRACE COINS: $coins\nGEMS: $gems\n\nTEAM MEMBERS: $teamPlayers/4"
        )
        text.textSize = 16f
        text.gravity = Gravity.CENTER
        details.addView(text, fullParams())
        overlay.addView(details, params(300, 270, Gravity.CENTER))
        addBackButton(overlay) { showLobby() }
    }

    // MODE SELECTION

    private fun showModeMenu() {
        removeAllViews()
        currentScreen = Screen.MODE_MENU
        addBackground(true)
        val overlay = FrameLayout(context)
        addView(overlay, fullParams())
        addPageTitle(overlay, "SELECT MODE")

        val modes = arrayOf(
            "QUICK RACE", "ONLINE RACE", "HEIST",
            "PRIVATE LOBBY", "SPECIAL EVENTS"
        )

        for (i in modes.indices) {
            val item = createButton(modes[i])
            overlay.addView(item, params(280, 52, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                topMargin = 120 + i * 62
            })
            item.setOnClickListener {
                selectedMode = modes[i]
                if (selectedMode == "QUICK RACE" || selectedMode == "ONLINE RACE") {
                    showRaceStarting()
                } else {
                    showInfoPage(selectedMode, "This mode is planned for a future update.")
                }
            }
        }
        addBackButton(overlay) { showLobby() }
    }

    private fun showRaceStarting() {
        removeAllViews()
        currentScreen = Screen.RACE_STARTING
        addBackground(true)
        val overlay = FrameLayout(context)
        addView(overlay, fullParams())

        addPageTitle(overlay, selectedMode)

        val status = createText("TEAM MEMBERS: $teamPlayers/4")
        status.gravity = Gravity.CENTER
        status.textSize = 15f
        overlay.addView(status, params(300, 45, Gravity.CENTER).apply {
            topMargin = -20
        })

        val ready = createButton(if (playerReady) "READY ✓" else "READY UP")
        overlay.addView(ready, params(190, 55, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply {
            bottomMargin = 80
        })
        ready.setOnClickListener {
            playerReady = !playerReady
            ready.text = if (playerReady) "READY ✓" else "READY UP"
        }
        addBackButton(overlay) { showLobby() }
    }

    // GENERAL INFORMATION PAGE

    private fun showInfoPage(titleText: String, message: String) {
        infoTitle = titleText
        infoMessage = message
        removeAllViews()
        currentScreen = Screen.INFO
        addBackground(false)
        val overlay = FrameLayout(context)
        addView(overlay, fullParams())
        addPageTitle(overlay, infoTitle)

        val panel = createPanel()
        val body = createText(infoMessage)
        body.textSize = 16f
        body.gravity = Gravity.CENTER
        panel.addView(body, fullParams())
        overlay.addView(panel, params(300, 180, Gravity.CENTER))
        addBackButton(overlay) { showSettings() }
    }

    // SHARED UI HELPERS

    private fun addPageTitle(overlay: FrameLayout, titleText: String) {
        val title = createText(titleText)
        title.textSize = 23f
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        overlay.addView(title, params(330, 55, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 25
        })
    }

    private fun addBackButton(overlay: FrameLayout, action: () -> Unit) {
        val back = createButton("← BACK")
        overlay.addView(back, params(125, 48, Gravity.BOTTOM or Gravity.START).apply {
            leftMargin = 15
            bottomMargin = 18
        })
        back.setOnClickListener { action() }
    }

    private fun createPanel(): FrameLayout {
        val panel = FrameLayout(context)
        val bg = GradientDrawable()
        bg.setColor(Color.argb(190, 5, 15, 30))
        bg.setStroke(1, Color.argb(180, 70, 145, 220))
        bg.cornerRadius = 18f
        panel.background = bg
        panel.setPadding(10, 8, 10, 8)
        return panel
    }

    private fun createText(value: String): TextView {
        return TextView(context).apply {
            text = value
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    private fun createButton(value: String): TextView {
        val button = TextView(context)
        button.text = value
        button.textSize = 13f
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        button.setTextColor(Color.WHITE)
        button.gravity = Gravity.CENTER
        button.setPadding(6, 4, 6, 4)

        val bg = GradientDrawable()
        bg.setColor(Color.argb(200, 8, 24, 48))
        bg.setStroke(2, Color.argb(200, 55, 145, 255))
        bg.cornerRadius = 16f
        button.background = bg
        button.isClickable = true
        return button
    }

    // ANIMATED CITY, WATER AND BRIDGE BACKGROUND

    private class RacingBackgroundView(context: Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val waterPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var animationOffset = 0f

        var showRoad = true

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            post(object : Runnable {
                override fun run() {
                    animationOffset = (animationOffset + 2.8f) % 100f
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

            paint.shader = LinearGradient(
                0f, 0f, 0f, h * 0.6f,
                Color.rgb(5, 14, 34),
                Color.rgb(23, 57, 92),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w, h * 0.6f, paint)
            paint.shader = null

            // Distant city skyline
            val cityBase = h * 0.54f
            var x = 0f
            var index = 0
            while (x < w) {
                val bw = when (index % 5) {
                    0 -> 38f
                    1 -> 55f
                    2 -> 30f
                    3 -> 70f
                    else -> 45f
                }
                val bh = h * when (index % 6) {
                    0 -> 0.22f
                    1 -> 0.31f
                    2 -> 0.17f
                    3 -> 0.27f
                    4 -> 0.20f
                    else -> 0.35f
                }
                val top = cityBase - bh
                paint.color = when (index % 3) {
                    0 -> Color.rgb(10, 24, 47)
                    1 -> Color.rgb(15, 32, 58)
                    else -> Color.rgb(8, 20, 42)
                }
                canvas.drawRect(x, top, x + bw, cityBase, paint)

                var wy = top + 12f
                while (wy < cityBase - 8f) {
                    var wx = x + 7f
                    while (wx < x + bw - 6f) {
                        if ((wx.toInt() + wy.toInt() + index) % 4 != 0) {
                            paint.color = if ((index + wx.toInt()) % 3 == 0) {
                                Color.rgb(255, 190, 70)
                            } else {
                                Color.rgb(70, 170, 255)
                            }
                            canvas.drawRect(wx, wy, wx + 5f, wy + 4f, paint)
                        }
                        wx += 12f
                    }
                    wy += 15f
                }
                x += bw + 5f
                index++
            }

            val horizon = h * 0.545f

            // Water covers both sides and the lower scene
            waterPaint.shader = LinearGradient(
                0f, horizon, 0f, h,
                Color.rgb(22, 78, 118),
                Color.rgb(3, 23, 45),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, horizon, w, h, waterPaint)
            waterPaint.shader = null

            for (i in 0 until 30) {
                val depth = i / 30f
                val y = horizon + depth * (h - horizon)
                val rw = 15f + depth * 75f
                paint.color = when (i % 3) {
                    0 -> Color.argb(120, 70, 165, 235)
                    1 -> Color.argb(90, 255, 70, 90)
                    else -> Color.argb(75, 120, 210, 255)
                }
                canvas.drawRect(w * 0.16f - rw, y, w * 0.16f + rw, y + 2f, paint)
                canvas.drawRect(w * 0.84f - rw, y, w * 0.84f + rw, y + 2f, paint)
            }

            for (i in 0 until 18) {
                val y = horizon + 18f + i * 30f
                val offset = (animationOffset * (0.15f + i * 0.01f)) % 80f
                paint.color = Color.argb(80, 80, 180, 255)
                canvas.drawRect(-40f + offset, y, 80f + offset, y + 2f, paint)
                canvas.drawRect(w - 80f - offset, y, w + 40f - offset, y + 2f, paint)
            }

            if (showRoad) {
                val topWidth = w * 0.075f
                val bottomWidth = w * 0.84f
                val bottom = h * 1.10f

                val bridge = Path().apply {
                    moveTo(w / 2f - topWidth, horizon)
                    lineTo(w / 2f + topWidth, horizon)
                    lineTo(w / 2f + bottomWidth, bottom)
                    lineTo(w / 2f - bottomWidth, bottom)
                    close()
                }

                roadPaint.color = Color.rgb(23, 28, 38)
                canvas.drawPath(bridge, roadPaint)

                linePaint.style = Paint.Style.STROKE
                linePaint.strokeWidth = 5f

                linePaint.color = Color.rgb(55, 145, 255)
                canvas.drawLine(
                    w / 2f - topWidth, horizon,
                    w / 2f - bottomWidth, bottom, linePaint
                )

                linePaint.color = Color.rgb(255, 55, 70)
                canvas.drawLine(
                    w / 2f + topWidth, horizon,
                    w / 2f + bottomWidth, bottom, linePaint
                )

                for (i in 0 until 15) {
                    val depth = i / 15f
                    val y = horizon + (bottom - horizon) * depth
                    val half = topWidth + (bottomWidth - topWidth) * depth
                    linePaint.strokeWidth = 2f + depth * 2f
                    linePaint.color = Color.rgb(65, 160, 255)
                    canvas.drawLine(w / 2f - half, y, w / 2f - half, y - 5f - depth * 20f, linePaint)
                    linePaint.color = Color.rgb(255, 70, 80)
                    canvas.drawLine(w / 2f + half, y, w / 2f + half, y - 5f - depth * 20f, linePaint)
                }

                for (i in 0 until 12) {
                    val depth = ((i + animationOffset / 100f) % 12) / 12f
                    val y = horizon + (bottom - horizon) * depth
                    val half = topWidth + (bottomWidth - topWidth) * depth
                    val radius = 2f + depth * 5f
                    paint.color = Color.rgb(160, 220, 255)
                    canvas.drawCircle(w / 2f - half * 0.78f, y, radius, paint)
                    canvas.drawCircle(w / 2f + half * 0.78f, y, radius, paint)
                }

                for (i in 0 until 12) {
                    val d1 = i / 12f
                    val d2 = min((i + 0.45f) / 12f, 1f)
                    val y1 = horizon + (bottom - horizon) * d1
                    val y2 = horizon + (bottom - horizon) * d2
                    linePaint.strokeWidth = 2f + d1 * 4f
                    linePaint.color = Color.WHITE
                    canvas.drawLine(w / 2f, y1, w / 2f, y2, linePaint)
                }
            }

            // Dark vignette
            paint.shader = LinearGradient(
                0f, 0f, 0f, h,
                Color.argb(10, 0, 0, 0),
                Color.argb(100, 0, 0, 0),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null
        }
    }
}
