package com.brsan7.oct.application

import android.app.Application
import com.brsan7.oct.helper.HelperDB

class OctApplication : Application(){

    var helperDB : HelperDB? = null
        private set

    companion object{
        lateinit var instance: OctApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        helperDB = HelperDB(this)
    }
}