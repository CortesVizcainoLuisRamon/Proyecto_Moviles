package com.example.activaescom

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import androidx.lifecycle.lifecycleScope
import com.example.activaescom.database.entities.UsuarioEntity
import com.example.activaescom.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

class EditarPerfilActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var imgPerfil: ImageView
    private lateinit var usuarioViewModel: UsuarioViewModel

    // Lanzador para galería
    private val seleccionarImagen = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { mostrarYGuardarFoto(it) }
    }

    // Lanzador para cámara
    private var uriFotoTemp: Uri? = null
    private val tomarFoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        if (exito) uriFotoTemp?.let { mostrarYGuardarFoto(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        usuarioViewModel = UsuarioViewModel(application)
        findViewById<android.widget.ImageButton>(R.id.navPerfil).isSelected = true

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        imgPerfil = findViewById(R.id.imgPerfil)

        // Cargar foto guardada si existe
        val rutaGuardada = UserPreferences.getFotoPerfil(this)
        if (rutaGuardada.isNotEmpty()) {
            val archivo = File(rutaGuardada)
            if (archivo.exists()) {
                imgPerfil.setImageBitmap(BitmapFactory.decodeFile(rutaGuardada))
                imgPerfil.clearColorFilter()
            }
        }

        lifecycleScope.launch {

            val usuarioId =
                UserPreferences.getUsuarioId(
                    this@EditarPerfilActivity
                )

            val usuario =
                usuarioViewModel
                    .obtenerUsuarioPorId(
                        usuarioId
                    )

            if (usuario != null) {

                findViewById<EditText>(R.id.etUsuario)
                    .setText(usuario.usuario)

                findViewById<EditText>(R.id.etNombre)
                    .setText(usuario.nombre)

                findViewById<EditText>(R.id.etApellido)
                    .setText(usuario.apellido)

                findViewById<EditText>(R.id.etFechaNacimiento)
                    .setText(usuario.fechaNacimiento)

            }
        }

        // Calendario para fecha de nacimiento
        val etFecha = findViewById<EditText>(R.id.etFechaNacimiento)
        etFecha.setOnClickListener {
            val c = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                    etFecha.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }

        // Botón regresar
        findViewById<ImageButton>(R.id.btnRegresar).setOnClickListener {
            finish()
        }

        // Guardar cambios
        findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener {

            lifecycleScope.launch {

                val usuarioId =
                    UserPreferences.getUsuarioId(
                        this@EditarPerfilActivity
                    )

                val usuarioActual =
                    usuarioViewModel
                        .obtenerUsuarioPorId(
                            usuarioId
                        )

                if (usuarioActual != null) {

                    val usuario =
                        findViewById<EditText>(R.id.etUsuario)
                            .text.toString().trim()

                    val nombre =
                        findViewById<EditText>(R.id.etNombre)
                            .text.toString().trim()

                    val apellido =
                        findViewById<EditText>(R.id.etApellido)
                            .text.toString().trim()

                    val fecha =
                        findViewById<EditText>(R.id.etFechaNacimiento)
                            .text.toString().trim()

                    val usuarioActualizado = UsuarioEntity(

                        id = usuarioActual.id,

                        usuario = usuario,

                        nombre = nombre,

                        boleta = usuarioActual.boleta,

                        apellido = apellido,

                        correo = usuarioActual.correo,

                        fechaNacimiento = fecha,

                        fotoPerfil = usuarioActual.fotoPerfil,

                        password = usuarioActual.password
                    )

                    usuarioViewModel.actualizarUsuario(
                        usuarioActualizado
                    )

                    UserPreferences.guardarDatosPerfil(
                        this@EditarPerfilActivity,
                        usuario,
                        nombre,
                        apellido,
                        fecha
                    )

                    Toast.makeText(
                        this@EditarPerfilActivity,
                        "Perfil actualizado",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }

        // Botón "Cambiar" foto
        findViewById<TextView>(R.id.btnCambiarFoto).setOnClickListener {
            mostrarOpcionesFoto()
        }
    }

    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("Tomar foto", "Elegir de galería")
        android.app.AlertDialog.Builder(this)
            .setTitle("Foto de perfil")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> abrirCamara()
                    1 -> seleccionarImagen.launch("image/*")
                }
            }
            .show()
    }

    private fun abrirCamara() {
        val archivoTemp = File(filesDir, "foto_temp.jpg")
        uriFotoTemp = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            archivoTemp
        )
        tomarFoto.launch(uriFotoTemp!!)
    }

    private fun mostrarYGuardarFoto(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        // Guardar en almacenamiento interno
        val archivo = File(filesDir, "foto_perfil.jpg")
        FileOutputStream(archivo).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        // Mostrar en ImageView y quitar tint
        imgPerfil.setImageBitmap(bitmap)
        imgPerfil.clearColorFilter()

        // Persistir ruta
        UserPreferences.guardarFotoPerfil(this, archivo.absolutePath)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}