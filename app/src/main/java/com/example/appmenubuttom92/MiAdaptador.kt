package com.example.appmenubuttom92

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MiAdaptador(
    private val listaAlumnos: ArrayList<AlumnoLista>,
    private val context: Context
) : RecyclerView.Adapter<MiAdaptador.ViewHolder>(), View.OnClickListener {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var listener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.alumno_item, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alumno = listaAlumnos[position]
        holder.txtMatricula.text = alumno.matricula
        holder.txtNombre.text = alumno.nombre
        holder.idImagen.setImageResource(alumno.foto)
        holder.txtCarrera.text = alumno.especialidad
    }

    override fun getItemCount(): Int {
        return listaAlumnos.size
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onClick(v: View) {
        listener?.onClick(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtAlumnoNombre)
        val txtMatricula: TextView = itemView.findViewById(R.id.txtMatricula)
        val txtCarrera: TextView = itemView.findViewById(R.id.txtCarrera)
        val idImagen: ImageView = itemView.findViewById(R.id.foto)
    }
}
