package com.example.appmenubuttom92

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import androidx.fragment.app.activityViewModels
import com.example.appmenubuttom92.Database.Alumno
import com.example.appmenubuttom92.Database.dbAlumnos
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val PICK_IMAGE_REQUEST = 1

class DbFragment : Fragment() {
    private val alumnosViewModel: AlumnosViewModel by activityViewModels()
    private lateinit var btnGuardar: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnBorrar: Button
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
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        txtMatricula = view.findViewById(R.id.txtMatricula)
        txtNombre = view.findViewById(R.id.txtNombre)
        txtDomicilio = view.findViewById(R.id.txtDomicilio)
        txtEspecialidad = view.findViewById(R.id.txtEspecialidad)
        imgPreview = view.findViewById(R.id.imgPreview)
        txtError = view.findViewById(R.id.txtError)

        // Recuperar alumno de los argumentos
        val alumno = arguments?.getParcelable<Alumno>("alumno")
        if (alumno != null) {
            populateFields(alumno)
        }

        checkStoragePermission()

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

            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Debes subir una foto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                db = dbAlumnos(requireContext())
                db.openDataBase()

                val imagePath = saveImageToInternalStorage(selectedImageUri!!)

                val alumnoExistente = db.getAlumno(matricula)
                if (alumnoExistente.id != 0) {
                    // Actualizar alumno existente
                    val alumnoActualizado = alumnoExistente.copy(
                        nombre = nombre,
                        domicilio = domicilio,
                        especialidad = especialidad,
                        foto = imagePath
                    )
                    db.actualizarAlumno(alumnoActualizado, alumnoExistente.id)
                    Toast.makeText(requireContext(), "Alumno actualizado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    // Insertar nuevo alumno
                    val nuevoAlumno = Alumno(
                        nombre = nombre,
                        matricula = matricula,
                        domicilio = domicilio,
                        especialidad = especialidad,
                        foto = imagePath
                    )

                    db.insertarAlumno(nuevoAlumno)
                    Toast.makeText(requireContext(), "Registro Exitoso", Toast.LENGTH_SHORT).show()
                }

                // Actualizar la lista de alumnos en el ViewModel
                val listaAlumnos = db.leerTodos()
                alumnosViewModel.setAlumnos(listaAlumnos)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al acceder a la base de datos: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("DbFragment", "Error al guardar el alumno", e)
            } finally {
                db.close()
            }

            limpiarCampos()
            hideKeyboard()
        }

        btnBuscar.setOnClickListener {
            val matricula = txtMatricula.text.toString()
            if (matricula.isEmpty()) {
                Toast.makeText(requireContext(), "Matricula sin capturar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                db = dbAlumnos(requireContext())
                db.openDataBase()

                val alumno: Alumno = db.getAlumno(matricula)
                if (alumno.id != 0) {
                    txtNombre.setText(alumno.nombre)
                    txtDomicilio.setText(alumno.domicilio)
                    txtEspecialidad.setText(alumno.especialidad)
                    if (alumno.foto.isNotEmpty()) {
                        val imageFile = File(alumno.foto)
                        if (imageFile.exists()) {
                            imgPreview.setImageURI(Uri.fromFile(imageFile))
                            imgPreview.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Matricula no encontrada", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al acceder a la base de datos: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("DbFragment", "Error al buscar el alumno", e)
            } finally {
                db.close()
            }
            hideKeyboard()
        }

        btnBorrar.setOnClickListener {
            val matricula = txtMatricula.text.toString()
            if (matricula.isEmpty()) {
                Toast.makeText(requireContext(), "Matricula sin capturar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
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

                            // Actualizar la lista de alumnos en el ViewModel
                            val listaAlumnos = db.leerTodos()
                            alumnosViewModel.setAlumnos(listaAlumnos)

                            db.close()
                            hideKeyboard()
                        }
                        .setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            hideKeyboard()
                        }
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Matricula no encontrada", Toast.LENGTH_SHORT).show()
                    db.close()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al acceder a la base de datos: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("DbFragment", "Error al borrar el alumno", e)
                db.close()
            }
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
        selectedImageUri = null // Restablecer selectedImageUri
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
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri!!)
                    val file = File(requireContext().filesDir, "images")
                    if (!file.exists()) {
                        file.mkdirs()
                    }
                    val imageFile = File(file, "${System.currentTimeMillis()}.jpg")
                    val outputStream = FileOutputStream(imageFile)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    selectedImageUri = Uri.fromFile(imageFile)
                    imgPreview.setImageURI(selectedImageUri)
                    imgPreview.visibility = View.VISIBLE
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateFields(alumno: Alumno) {
        txtMatricula.setText(alumno.matricula)
        txtNombre.setText(alumno.nombre)
        txtDomicilio.setText(alumno.domicilio)
        txtEspecialidad.setText(alumno.especialidad)
        if (alumno.foto.isNotEmpty()) {
            val imageFile = File(alumno.foto)
            if (imageFile.exists()) {
                selectedImageUri = Uri.fromFile(imageFile)
                imgPreview.setImageURI(selectedImageUri)
                imgPreview.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "images")
        if (!file.exists()) {
            file.mkdirs()
        }
        val imageFile = File(file, "${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(imageFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return imageFile.absolutePath
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(alumno: Alumno?) = DbFragment().apply {
            arguments = Bundle().apply {
                putParcelable("alumno", alumno)
            }
        }
    }
}
