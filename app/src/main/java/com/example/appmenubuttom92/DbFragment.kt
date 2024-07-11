package com.example.appmenubuttom92

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.appmenubuttom92.Database.Alumno
import com.example.appmenubuttom92.Database.dbAlumnos

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val PICK_IMAGE_REQUEST = 1

class DbFragment : Fragment() {
    private lateinit var btnGuardar: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnBorrar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnTest: Button
    private lateinit var btnSelectImage: Button
    private lateinit var txtMatricula: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtDomicilio: EditText
    private lateinit var txtEspecialidad: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var txtError: TextView
    private lateinit var db: dbAlumnos
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_db, container, false)

        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnBuscar = view.findViewById(R.id.btnBuscar)
        btnBorrar = view.findViewById(R.id.btnBorrar)
        btnTest = view.findViewById(R.id.btnTest)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        txtMatricula = view.findViewById(R.id.txtMatricula)
        txtNombre = view.findViewById(R.id.txtNombre)
        txtDomicilio = view.findViewById(R.id.txtDomicilio)
        txtEspecialidad = view.findViewById(R.id.txtEspecialidad)
        imgPreview = view.findViewById(R.id.imgPreview)
        txtError = view.findViewById(R.id.txtError)

        btnSelectImage.setOnClickListener {
            openImageChooser()
        }

        btnGuardar.setOnClickListener {
            val matricula = txtMatricula.text.toString()
            val nombre = txtNombre.text.toString()
            val domicilio = txtDomicilio.text.toString()
            val especialidad = txtEspecialidad.text.toString()

            if (nombre.isEmpty() || matricula.isEmpty() || domicilio.isEmpty() || especialidad.isEmpty()) {
                Toast.makeText(requireContext(), "Campos Faltantes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (especialidad != "Licenciatura" && especialidad != "Ingenieria") {
                txtError.visibility = View.VISIBLE
                txtError.text = "Especialidad debe ser 'Licenciatura' o 'Ingenieria'"
                return@setOnClickListener
            } else {
                txtError.visibility = View.GONE
            }

            db = dbAlumnos(requireContext())
            db.openDataBase()

            val alumnoExistente = db.getAlumno(matricula)
            if (alumnoExistente.id != 0) {
                // Actualizar alumno existente
                alumnoExistente.nombre = nombre
                alumnoExistente.domicilio = domicilio
                alumnoExistente.especialidad = especialidad
                alumnoExistente.foto = selectedImageUri.toString()
                db.actualizarAlumno(alumnoExistente, alumnoExistente.id)
                Toast.makeText(requireContext(), "Alumno actualizado con éxito", Toast.LENGTH_SHORT).show()
            } else {
                // Insertar nuevo alumno
                val alumno = Alumno()
                alumno.nombre = nombre
                alumno.matricula = matricula
                alumno.domicilio = domicilio
                alumno.especialidad = especialidad
                alumno.foto = selectedImageUri.toString()

                db.insertarAlumno(alumno)
                Toast.makeText(requireContext(), "Registro Exitoso", Toast.LENGTH_SHORT).show()
            }
            db.close()
            limpiarCampos()
            hideKeyboard()
        }

        btnBuscar.setOnClickListener {
            val matricula = txtMatricula.text.toString()
            if (matricula.isEmpty()) {
                Toast.makeText(requireContext(), "Matricula sin capturar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db = dbAlumnos(requireContext())
            db.openDataBase()

            val alumno: Alumno = db.getAlumno(matricula)
            if (alumno.id != 0) {
                txtNombre.setText(alumno.nombre)
                txtDomicilio.setText(alumno.domicilio)
                txtEspecialidad.setText(alumno.especialidad)
                if (alumno.foto.isNotEmpty()) {
                    selectedImageUri = Uri.parse(alumno.foto)
                    imgPreview.setImageURI(selectedImageUri)
                    imgPreview.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(requireContext(), "Matricula no encontrada", Toast.LENGTH_SHORT).show()
            }
            db.close()
            hideKeyboard()
        }

        btnBorrar.setOnClickListener {
            val matricula = txtMatricula.text.toString()
            if (matricula.isEmpty()) {
                Toast.makeText(requireContext(), "Matricula sin capturar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db = dbAlumnos(requireContext())
            db.openDataBase()

            val alumno: Alumno = db.getAlumno(matricula)
            if (alumno.id != 0) {
                // Confirmación de eliminación
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Está seguro de eliminar este alumno?")
                    .setPositiveButton("Sí") { dialog, which ->
                        db.borrarAlumno(alumno.id)
                        Toast.makeText(requireContext(), "Alumno eliminado exitosamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(requireContext(), "Matricula no encontrada", Toast.LENGTH_SHORT).show()
            }
            db.close()
            hideKeyboard()
        }

        btnTest.setOnClickListener {
            Toast.makeText(requireContext(), "Botón Agregar Alumno presionado", Toast.LENGTH_SHORT).show()
            hideKeyboard()
        }

        return view
    }

    private fun limpiarCampos() {
        txtMatricula.setText("")
        txtNombre.setText("")
        txtDomicilio.setText("")
        txtEspecialidad.setText("")
        imgPreview.setImageURI(null)
        imgPreview.visibility = View.GONE
        txtError.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                selectedImageUri = data.data
                imgPreview.setImageURI(selectedImageUri)
                imgPreview.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            DbFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
