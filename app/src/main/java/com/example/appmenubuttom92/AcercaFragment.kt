package com.example.appmenubuttom92

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.appmenubuttom92.Database.Alumno

class AcercaFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MiAdaptador
    private val alumnosViewModel: AlumnosViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_acerca, container, false)
        recyclerView = view.findViewById(R.id.recId)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Configurar el adaptador
        adapter = MiAdaptador(requireContext(), listOf())
        recyclerView.adapter = adapter

        // Observar cambios en la lista de alumnos
        alumnosViewModel.alumnos.observe(viewLifecycleOwner, Observer { alumnos ->
            adapter.actualizarAlumnos(alumnos)
        })

        // Configurar el FloatingActionButton
        val fab: FloatingActionButton = view.findViewById(R.id.agregarAlumno)
        fab.setOnClickListener {
            // Reemplazar el fragmento actual con DbFragment
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frmContenedor, DbFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }
}
