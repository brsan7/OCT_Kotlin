package com.brsan7.oct

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val tvNotifActEvento = findViewById<TextView>(R.id.tvNotifActEvento)
        val tvNotifActEventoAlpha = findViewById<TextView>(R.id.tvNotifActEventoAlpha)
        val efabAction = findViewById<FloatingActionButton>(R.id.efabAction)
        tvNotifActEvento.text = intent.getStringExtra("Evento")
        tvNotifActEventoAlpha.text = tvNotifActEvento.text
        efabAction.setOnClickListener {
            Toast.makeText(this,"Não Implementado",Toast.LENGTH_LONG).show()
        }
    }
}