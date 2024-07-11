package com.example.appmenubuttom92

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.appmenubuttom92.Database.Alumno
import com.example.appmenubuttom92.Database.dbAlumnos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlumnosViewModel(application: Application) : AndroidViewModel(application) {

    private val _alumnos = MutableLiveData<List<Alumno>>()
    val alumnos: LiveData<List<Alumno>> get() = _alumnos

    init {
        cargarAlumnos()
    }

    fun cargarAlumnos() {
        viewModelScope.launch(Dispatchers.IO) {
            // Inicializar la base de datos y cargar los alumnos
            val dbAlumnos = dbAlumnos(getApplication())
            dbAlumnos.openDataBase()
            val listaAlumnos = dbAlumnos.leerTodos()
            dbAlumnos.close()
            _alumnos.postValue(listaAlumnos)
        }
    }

    fun setAlumnos(listaAlumnos: List<Alumno>) {
        _alumnos.value = listaAlumnos
    }
}
