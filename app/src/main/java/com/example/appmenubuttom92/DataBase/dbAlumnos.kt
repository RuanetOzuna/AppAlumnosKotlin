package com.example.appmenubuttom92.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class dbAlumnos(private val context: Context) {
    private val dbHelper: AlumnosDbHelper = AlumnosDbHelper(context)
    private lateinit var db: SQLiteDatabase

    private val leerRegistro = arrayOf(
        DefinirDB.Alumnos.ID,
        DefinirDB.Alumnos.MATRICULA,
        DefinirDB.Alumnos.NOMBRE,
        DefinirDB.Alumnos.DOMICILIO,
        DefinirDB.Alumnos.ESPECIALIDAD,
        DefinirDB.Alumnos.FOTO
    )

    fun openDataBase() {
        db = dbHelper.writableDatabase
    }

    fun insertarAlumno(alumno: Alumno): Long {
        val value = ContentValues().apply {
            put(DefinirDB.Alumnos.MATRICULA, alumno.matricula)
            put(DefinirDB.Alumnos.NOMBRE, alumno.nombre)
            put(DefinirDB.Alumnos.DOMICILIO, alumno.domicilio)
            put(DefinirDB.Alumnos.ESPECIALIDAD, alumno.especialidad)
            put(DefinirDB.Alumnos.FOTO, alumno.foto)
        }
        return db.insert(DefinirDB.Alumnos.TABLA, null, value)
    }

    fun actualizarAlumno(alumno: Alumno, id: Int): Int {
        val value = ContentValues().apply {
            put(DefinirDB.Alumnos.MATRICULA, alumno.matricula)
            put(DefinirDB.Alumnos.NOMBRE, alumno.nombre)
            put(DefinirDB.Alumnos.DOMICILIO, alumno.domicilio)
            put(DefinirDB.Alumnos.ESPECIALIDAD, alumno.especialidad)
            put(DefinirDB.Alumnos.FOTO, alumno.foto)
        }
        return db.update(DefinirDB.Alumnos.TABLA, value, "${DefinirDB.Alumnos.ID} = ?", arrayOf(id.toString()))
    }

    fun borrarAlumno(id: Int): Int {
        return db.delete(DefinirDB.Alumnos.TABLA, "${DefinirDB.Alumnos.ID} = ?", arrayOf(id.toString()))
    }

    fun mostrarAlumnos(cursor: Cursor): Alumno {
        return Alumno(
            id = cursor.getInt(0),
            matricula = cursor.getString(1),
            nombre = cursor.getString(2),
            domicilio = cursor.getString(3),
            especialidad = cursor.getString(4),
            foto = cursor.getString(5)
        )
    }

    fun getAlumno(id: Long): Alumno {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DefinirDB.Alumnos.TABLA, leerRegistro, "${DefinirDB.Alumnos.ID} = ?", arrayOf(id.toString()), null, null, null)
        cursor.moveToFirst()
        val alumno = mostrarAlumnos(cursor)
        cursor.close()
        return alumno
    }

    fun getAlumno(matricula: String): Alumno {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DefinirDB.Alumnos.TABLA, leerRegistro, "${DefinirDB.Alumnos.MATRICULA} = ?", arrayOf(matricula), null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                return mostrarAlumnos(it)
            }
        }
        return Alumno() // Asegúrate de tener valores predeterminados en el constructor de Alumno
    }

    fun leerTodos(): ArrayList<Alumno> {
        val cursor = db.query(DefinirDB.Alumnos.TABLA, leerRegistro, null, null, null, null, null)
        val listaAlumno = ArrayList<Alumno>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val alumno = mostrarAlumnos(cursor)
            listaAlumno.add(alumno)
            cursor.moveToNext()
        }
        cursor.close()
        return listaAlumno
    }

    fun close() {
        dbHelper.close()
    }
}
