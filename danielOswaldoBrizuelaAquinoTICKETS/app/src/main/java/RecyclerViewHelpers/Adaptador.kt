package RecyclerViewHelpers

import Modelo.ClaseConexion
import Modelo.dataClassTickets
import android.app.AlertDialog
import kotlinx.coroutines.withContext
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import daniel.oswaldo.brizuela.danieloswaldobrizuelaaquinotickets.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Adaptador(private var Datos: List<dataClassTickets>) : RecyclerView.Adapter<ViewHolder>() {

    fun actualizarLista(nuevaLista: List<dataClassTickets>) {
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun actualicePantalla(UUID_Ticket: String, nuevoTicket: String) {
        val index = Datos.indexOfFirst { it.UUID_ticket == UUID_Ticket }
        Datos[index].titulo = nuevoTicket
        notifyDataSetChanged()
    }

    fun eliminarDatos(titulo: String, UUID_Ticket: String, Posicion: Int) {
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(Posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val deleteTickets =
                objConexion?.prepareStatement("delete from Ticket where titulo = ? and UUID_Ticket = ?")!!

            deleteTickets.setString(1, titulo)
            deleteTickets.setString(2, UUID_Ticket)
            deleteTickets.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(Posicion)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = Datos[position]
        holder.textView.text = ticket.titulo

        holder.imgDelete.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Estás seguro que deseas eliminar el ticket?")

            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(ticket.titulo, ticket.UUID_ticket, position)
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        fun actualizarDato(
            UUID_Ticket: String,
            titulo: String,
            descripcion: String,
            autor: String,
            email: String,
            fecha: String
        ) {
            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()

                val updateTickets =
                    objConexion?.prepareStatement("UPDATE Tickets SET titulo = ?, descripcion = ?, autor = ?, email_autor = ?, fecha_ticket = ? WHERE UUID_ticket = ?")!!
                updateTickets.setString(1, titulo)
                updateTickets.setString(2, descripcion)
                updateTickets.setString(3, autor)
                updateTickets.setString(4, email)
                updateTickets.setString(5, fecha)
                updateTickets.setString(6, UUID_Ticket)
                updateTickets.executeUpdate()

                withContext(Dispatchers.Main) {
                }
            }
        }

        holder.imgEdit.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar")
            builder.setMessage("¿Desea editar los datos del ticket?")

            val cuadroTexto = EditText(context)
            cuadroTexto.setHint("Datos del ticket:")

            val datosTickets = "Nombre: ${ticket.titulo}\n" +
                    "Descripción: ${ticket.descripcion}\n" +
                    "Autor: ${ticket.autor}\n" +
                    "Email del autor: ${ticket.email_autor}\n" +
                    "Fecha del ticket: ${ticket.fecha_ticket}"

            cuadroTexto.setText(datosTickets)
            builder.setView(cuadroTexto)

            builder.setPositiveButton("Actualizar") { dialog, which ->
                val datosActualizados = cuadroTexto.text.toString().split("\n")
                val titulo = datosActualizados[0].substringAfter("Nombre: ")
                val descripcion = datosActualizados[1].substringAfter("Descripción: ")
                val autor = datosActualizados[2].substringAfter("Autor: ")
                val email = datosActualizados[3].substringAfter("Email del Autor: ")
                val fecha = datosActualizados[4].substringAfter("Fecha del Ticket: ")

                actualizarDato(ticket.UUID_ticket, titulo, descripcion, autor, email, fecha)
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }
}