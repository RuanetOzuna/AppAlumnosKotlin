package com.example.appmenubuttom92.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class dbAlumnos(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Alumnos.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_ALUMNOS = "alumnos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MATRICULA = "matricula"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_DOMICILIO = "domicilio"
        private const val COLUMN_ESPECIALIDAD = "especialidad"
        private const val COLUMN_FOTO = "foto"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_ALUMNOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MATRICULA + " TEXT,"
                + COLUMN_NOMBRE + " TEXT,"
                + COLUMN_DOMICILIO + " TEXT,"
                + COLUMN_ESPECIALIDAD + " TEXT,"
                + COLUMN_FOTO + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNOS")
        onCreate(db)
    }

    fun openDataBase(): SQLiteDatabase {
        return this.writableDatabase
    }

    fun getAlumno(matricula: String): Alumno {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ALUMNOS, arrayOf(COLUMN_ID, COLUMN_MATRICULA, COLUMN_NOMBRE, COLUMN_DOMICILIO, COLUMN_ESPECIALIDAD, COLUMN_FOTO),
            "$COLUMN_MATRICULA=?", arrayOf(matricula), null, null, null, null
        )
        cursor?.moveToFirst()
        val alumno = Alumno(
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MATRICULA)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOMICILIO)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESPECIALIDAD)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOTO))
        )
        cursor.close()
        return alumno
    }

    fun getAllAlumnos(): List<Alumno> {
        val alumnos = mutableListOf<Alumno>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ALUMNOS", null)
        if (cursor.moveToFirst()) {
            do {
                val alumno = Alumno(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    matricula = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MATRICULA)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    domicilio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOMICILIO)),
                    especialidad = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESPECIALIDAD)),
                    foto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOTO))
                )
                alumnos.add(alumno)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return alumnos
    }

    fun insertarAlumno(alumno: Alumno): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_MATRICULA, alumno.matricula)
        values.put(COLUMN_NOMBRE, alumno.nombre)
        values.put(COLUMN_DOMICILIO, alumno.domicilio)
        values.put(COLUMN_ESPECIALIDAD, alumno.especialidad)
        values.put(COLUMN_FOTO, alumno.foto)
        return db.insert(TABLE_ALUMNOS, null, values)
    }

    fun actualizarAlumno(alumno: Alumno, id: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_MATRICULA, alumno.matricula)
        values.put(COLUMN_NOMBRE, alumno.nombre)
        values.put(COLUMN_DOMICILIO, alumno.domicilio)
        values.put(COLUMN_ESPECIALIDAD, alumno.especialidad)
        values.put(COLUMN_FOTO, alumno.foto)
        return db.update(TABLE_ALUMNOS, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun borrarAlumno(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_ALUMNOS, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
}
