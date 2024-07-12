package com.example.appmenubuttom92

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appmenubuttom92.Database.Alumno

class MiAdaptador(private val context: Context, private var alumnos: List<Alumno>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MiAdaptador.ViewHolder>() {

    private var alumnosFiltrados: List<Alumno> = alumnos

    interface OnItemClickListener {
        fun onItemClick(alumno: Alumno)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foto: ImageView = view.findViewById(R.id.foto)
        val txtAlumnoNombre: TextView = view.findViewById(R.id.txtAlumnoNombre)
        val txtCarrera: TextView = view.findViewById(R.id.txtCarrera)
        val txtMatricula: TextView = view.findViewById(R.id.txtMatricula)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alumno_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alumno = alumnosFiltrados[position]
        holder.txtAlumnoNombre.text = alumno.nombre
        holder.txtCarrera.text = alumno.especialidad
        holder.txtMatricula.text = alumno.matricula
        // Cargar la imagen usando Glide
        Glide.with(context)
            .load(alumno.foto) // Suponiendo que tienes una URL o URI de la foto
            .into(holder.foto)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(alumno)
        }
    }

    override fun getItemCount() = alumnosFiltrados.size

    fun actualizarAlumnos(nuevosAlumnos: List<Alumno>) {
        this.alumnos = nuevosAlumnos
        this.alumnosFiltrados = nuevosAlumnos
        notifyDataSetChanged()
    }

    fun filtrar(query: String?) {
        alumnosFiltrados = if (query.isNullOrEmpty()) {
            alumnos
        } else {
            alumnos.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.matricula.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
