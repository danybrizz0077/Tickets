package daniel.oswaldo.brizuela.helpsdanielbrizuelaaquino

import Modelo.ClaseConexion
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtNombre = findViewById<TextView>(R.id.txtUsuario)
        val txtContra = findViewById<TextView>(R.id.txtContra)
        val btnRegistrar = findViewById<Button>(R.id.btnIngresar)

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
            val pantallaPrincipal = Intent(this, Tickets::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()
                val ComprobarUsuario =
                    objConexion?.prepareStatement("select * from Usuario where nombre_usuario =  ? and contrasena_usuario = ?")!!
                ComprobarUsuario.setString(1, txtNombre.text.toString())
                ComprobarUsuario.setString(2, txtContra.text.toString())

                val resultado = ComprobarUsuario.executeQuery()
                if (resultado.next()) {
                    startActivity(pantallaPrincipal)
                }else{
                    println("Usuario no registrado")
                }
                btnRegistrar.setOnClickListener {
                    val pantallaLogin = Intent(this@Login, Tickets::class.java)

                    startActivity(pantallaLogin)
                }
            }
        }
    }
}


