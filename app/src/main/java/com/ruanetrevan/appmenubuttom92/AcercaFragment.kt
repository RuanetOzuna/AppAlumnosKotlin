package com.ruanetrevan.appmenubuttom92

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.SearchView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ruanetrevan.appmenubuttom92.Database.Alumno

class AcercaFragment : Fragment(), MiAdaptador.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MiAdaptador
    private val alumnosViewModel: AlumnosViewModel by activityViewModels()
    private lateinit var searchView: SearchView
    private lateinit var searchIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_acerca, container, false)
        recyclerView = view.findViewById(R.id.recId)
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchView = view.findViewById(R.id.searchView)
        searchIcon = view.findViewById(R.id.imgSearchIcon)

        // Configurar el adaptador
        adapter = MiAdaptador(requireContext(), listOf(), this)
        recyclerView.adapter = adapter

        // Observar cambios en la lista de alumnos
        alumnosViewModel.alumnos.observe(viewLifecycleOwner, Observer { alumnos ->
            adapter.actualizarAlumnos(alumnos)
        })

        // Configurar el SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filtrar(newText)
                return false
            }
        })

        // Mostrar/ocultar el SearchView al hacer clic en el icono de búsqueda
        searchIcon.setOnClickListener {
            if (searchView.visibility == View.GONE) {
                searchView.visibility = View.VISIBLE
            } else {
                searchView.visibility = View.GONE
            }
        }

        // Configurar el FloatingActionButton
        val fab: FloatingActionButton = view.findViewById(R.id.agregarAlumno)
        fab.setOnClickListener {
            // Reemplazar el fragmento actual con DbFragment
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frmContenedor, DbFragment.newInstance(null))
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    override fun onItemClick(alumno: Alumno) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frmContenedor, DbFragment.newInstance(alumno))
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
