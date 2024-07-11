package com.example.appmenubuttom92

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class ListaFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var arrayList: ArrayList<String>
    private lateinit var adapter: CustomArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista, container, false)
        listView = view.findViewById(R.id.lstAlumnos)
        searchView = view.findViewById(R.id.searchView)
        val searchIcon = view.findViewById<ImageView>(R.id.imgSearchIcon)

        val items = resources.getStringArray(R.array.alumnos)
        arrayList = ArrayList()
        arrayList.addAll(items)

        adapter = CustomArrayAdapter(requireContext(), arrayList)
        listView.adapter = adapter

        searchView.visibility = View.GONE // Hide SearchView initially

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        searchIcon.setOnClickListener {
            if (searchView.visibility == View.GONE) {
                searchView.visibility = View.VISIBLE
            } else {
                searchView.visibility = View.GONE
            }
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val alumno: String = parent.getItemAtPosition(position).toString()
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Lista de Alumnos")
            builder.setMessage("$position: $alumno")
            builder.setPositiveButton("OK") { dialog, which -> }
            builder.show()
        }

        return view
    }

    class CustomArrayAdapter(context: Context, private val items: List<String>) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, items) {
        private var filteredItems = items.toMutableList()

        override fun getCount(): Int {
            return filteredItems.size
        }

        override fun getItem(position: Int): String? {
            return filteredItems[position]
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filterResults = FilterResults()
                    if (constraint == null || constraint.isEmpty()) {
                        filterResults.values = items
                        filterResults.count = items.size
                    } else {
                        val query = constraint.toString().toLowerCase().trim()
                        val filteredList = items.filter {
                            it.toLowerCase().contains(query)
                        }
                        filterResults.values = filteredList
                        filterResults.count = filteredList.size
                    }
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredItems = results?.values as MutableList<String>
                    notifyDataSetChanged()
                }
            }
        }
    }
}
