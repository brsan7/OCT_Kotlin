package com.brsan7.oct.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.brsan7.oct.R
import com.brsan7.oct.application.OctApplication
import com.brsan7.oct.model.LocalVO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

open class SharedPreferencesUtils {

    private fun getInstanceSharedPreferences() : SharedPreferences {
        return OctApplication.instance.getSharedPreferences("com.brsan7.oct.LOCAL_DEFAULT", Context.MODE_PRIVATE)
    }

    fun setShareLocalDefault(meuLocal: LocalVO){
        getInstanceSharedPreferences().edit{
            putString("localDef", Gson().toJson(meuLocal))
            commit()
        }
    }

    fun getShareLocalDefault() : LocalVO {
        val defLocal = LocalVO(
                id = -1,
                titulo = OctApplication.instance.getString(R.string.aviso_semLocalDef),
                latitude = "",
                longitude = "",
                fusoHorario = ""
        )
        val ultimoItemRegGson = getInstanceSharedPreferences().getString("localDef", Gson().toJson(defLocal))
        val convTipo = object : TypeToken<LocalVO>(){}.type
        return Gson().fromJson(ultimoItemRegGson,convTipo)
    }
}