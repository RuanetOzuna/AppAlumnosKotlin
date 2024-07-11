package com.example.appmenubuttom92

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AcercaFragment : Fragment() {
    private lateinit var rcvLista: RecyclerView
    private lateinit var adaptador: MiAdaptador

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_acerca, container, false)
        rcvLista = view.findViewById(R.id.recId) // Corrected view reference
        rcvLista.layoutManager = LinearLayoutManager(requireContext())

        val listaAlumno = ArrayList<AlumnoLista>().apply {
            add(AlumnoLista(1, "2021030124", "Ozuna Diaz Ruanet Alejandro", "", "Ing. Tec. Información", R.drawable.ruanet))
            add(AlumnoLista(2, "2021030233", "Ubaldo Sanchez Juan Carlos", "", "Ing. Tec. Información", R.drawable.ubaldo))
        }

        adaptador = MiAdaptador(listaAlumno, requireContext())
        rcvLista.adapter = adaptador

        return view
    }
}
