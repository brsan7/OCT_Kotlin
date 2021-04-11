package com.brsan7.oct

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class DrawerMenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    protected fun setupDrawerMenu(tbTitulo: String){
        drawerLayout = findViewById(R.id.drawerLayout)

        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolBar)
        toolBar.title = tbTitulo
        setSupportActionBar(toolBar)

        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolBar,R.string.open_drawer,R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)

        return when (item.itemId){
            R.id.menuMainAct ->{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuCalAct_calendario ->{
                val intent = Intent(this, CalendarioEventoActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuCalAct_solar ->{
                val intent = Intent(this, CalendarioSolarActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuLocalAct ->{
                val intent = Intent(this, LocalizacaoActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}