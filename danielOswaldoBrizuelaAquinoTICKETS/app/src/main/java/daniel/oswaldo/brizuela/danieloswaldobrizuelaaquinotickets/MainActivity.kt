package daniel.oswaldo.brizuela.danieloswaldobrizuelaaquinotickets

import Modelo.ClaseConexion
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//mandar a llamar a todos los elementos de la vista
        val txtNombre = findViewById<EditText>(R.id.txtUsuario)
        val txtContra = findViewById<EditText>(R.id.txtContra)
        val btnRegistrar = findViewById<Button>(R.id.btnAgregar)

        //programar el boton de agregar
        btnRegistrar.setOnClickListener {

            val nombre = txtNombre.text.toString()
            val contra = txtContra.text.toString()

            if (nombre.isEmpty() || contra.isEmpty()) {
                Toast.makeText(
                    this, "Para seguir debe llenar todos los datos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.i(
                    "Teste de credenciales",
                    "Nombre: $nombre y Contraseña: $contra "
                )
            }
        }

        btnRegistrar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {

                val objConexion = ClaseConexion().cadenaConexion()
                val addUsuario =
                    objConexion?.prepareStatement("insert into Usuario (UUID_Usuario,Nombre_Usuario,Contrasena_Usuario) values(?,?,?)")!!
                addUsuario.setString(1, UUID.randomUUID().toString())
                addUsuario.setString(2, txtNombre.text.toString())
                addUsuario.setString(3, txtContra.text.toString())

                addUsuario.executeUpdate()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Usuario Registrado", Toast.LENGTH_SHORT)
                        .show()
                    txtNombre.setText("")
                    txtContra.setText("")
                }
                btnRegistrar.setOnClickListener {
                    val pantallaLogin = Intent(this@MainActivity, Login::class.java)
                    startActivity(pantallaLogin)
                }
            }
        }
    }
}