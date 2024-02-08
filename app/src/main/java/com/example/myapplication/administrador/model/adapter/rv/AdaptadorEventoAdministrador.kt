package com.example.myapplication.administrador.model.adapter.rv

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.administrador.fragments.AdministradorGestionarEventosModificarFragment
import com.example.myapplication.cliente.model.adapter.rv.OnClickListener
import com.example.myapplication.data.model.Evento
import com.example.myapplication.data.model.ReservarEvento
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.data.model.UsuarioActual
import com.google.firebase.database.FirebaseDatabase

class AdaptadorEventoAdministrador (private val listaEventos: MutableList<Evento>, private val navController: NavController, private val listener: OnClickListener): RecyclerView.Adapter<AdaptadorEventoAdministrador.EventoViewHolder>(){
    private lateinit var contexto: Context
    private var listaFiltrada = listaEventos

    private var tipoUsuario = ""
    private var idUsuario = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorEventoAdministrador.EventoViewHolder {
        val vistaItem = LayoutInflater.from(parent.context).inflate(R.layout.item_evento_administrador, parent, false)
        contexto = parent.context
        return EventoViewHolder(vistaItem)
    }

    override fun onBindViewHolder(holder: AdaptadorEventoAdministrador.EventoViewHolder, position: Int) {
        //Obtenemos el usuario
        if(UsuarioActual.usuarioActual != null){
            val usuarioActual: Usuario = UsuarioActual.usuarioActual!!
            tipoUsuario = usuarioActual.tipoDeUsuario
            idUsuario = usuarioActual.idUsuario
        }


        val itemActual = listaFiltrada[position]

        holder.nombreEvento.text = itemActual.nombre
        holder.formatoEvento.text = itemActual.formato
        holder.precio.text = itemActual.precio.toString()
        holder.fechaEvento.text = itemActual.fecha
        holder.aforoMaximo.text = itemActual.aforo.toString()
        holder.aforoOcupado.text = itemActual.aforoOcupado.toString()

        val URL: String? = when(itemActual.urlImagenEvento){
            "" -> null
            else -> itemActual.urlImagenEvento
        }

        Glide.with(contexto)
            .load(URL)
            .apply(opcionesGlide(contexto))
            .transition(transicion)
            .into(holder.foto)

        //Diferencias entre usuarios
        if(tipoUsuario == "administrador"){
            holder.boton.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("evento", itemActual)
                val fragment = AdministradorGestionarEventosModificarFragment()
                fragment.arguments = bundle
                //Cambiar la direccion
                navController.navigate(R.id.action_administradorGestionarCartasFragment_to_administradorGestionarCartasModificarFragment, bundle)
            }
        }else{
            //Usuario
            if(itemActual.aforo == itemActual.aforoOcupado){
                //Desactivo el boton para que no se puedan apuntar mas personas
                holder.boton.visibility = View.GONE
                holder.mensajeAforoMaximo.visibility = View.VISIBLE
            }else{
                holder.boton.setOnClickListener {
                    //Añadimos el evento a la base de datos
                    val dbRef = FirebaseDatabase.getInstance().reference
                    //Atributos del evento
                    val urlFoto = itemActual.urlImagenEvento
                    val nombreEvento = itemActual.nombre
                    val formatoEvento = itemActual.formato
                    val aforoMaximo = itemActual.aforo
                    val aforoOcupado = itemActual.aforoOcupado
                    val precioEvento = itemActual.precio
                    val fechaEvento = itemActual.fecha
                    val idEvento = itemActual.id
                    val idReservaEvento = dbRef.child("tienda").child("reservas_eventos").push().key
                    //Tenemos que actualizar las plazas de ocupados
                    val nuevoAforoOcupado = aforoOcupado + 1
                    dbRef.child("tienda").child("reservas_eventos").child(idReservaEvento!!).setValue(
                        ReservarEvento(
                            idReservaEvento,
                            idEvento,
                            idUsuario,
                            nombreEvento,
                            formatoEvento,
                            fechaEvento,
                            precioEvento,
                            aforoMaximo,
                            nuevoAforoOcupado,
                            urlFoto
                        )
                    )
                    listener.onClick(position)
                    //Ahora debemos actualizar los eventos con los datos de reservas_eventos
                    dbRef.child("tienda").child("eventos").child(idEvento).setValue(
                        Evento(
                            idEvento,
                            nombreEvento,
                            formatoEvento,
                            fechaEvento,
                            precioEvento,
                            aforoMaximo,
                            nuevoAforoOcupado,
                            urlFoto
                        )
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int = listaFiltrada.size

    class EventoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val foto: ImageView = itemView.findViewById(R.id.ivFotoEvento)
        val nombreEvento: TextView = itemView.findViewById(R.id.tvNombreEvento)
        val formatoEvento: TextView = itemView.findViewById(R.id.tvFormatoEvento)
        val fechaEvento: TextView = itemView.findViewById(R.id.tvFechaEvento)
        val precio: TextView = itemView.findViewById(R.id.tvPrecioEvento)
        val aforoMaximo: TextView = itemView.findViewById(R.id.tvAforoMaximo)
        val aforoOcupado: TextView = itemView.findViewById(R.id.tvAforoOcupado)
        val boton: Button = itemView.findViewById(R.id.btnApuntarseEvento)
        val mensajeAforoMaximo: TextView = itemView.findViewById(R.id.tvAforoMaximo)
    }

    fun opcionesGlide(context: Context): RequestOptions {
        return RequestOptions()
            .placeholder(animacionCarga(context))
            .fallback(R.drawable.buried_joke)
            .error(R.drawable.error_404)
    }

    fun animacionCarga(contexto: Context): CircularProgressDrawable {
        val animacion = CircularProgressDrawable(contexto)
        animacion.strokeWidth = 5f
        animacion.centerRadius = 30f
        animacion.start()
        return animacion
    }

    val transicion = DrawableTransitionOptions.withCrossFade(500)
}