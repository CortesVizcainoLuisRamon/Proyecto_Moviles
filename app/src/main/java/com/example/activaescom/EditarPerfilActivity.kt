package com.example.activaescom

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.util.Calendar

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // Pre-llenar campos con datos actuales
        findViewById<EditText>(R.id.etUsuario).setText(UserPreferences.getUsuario(this))
        findViewById<EditText>(R.id.etNombre).setText(UserPreferences.getNombre(this))
        findViewById<EditText>(R.id.etApellido).setText(UserPreferences.getApellido(this))
        findViewById<EditText>(R.id.etFechaNacimiento).setText(UserPreferences.getFecha(this))

        // ── Calendario para fecha de nacimiento ──
        val etFecha = findViewById<EditText>(R.id.etFechaNacimiento)

        etFecha.setOnClickListener {
            val c = Calendar.getInstance()
            val day   = c.get(Calendar.DAY_OF_MONTH)
            val month = c.get(Calendar.MONTH)
            val year  = c.get(Calendar.YEAR)

            val datePicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                    etFecha.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )

            // No permite fechas futuras
            datePicker.datePicker.maxDate = System.currentTimeMillis()

            datePicker.show()
        }
        // ─────────────────────────────────────────

        // Botón regresar
        findViewById<ImageButton>(R.id.btnRegresar).setOnClickListener {
            finish()
        }

        // Guardar cambios
        findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener {
            val usuario  = findViewById<EditText>(R.id.etUsuario).text.toString().trim()
            val nombre   = findViewById<EditText>(R.id.etNombre).text.toString().trim()
            val apellido = findViewById<EditText>(R.id.etApellido).text.toString().trim()
            val fecha    = findViewById<EditText>(R.id.etFechaNacimiento).text.toString().trim()

            UserPreferences.guardarDatosPerfil(this, usuario, nombre, apellido, fecha)

            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}