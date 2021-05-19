package com.brsan7.oct

import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton


@Suppress("DEPRECATION")
class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val tvNotifActEvento = findViewById<TextView>(R.id.tvNotifActEvento)
        val fabSilenceAction = findViewById<FloatingActionButton>(R.id.fabSilenceAction)
        val ivFabAnimation = findViewById<ImageView>(R.id.ivFabAnimation)

        var evento = intent.getStringExtra("bigText")
        if (evento == ""){ evento = intent.getStringExtra("body") }
        tvNotifActEvento.text = evento

        Glide.with(this)
            .load(R.drawable.fab_animation)
            .asGif()
            .into(ivFabAnimation)

        fabSilenceAction.setOnClickListener {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            fabSilenceAction.hide()
            ivFabAnimation.visibility = View.GONE
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(2200)
        }
    }
}