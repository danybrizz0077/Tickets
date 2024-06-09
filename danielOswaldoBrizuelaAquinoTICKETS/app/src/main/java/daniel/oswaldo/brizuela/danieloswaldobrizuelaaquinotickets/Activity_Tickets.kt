package daniel.oswaldo.brizuela.danieloswaldobrizuelaaquinotickets

import Modelo.ClaseConexion
import Modelo.dataClassTickets
import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
class Activity_Tickets : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tickets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtTitulo = findViewById<EditText>(R.id.txtTitulo)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtAutor = findViewById<EditText>(R.id.txtAutor)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtFecha = findViewById<EditText>(R.id.txtFecha)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rcvTickets = findViewById<RecyclerView>(R.id.rcvTickets)

        rcvTickets.layoutManager = LinearLayoutManager(this)

        fun ObtenerDatos(): List<dataClassTickets> {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from Ticket")!!
            val tickets = mutableListOf<dataClassTickets>()

            while (resultSet.next()) {
                val UUID_Ticket = resultSet.getString("UUID_ticket")
                val titulo = resultSet.getString("titulo")
                val descripcion = resultSet.getString("descripcion")
                val autor = resultSet.getString("autor")
                val email = resultSet.getString("email_autor")
                val fecha = resultSet.getString("fecha_ticket")

                val ticket = dataClassTickets(UUID_Ticket, titulo, descripcion, autor, email, fecha)

                tickets.add(ticket)
            }

            return tickets
        }
        CoroutineScope(Dispatchers.IO).launch {
            val TicketsDB = ObtenerDatos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(TicketsDB)
                rcvTickets .adapter = adapter
            }
        }

        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val  objConexion = ClaseConexion().cadenaConexion()
                val addTickets = objConexion?.prepareStatement("insert into Ticket " +
                        "(UUID_ticket, titulo, descripcion, autor, email_autor, fecha_ticket) " +
                        "values (?, ?, ?, ?, ?, ?")!!

                addTickets.setString(1, UUID.randomUUID().toString())
                addTickets.setString(2, txtTitulo.text.toString())
                addTickets.setString(3, txtDescripcion.text.toString())
                addTickets.setString(4, txtAutor.text.toString())
                addTickets.setString(5, txtEmail.text.toString())
                addTickets.setString(6, txtFecha.text.toString())

                addTickets.executeUpdate()

                val nuevosTickets = ObtenerDatos()
                withContext(Dispatchers.Main){
                    (rcvTickets.adapter as? Adaptador)?.actualizarLista(nuevosTickets)
                }
            }
        }
    }

}
