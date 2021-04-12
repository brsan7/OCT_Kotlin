package com.brsan7.oct.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.brsan7.oct.model.EventoVO

class HelperDB (
        context: Context
): SQLiteOpenHelper(context, NOME_BANCO, null, VESAO_ATUAL) {

    companion object{
        private const val NOME_BANCO = "oct.db"
        private const val VESAO_ATUAL = 1

    }
//////////////////////EVENTOS//////////////////////////
    var TABLE_EVENTOS = "eventos"
    val COLUMNS_ID_EVENTOS = "id"
    val COLUMNS_TITULO_EVENTOS = "titulo"
    val COLUMNS_DATA_EVENTOS = "data"
    val COLUMNS_HORA_EVENTOS = "hora"
    val COLUMNS_TIPO_EVENTOS = "tipo"
    val COLUMNS_RECORRENCIA_EVENTOS = "recorrencia"
    val COLUMNS_DESCRICAO_EVENTOS = "descricao"
    val DROP_TABLE_EVENTOS = "DROP TABLE IF EXISTS $TABLE_EVENTOS"
    val CREATE_TABLE_EVENTOS = "CREATE TABLE $TABLE_EVENTOS(" +
            "$COLUMNS_ID_EVENTOS INTEGER NOT NULL," +
            "$COLUMNS_TITULO_EVENTOS TEXT NOT NULL," +
            "$COLUMNS_DATA_EVENTOS TEXT NOT NULL," +
            "$COLUMNS_HORA_EVENTOS TEXT NOT NULL," +
            "$COLUMNS_TIPO_EVENTOS TEXT NOT NULL," +
            "$COLUMNS_RECORRENCIA_EVENTOS TEXT NOT NULL," +
            "$COLUMNS_DESCRICAO_EVENTOS TEXT NOT NULL," +
            "" +
            "PRIMARY KEY($COLUMNS_ID_EVENTOS AUTOINCREMENT)" +
            ")"
//////////////////////EVENTOS//////////////////////////
//////////////////////LOCAIS//////////////////////////
    var TABLE_LOCAIS = "locais"
    val COLUMNS_ID_LOCAIS = "id"
    val COLUMNS_TITULO_LOCAIS = "titulo"
    val COLUMNS_LATITUDE_LOCAIS = "latitude"
    val COLUMNS_LONGITUDE_LOCAIS = "longitude"
    val COLUMNS_DESCRICAO_LOCAIS = "descricao"
    val DROP_TABLE_LOCAIS = "DROP TABLE IF EXISTS $TABLE_LOCAIS"
    val CREATE_TABLE_LOCAIS = "CREATE TABLE $TABLE_LOCAIS(" +
            "$COLUMNS_ID_LOCAIS INTEGER NOT NULL," +
            "$COLUMNS_TITULO_LOCAIS TEXT NOT NULL," +
            "$COLUMNS_LATITUDE_LOCAIS TEXT NOT NULL," +
            "$COLUMNS_LONGITUDE_LOCAIS TEXT NOT NULL," +
            "$COLUMNS_DESCRICAO_LOCAIS TEXT NOT NULL," +
            "" +
            "PRIMARY KEY($COLUMNS_ID_LOCAIS AUTOINCREMENT)" +
            ")"
//////////////////////LOCAIS//////////////////////////

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_EVENTOS)
        db?.execSQL(CREATE_TABLE_LOCAIS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion){
            db?.execSQL(DROP_TABLE_EVENTOS)
            db?.execSQL(DROP_TABLE_LOCAIS)
        }
        onCreate(db)
    }

    fun buscarEventos(busca : String, isBuscaPorData : Boolean = false) : List<EventoVO>{

        val db = readableDatabase ?: return mutableListOf()
        val lista = mutableListOf<EventoVO>()
        val sql:String
        if(isBuscaPorData){
            sql = "SELECT * FROM $TABLE_EVENTOS WHERE $COLUMNS_DATA_EVENTOS LIKE '%$busca%'"
        }else{
            //sql = "SELECT * FROM $TABLE_EVENTOS"
            sql = "SELECT * FROM $TABLE_EVENTOS WHERE $COLUMNS_ID_EVENTOS LIKE '%$busca%'"
        }
        val cursor = db.rawQuery(sql, arrayOf())
        if (cursor == null){
            db.close()
            return mutableListOf()
        }
        while (cursor.moveToNext()){
            val itemHist = EventoVO(
                    cursor.getInt(cursor.getColumnIndex(COLUMNS_ID_EVENTOS)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_TITULO_EVENTOS)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_DATA_EVENTOS)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_HORA_EVENTOS)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_TIPO_EVENTOS)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_RECORRENCIA_EVENTOS)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_DESCRICAO_EVENTOS))
            )
            lista.add(itemHist)
        }
        cursor.close()
        db.close()
        return lista
    }

    fun registrarEvento(itemEvento: EventoVO){
        val db: SQLiteDatabase = writableDatabase ?: return
        val sql = "INSERT INTO $TABLE_EVENTOS " +
                "($COLUMNS_TITULO_EVENTOS," +
                "$COLUMNS_DATA_EVENTOS," +
                "$COLUMNS_HORA_EVENTOS," +
                "$COLUMNS_TIPO_EVENTOS," +
                "$COLUMNS_RECORRENCIA_EVENTOS," +
                "$COLUMNS_DESCRICAO_EVENTOS) " +
                "VALUES(?,?,?,?,?,?)"
        val argumento = arrayOf(
                itemEvento.titulo,
                itemEvento.data,
                itemEvento.hora,
                itemEvento.tipo,
                itemEvento.recorrencia,
                itemEvento.descricao)
        db.execSQL(sql,argumento)
        db.close()
    }

    fun deletarEvento(id:Int){
        val db = writableDatabase ?: return
        val sql = "DELETE FROM $TABLE_EVENTOS WHERE $COLUMNS_ID_EVENTOS = ?"
        val argumento = arrayOf("$id")
        db.execSQL(sql,argumento)
        db.close()
    }

    fun modificarEvento(itemEvento: EventoVO){
        val db = writableDatabase ?: return
        val sql = "UPDATE $TABLE_EVENTOS " +
                "SET $COLUMNS_TITULO_EVENTOS = ?, " +
                "$COLUMNS_DATA_EVENTOS = ?, " +
                "$COLUMNS_HORA_EVENTOS = ?, " +
                "$COLUMNS_TIPO_EVENTOS = ?, " +
                "$COLUMNS_RECORRENCIA_EVENTOS = ?, " +
                "$COLUMNS_DESCRICAO_EVENTOS = ? " +
                "WHERE $COLUMNS_ID_EVENTOS = ?"
        val argumento = arrayOf(
                itemEvento.titulo,
                itemEvento.data,
                itemEvento.hora,
                itemEvento.tipo,
                itemEvento.recorrencia,
                itemEvento.descricao,
                itemEvento.id)
        db.execSQL(sql,argumento)
        db.close()
    }
}