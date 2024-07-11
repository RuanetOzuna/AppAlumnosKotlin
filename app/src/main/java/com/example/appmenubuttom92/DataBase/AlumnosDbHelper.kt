package com.example.appmenubuttom92.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AlumnosDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "alumnos.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE ${DefinirDB.Alumnos.TABLA} (" +
                "${DefinirDB.Alumnos.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${DefinirDB.Alumnos.MATRICULA} TEXT, " +
                "${DefinirDB.Alumnos.NOMBRE} TEXT, " +
                "${DefinirDB.Alumnos.DOMICILIO} TEXT, " +
                "${DefinirDB.Alumnos.ESPECIALIDAD} TEXT, " +
                "${DefinirDB.Alumnos.FOTO} TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${DefinirDB.Alumnos.TABLA}")
        onCreate(db)
    }
}
