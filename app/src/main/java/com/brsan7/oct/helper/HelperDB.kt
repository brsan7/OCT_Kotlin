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

    val TABLE_NAME = "eventos"
    val COLUMNS_ID = "id"
    val COLUMNS_TITULO = "titulo"
    val COLUMNS_DATA = "data"
    val COLUMNS_HORA = "hora"
    val COLUMNS_TIPO = "tipo"
    val COLUMNS_RECORRENCIA = "recorrencia"
    val COLUMNS_DESCRICAO = "descricao"
    val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    val CREATE_TABLE = "CREATE TABLE $TABLE_NAME(" +
            "$COLUMNS_ID INTEGER NOT NULL," +
            "$COLUMNS_TITULO TEXT NOT NULL," +
            "$COLUMNS_DATA TEXT NOT NULL," +
            "$COLUMNS_HORA TEXT NOT NULL," +
            "$COLUMNS_TIPO TEXT NOT NULL," +
            "$COLUMNS_RECORRENCIA TEXT NOT NULL," +
            "$COLUMNS_DESCRICAO TEXT NOT NULL," +
            "" +
            "PRIMARY KEY($COLUMNS_ID AUTOINCREMENT)" +
            ")"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion){
            db?.execSQL(DROP_TABLE)
        }
        onCreate(db)
    }

    fun buscarRegistros(argumento : String, isBuscaPorData : Boolean = false) : List<EventoVO>{

        val db = readableDatabase ?: return mutableListOf()
        val lista = mutableListOf<EventoVO>()
        val sql:String
        if(isBuscaPorData){
            sql = "SELECT * FROM $TABLE_NAME WHERE $COLUMNS_TITULO LIKE '%$argumento%'"
        }else{
            sql = "SELECT * FROM $TABLE_NAME WHERE $COLUMNS_ID LIKE '%$argumento%'"
        }
        val cursor = db.rawQuery(sql, arrayOf())
        if (cursor == null){
            db.close()
            return mutableListOf()
        }
        while (cursor.moveToNext()){
            val itemHist = EventoVO(
                    cursor.getInt(cursor.getColumnIndex(COLUMNS_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_TITULO)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_DATA)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_HORA)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_TIPO)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_RECORRENCIA)),
                    cursor.getString(cursor.getColumnIndex(COLUMNS_DESCRICAO))
            )
            lista.add(itemHist)
        }
        cursor.close()
        db.close()
        return lista
    }

    fun salvarRegistro(itemHist: EventoVO){
        val db: SQLiteDatabase = writableDatabase ?: return
        val sql = "INSERT INTO $TABLE_NAME " +
                "($COLUMNS_TITULO,$COLUMNS_DATA,$COLUMNS_HORA,$COLUMNS_TIPO,$COLUMNS_RECORRENCIA,$COLUMNS_DESCRICAO) " +
                "VALUES(?,?,?,?,?,?)"
        val argumento = arrayOf(itemHist.titulo,
                                itemHist.data,
                                itemHist.hora,
                                itemHist.tipo,
                                itemHist.recorrencia,
                                itemHist.descricao)
        db.execSQL(sql,argumento)
        db.close()
    }

    fun deletarRegistro(id:Int){
        val db = writableDatabase ?: return
        val sql = "DELETE FROM $TABLE_NAME WHERE $COLUMNS_ID = ?"
        val argumento = arrayOf("$id")
        db.execSQL(sql,argumento)
        db.close()
    }

    fun editarRegistro(itemHist: EventoVO){
        val db = writableDatabase ?: return
        val sql = "UPDATE $TABLE_NAME SET $COLUMNS_DESCRICAO = ? WHERE $COLUMNS_ID = ?"
        val argumento = arrayOf(itemHist.descricao,itemHist.id)
        db.execSQL(sql,argumento)
        db.close()
    }
}