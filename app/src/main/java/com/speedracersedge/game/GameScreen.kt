
package com.speedracersedge.game

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class GameScreen(context: Context) : FrameLayout(context) {

    private enum class Screen {
        LOADING, TAP_TO_START, LOBBY, INVENTORY, GARAGE,
        SHOP, FRIENDS, SETTINGS, PROFILE, MODE
    }

    private var currentScreen = Screen.LOADING
    private var progress = 0
    private var coins = 0
    private var gems = 0
    private var selectedMode = "ONLINE RACE"
    private var ready = false
    private var teamSize = 1
    private var shopCategory = "Cars"

    private val cyan = Color.rgb(0, 220, 255)
    private val gold = Color.rgb(255, 198, 62)
    private val panel = Color.rgb(9, 18, 35)

    private val background = CityBackground(context)
    private val content = FrameLayout(context)

    init {
        setBackgroundColor(Color.rgb(3, 7, 17))
        addView(background, fullParams())
        addView(content, fullParams())
        showLoading()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun fullParams() = LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
    )

    private fun params(
        width: Int,
        height: Int,
        gravity: Int = Gravity.TOP or Gravity.START
    ) = LayoutParams(
        if (width == -1) LayoutParams.MATCH_PARENT else dp(width),
        if (height == -1) LayoutParams.MATCH_PARENT else dp(height),
        gravity
    )

    private fun clear() {
        content.removeAllViews()
    }

    private fun setCityBackground(showCity: Boolean = true) {
        background.showCity = showCity
        background.invalidate()
    }

    private fun label(
        value: String,
        size: Float = 12f,
        color: Int = Color.WHITE,
        bold: Boolean = false
    ) = TextView(context).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.CENTER
        typeface = Typeface.create(
            "sans-serif",
            if (bold) Typeface.BOLD else Typeface.NORMAL
        )
        setPadding(dp(5), dp(3), dp(5), dp(3))
    }

    private fun button(
        value: String,
        action: () -> Unit
    ) = label(value, 11f, Color.WHITE, true).apply {
        isClickable = true
        isFocusable = true
        background = rounded(panel, cyan, 1)
        setOnClickListener { action() }
    }

    private fun rounded(
        fill: Int,
        stroke: Int = cyan,
        strokeWidth: Int = 1
    ) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(9).toFloat()
        setColor(fill)
        if (strokeWidth > 0) {
            setStroke(dp(strokeWidth), stroke)
        }
    }

    // ---------------------------------------------------------
    // LOADING SCREEN
    // ---------------------------------------------------------

    private fun showLoading() {
        currentScreen = Screen.LOADING
        clear()
        setCityBackground()

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(22), dp(22), dp(35))
        }

        root.addView(
            label("SPEED RACERS EDGE", 25f, Color.WHITE, true),
            LinearLayout.LayoutParams(-1, dp(48))
        )

        root.addView(
            label("PREPARE TO RACE", 12f, cyan, true),
            LinearLayout.LayoutParams(-1, dp(30))
        )

        val progressLabel = label("LOADING 0%", 11f, Color.WHITE, true)
        root.addView(
            progressLabel,
            LinearLayout.LayoutParams(-1, dp(27))
        )

        val track = FrameLayout(context).apply {
            background = rounded(Color.DKGRAY, cyan, 1)
        }

        val fill = View(context).apply {
            background = rounded(cyan, cyan, 0)
        }

        track.addView(
            fill,
            FrameLayout.LayoutParams(0, dp(6), Gravity.START)
        )

        root.addView(track, LinearLayout.LayoutParams(-1, dp(8)))
        content.addView(root, fullParams())

        progress = 0

        val task = object : Runnable {
            override fun run() {
                if (currentScreen != Screen.LOADING) return

                progress = (progress + 5).coerceAtMost(100)
                progressLabel.text = "LOADING $progress%"

                fill.layoutParams = FrameLayout.LayoutParams(
                    (track.width * progress / 100f).toInt(),
                    dp(6),
                    Gravity.START
                )

                if (progress < 100) {
                    postDelayed(this, 65L)
                } else {
                    postDelayed({
                        if (currentScreen == Screen.LOADING) {
                            showTapToStart()
                        }
                    }, 250L)
                }
            }
        }

        postDelayed(task, 250L)
    }

    private fun showTapToStart() {
        currentScreen = Screen.TAP_TO_START
        clear()
        setCityBackground()

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setPadding(dp(20), dp(20), dp(20), dp(35))
            isClickable = true
            setOnClickListener { showLobby() }
        }

        root.addView(
            label("SPEED RACERS EDGE", 27f, Color.WHITE, true),
            LinearLayout.LayoutParams(-1, dp(50))
        )

        root.addView(
            label("T A P   T O   S T A R T", 14f, cyan, true),
            LinearLayout.LayoutParams(-1, dp(40))
        )

        content.addView(root, fullParams())
    }

    // ---------------------------------------------------------
    // MAIN LOBBY - NOT A GARAGE
    // ---------------------------------------------------------

    private fun showLobby() {
        currentScreen = Screen.LOBBY
        clear()
        setCityBackground()

        val root = FrameLayout(context)
        content.addView(root, fullParams())

        val top = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        top.addView(
            button("ADAM • LV.1") { showProfile() },
            LinearLayout.LayoutParams(0, dp(40), 1.15f)
        )

        top.addView(
            label("◈ $coins", 11f, Color.WHITE, true),
            LinearLayout.LayoutParams(0, dp(40), 0.85f)
        )

        top.addView(
            label("◆ $gems", 11f, gold, true),
            LinearLayout.LayoutParams(0, dp(40), 0.85f)
        )

        top.addView(
            button("⚙") { showSettings() },
            LinearLayout.LayoutParams(dp(43), dp(40))
        )

        root.addView(
            top,
            params(-1, 44).apply {
                leftMargin = dp(7)
                rightMargin = dp(7)
                topMargin = dp(7)
            }
        )

        root.addView(
            label("THE CITY IS YOURS", 23f, Color.WHITE, true),
            params(-1, 45).apply { topMargin = dp(68) }
        )

        root.addView(
            label("RACE • COMPETE • ESCAPE", 11f, cyan, true),
            params(-1, 28).apply { topMargin = dp(110) }
        )

        val feature = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = rounded(
                Color.argb(218, 7, 16, 33),
                Color.rgb(35, 92, 125),
                1
            )
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }

        feature.addView(
            label("STREET DOMINANCE", 19f, Color.WHITE, true),
            LinearLayout.LayoutParams(-1, dp(38))
        )

        feature.addView(
            label(
                "Build your collection. Assemble your crew.\nChoose a mode and hit the streets.",
                12f,
                Color.LTGRAY
            ),
            LinearLayout.LayoutParams(-1, dp(55))
        )

        feature.addView(
            button("SELECT MODE  →") { showMode() },
            LinearLayout.LayoutParams(-1, dp(43))
        )

        root.addView(
            feature,
            params(-1, 180, Gravity.CENTER).apply {
                leftMargin = dp(22)
                rightMargin = dp(22)
                topMargin = dp(-5)
            }
        )

        val crew = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = rounded(
                Color.argb(220, 5, 12, 25),
                cyan,
                1
            )
        }

        crew.addView(
            label("CREW $teamSize/4", 10f, Color.WHITE, true),
            LinearLayout.LayoutParams(0, dp(40), 1f)
        )

        crew.addView(
            button("FRIENDS") { showFriends() },
            LinearLayout.LayoutParams(0, dp(40), 1f)
        )

        crew.addView(
            button(if (ready) "READY ✓" else "READY UP") {
                ready = !ready
                showLobby()
            },
            LinearLayout.LayoutParams(0, dp(40), 1f)
        )

        root.addView(
            crew,
            params(-1, 44, Gravity.BOTTOM).apply {
                leftMargin = dp(9)
                rightMargin = dp(9)
                bottomMargin = dp(112)
            }
        )

        val nav = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(5), dp(5), dp(5), dp(5))
            background = rounded(
                Color.rgb(4, 10, 22),
                Color.rgb(38, 65, 88),
                1
            )
        }

        fun navItem(title: String, action: () -> Unit) {
            nav.addView(
                button(title, action),
                LinearLayout.LayoutParams(0, dp(49), 1f).apply {
                    leftMargin = dp(2)
                    rightMargin = dp(2)
                }
            )
        }

        navItem("SHOP") { showShop() }
        navItem("INVENTORY") { showInventory() }
        navItem("GARAGE") { showGarage() }
        navItem("MODE") { showMode() }

        root.addView(
            nav,
            params(-1, 59, Gravity.BOTTOM).apply {
                leftMargin = dp(7)
                rightMargin = dp(7)
                bottomMargin = dp(7)
            }
        )
    }

    // ---------------------------------------------------------
    // CHARACTER INVENTORY - REAL 3D CHARACTER MODEL
    // ---------------------------------------------------------

    private fun showInventory() {
        currentScreen = Screen.INVENTORY
        clear()
        setCityBackground(false)

        val root = pageRoot("CHARACTER INVENTORY")

        root.addView(
            label("YOUR CHARACTER • CASUAL OUTFIT", 11f, cyan, true),
            params(-1, 30).apply { topMargin = dp(58) }
        )

        val model = ModelPreviewView(
            context,
            "characters/Casual Character.glb"
        )

        root.addView(
            model,
            params(-1, -1).apply {
                topMargin = dp(91)
                leftMargin = dp(12)
                rightMargin = dp(12)
                bottomMargin = dp(184)
            }
        )

        root.addView(
            label("OWNED CHARACTER ITEMS", 12f, Color.WHITE, true),
            params(-1, 27, Gravity.BOTTOM).apply {
                bottomMargin = dp(154)
            }
        )

        val items = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(
                Color.argb(235, 5, 13, 29),
                cyan,
                1
            )
            setPadding(dp(8), dp(5), dp(8), dp(5))
        }

        items.addView(
            label("✓ Casual Character • EQUIPPED", 11f, Color.WHITE, true),
            LinearLayout.LayoutParams(-1, dp(30))
        )

        items.addView(
            label("Outfits: 1 owned     Emotes: 0 unlocked", 10f, Color.LTGRAY),
            LinearLayout.LayoutParams(-1, dp(29))
        )

        items.addView(
            label("Locked cosmetics belong in the Shop.", 10f, gold),
            LinearLayout.LayoutParams(-1, dp(27))
        )

        root.addView(
            items,
            params(-1, 94, Gravity.BOTTOM).apply {
                leftMargin = dp(12)
                rightMargin = dp(12)
                bottomMargin = dp(54)
            }
        )

        addBottomBack(root)
    }

    // ---------------------------------------------------------
    // VEHICLE GARAGE - REAL 3D CAR MODEL
    // ---------------------------------------------------------

    private fun showGarage() {
        currentScreen = Screen.GARAGE
        clear()
        setCityBackground(false)

        val root = pageRoot("VEHICLE GARAGE")

        root.addView(
            label("YOUR VEHICLES • SPORTS CAR", 11f, cyan, true),
            params(-1, 30).apply { topMargin = dp(58) }
        )

        val model = ModelPreviewView(
            context,
            "cars/Sports Car.glb"
        )

        root.addView(
            model,
            params(-1, -1).apply {
                topMargin = dp(91)
                leftMargin = dp(12)
                rightMargin = dp(12)
                bottomMargin = dp(190)
            }
        )

        root.addView(
            label("OWNED VEHICLES", 12f, Color.WHITE, true),
            params(-1, 27, Gravity.BOTTOM).apply {
                bottomMargin = dp(158)
            }
        )

        val owned = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(
                Color.argb(235, 5, 13, 29),
                cyan,
                1
            )
            setPadding(dp(8), dp(5), dp(8), dp(5))
        }

        owned.addView(
            label("✓ Sports Car • SELECTED", 11f, Color.WHITE, true),
            LinearLayout.LayoutParams(-1, dp(30))
        )

        owned.addView(
            label("Vehicle skins: 0 unlocked", 10f, Color.LTGRAY),
            LinearLayout.LayoutParams(-1, dp(28))
        )

        owned.addView(
            button("VEHICLE SHOP") {
                shopCategory = "Cars"
                showShop()
            },
            LinearLayout.LayoutParams(-1, dp(34))
        )

        root.addView(
            owned,
            params(-1, 98, Gravity.BOTTOM).apply {
                leftMargin = dp(12)
                rightMargin = dp(12)
                bottomMargin = dp(54)
            }
        )

        addBottomBack(root)
    }

    // ---------------------------------------------------------
    // COMMON PAGE LAYOUT
    // ---------------------------------------------------------

    private fun pageRoot(title: String): FrameLayout {
        val root = FrameLayout(context)
        content.addView(root, fullParams())

        root.addView(
            label(title, 17f, Color.WHITE, true).apply {
                background = rounded(
                    Color.argb(235, 5, 14, 30),
                    cyan,
                    1
                )
            },
            params(-1, 46).apply {
                leftMargin = dp(10)
                rightMargin = dp(10)
                topMargin = dp(8)
            }
        )

        return root
    }

    private fun addBottomBack(root: FrameLayout) {
        root.addView(
            button("← BACK TO LOBBY") { showLobby() },
            params(-1, 42, Gravity.BOTTOM).apply {
                leftMargin = dp(12)
                rightMargin = dp(12)
                bottomMargin = dp(7)
            }
        )
    }

    // ---------------------------------------------------------
    // SELECT MODE
    // ---------------------------------------------------------

    private fun showMode() {
        currentScreen = Screen.MODE
        clear()
        setCityBackground(false)

        val root = pageRoot("SELECT GAME MODE")

        val options = listOf(
            "QUICK RACE",
            "ONLINE RACE",
            "HEIST",
            "PRIVATE LOBBY",
            "SPECIAL EVENTS"
        )

        options.forEachIndexed { index, mode ->
            root.addView(
                button(
                    (if (selectedMode == mode) "✓  " else "") + mode
                ) {
                    selectedMode = mode
                    showMode()
                },
                params(-1, 45).apply {
                    topMargin = dp(67 + index * 53)
                    leftMargin = dp(12)
                    rightMargin = dp(12)
                }
            )
        }

        root.addView(
            label("Selected: $selectedMode", 11f, cyan, true),
            params(-1, 34, Gravity.BOTTOM).apply {
                bottomMargin = dp(64)
            }
        )

        root.addView(
            button("CONFIRM MODE") {
                Toast.makeText(
                    context,
                    "$selectedMode selected. Racing and matchmaking are not connected yet.",
                    Toast.LENGTH_LONG
                ).show()
                showLobby()
            },
            params(-1, 43, Gravity.BOTTOM).apply {
                leftMargin = dp(12)
                rightMargin = dp(12)
                bottomMargin = dp(14)
            }
        )
    }

    // ---------------------------------------------------------
    // SHOP - LOCKED ITEMS ARE SEPARATE FROM OWNED INVENTORY
    // ---------------------------------------------------------

    private fun showShop() {
        currentScreen = Screen.SHOP
        clear()
        setCityBackground(false)

        val root = pageRoot("SHOP")

        root.addView(
            label("◈ $coins Race Coins     ◆ $gems Gems", 12f, gold, true),
            params(-1, 35).apply { topMargin = dp(57) }
        )

        val categories = listOf(
            "Cars",
            "Drivers",
            "Car Skins",
            "Vehicle Effects",
            "Emotes",
            "Arrival & Finish Effects",
            "Coins",
            "Gems"
        )

        val scroll = ScrollView(context)
        val list = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(5), dp(12), dp(12))
        }

        categories.forEach { category ->
            list.addView(
                button(
                    (if (shopCategory == category) "✓  " else "") + category
                ) {
                    shopCategory = category
                    showShop()
                },
                LinearLayout.LayoutParams(-1, dp(41)).apply {
                    bottomMargin = dp(5)
                }
            )
        }

        val note = when (shopCategory) {
            "Coins" ->
                "Race Coins are intended to be earned through gameplay."

            "Gems" ->
                "Gems are premium credits. Real-money purchases are not connected."

            "Drivers" ->
                "Character models and outfits will appear here as unlockable items."

            "Car Skins" ->
                "Owned vehicle skins and locked skins will be shown separately."

            else ->
                "$shopCategory: items and purchases are not connected yet."
        }

        list.addView(
            label(note, 11f, Color.LTGRAY),
            LinearLayout.LayoutParams(-1, dp(75))
        )

        scroll.addView(list)

        root.addView(
            scroll,
            params(-1, -1).apply {
                topMargin = dp(96)
                bottomMargin = dp(55)
            }
        )

        addBottomBack(root)
    }

    // ---------------------------------------------------------
    // FRIENDS AND TEAM
    // ---------------------------------------------------------

    private fun showFriends() {
        currentScreen = Screen.FRIENDS
        clear()
        setCityBackground(false)

        val root = pageRoot("FRIENDS & CREW")

        root.addView(
            label(
                "Invite friends to your team. Maximum 4 players.",
                11f,
                Color.WHITE
            ),
            params(-1, 38).apply { topMargin = dp(58) }
        )

        listOf(
            "Alex • ONLINE",
            "Mike • ONLINE",
            "John • OFFLINE"
        ).forEachIndexed { index, friend ->
            root.addView(
                button(friend) {
                    if (friend.contains("OFFLINE")) {
                        Toast.makeText(
                            context,
                            "Friend is offline.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (teamSize >= 4) {
                        Toast.makeText(
                            context,
                            "Your team is full.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        AlertDialog.Builder(context)
                            .setTitle("Invite ${friend.substringBefore(" •")}?")
                            .setMessage(
                                "This is a local preview. Online invites need a multiplayer backend."
                            )
                            .setNegativeButton("CANCEL", null)
                            .setPositiveButton("INVITE") { _, _ ->
                                teamSize++
                                showFriends()
                            }
                            .show()
                    }
                },
                params(-1, 47).apply {
                    topMargin = dp(105 + index * 55)
                    leftMargin = dp(12)
                    rightMargin = dp(12)
                }
            )
        }

        addBottomBack(root)
    }

    // ---------------------------------------------------------
    // SETTINGS
    // ---------------------------------------------------------

    private fun showSettings() {
        currentScreen = Screen.SETTINGS
        clear()
        setCityBackground(false)

        val root = pageRoot("SETTINGS")

        val settings = listOf(
            "Gameplay",
            "Controls",
            "Graphics",
            "Sound",
            "Account"
        )

        settings.forEachIndexed { index, item ->
            root.addView(
                button(item) {
                    Toast.makeText(
                        context,
                        "$item settings will be connected in a later step.",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                params(-1, 47).apply {
                    topMargin = dp(70 + index * 55)
                    leftMargin = dp(12)
                    rightMargin = dp(12)
                }
            )
        }

        addBottomBack(root)
    }

    // ---------------------------------------------------------
    // PLAYER PROFILE
    // ---------------------------------------------------------

    private fun showProfile() {
        currentScreen = Screen.PROFILE
        clear()
        setCityBackground(false)

        val root = pageRoot("PLAYER PROFILE")

        root.addView(
            label("ADAM", 25f, Color.WHITE, true),
            params(-1, 55).apply { topMargin = dp(78) }
        )

        root.addView(
            label(
                "LEVEL 1\nRace Coins: $coins\nGems: $gems",
                14f,
                Color.LTGRAY
            ),
            params(-1, 115).apply { topMargin = dp(140) }
        )

        addBottomBack(root)
    }

    // ---------------------------------------------------------
    // CITY BACKGROUND - NO GARAGE OR DRAWN CAR
    // ---------------------------------------------------------

    private inner class CityBackground(
        context: Context
    ) : View(context) {

        var showCity = true

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            if (w <= 0f || h <= 0f) return

            paint.shader = LinearGradient(
                0f,
                0f,
                0f,
                h,
                intArrayOf(
                    Color.rgb(3, 7, 21),
                    Color.rgb(10, 24, 48),
                    Color.rgb(4, 9, 22)
                ),
                null,
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null

            if (!showCity) return

            // Distant city skyline.
            paint.color = Color.rgb(8, 18, 36)

            val skylineY = h * 0.42f
            val buildings = 12

            for (i in 0 until buildings) {
                val x = w * i / buildings
                val buildingWidth = w / buildings + dp(3)
                val buildingHeight =
                    h * (0.12f + ((i * 17) % 23) / 100f)

                canvas.drawRect(
                    x,
                    skylineY - buildingHeight,
                    x + buildingWidth,
                    h * 0.88f,
                    paint
                )

                paint.color = if (i % 3 == 0) {
                    Color.rgb(0, 83, 112)
                } else {
                    Color.rgb(16, 39, 62)
                }

                for (row in 0..3) {
                    canvas.drawRect(
                        x + dp(5),
                        skylineY - buildingHeight + dp(9 + row * 15),
                        x + dp(9),
                        skylineY - buildingHeight + dp(14 + row * 15),
                        paint
                    )
                }

                paint.color = Color.rgb(8, 18, 36)
            }

            // Neon horizon.
            paint.color = Color.argb(70, 0, 190, 255)
            canvas.drawRect(
                0f,
                h * 0.66f,
                w,
                h * 0.665f,
                paint
            )

            // Distant road perspective.
            paint.color = Color.rgb(16, 28, 45)
            val roadTop = h * 0.70f
            canvas.drawRect(0f, roadTop, w, h, paint)

            paint.color = Color.rgb(0, 95, 125)
            canvas.drawLine(w * 0.5f, roadTop, 0f, h, paint)
            canvas.drawLine(w * 0.5f, roadTop, w, h, paint)

            paint.color = Color.rgb(110, 65, 45)
            canvas.drawLine(w * 0.5f, roadTop, w * 0.42f, h, paint)
            canvas.drawLine(w * 0.5f, roadTop, w * 0.58f, h, paint)
        }
    }
}
