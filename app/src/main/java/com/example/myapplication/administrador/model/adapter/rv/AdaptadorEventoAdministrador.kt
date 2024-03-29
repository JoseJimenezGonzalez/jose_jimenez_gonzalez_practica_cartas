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
import android.widget.Filter
import android.widget.Filterable
import com.example.myapplication.data.model.DivisaActual

class AdaptadorEventoAdministrador (private val listaEventos: MutableList<Evento>, private val navController: NavController, private val listener: OnClickListener): RecyclerView.Adapter<AdaptadorEventoAdministrador.EventoViewHolder>(), Filterable{
    private lateinit var contexto: Context
    private var listaFiltrada = listaEventos

    private var tipoUsuario = ""
    private var idUsuario = ""
    val equivalenciaDolares = DivisaActual.dolar

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

        holder.nombreEvento.text = "Nombre del torneo: " + itemActual.nombre
        holder.formatoEvento.text = "Formato del torneo: " + itemActual.formato
        if(equivalenciaDolares == 0.0){
            holder.precio.text = "Precio: " + itemActual.precio.toString() + " euros"
        }else{
            val precioEnDolares = itemActual.precio * equivalenciaDolares!!
            val precioFormateado = String.format("%.2f", precioEnDolares)
            holder.precio.text = "Precio: $precioFormateado dolares"
        }
        holder.fechaEvento.text = "Fecha del evento: " + itemActual.fecha
        holder.aforoMaximo.text = "Aforo maximo del torneo: " + itemActual.aforo.toString() + " plazas"
        holder.aforoOcupado.text = "Aforo ocupado: " + itemActual.aforoOcupado.toString() + " plazas"

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
                navController.navigate(R.id.administradorGestionarEventosModificarFragment, bundle)
            }
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

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()){
                    listaFiltrada = listaEventos
                }else {
                    listaFiltrada = (listaEventos.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<Evento>
                }

                val filterResults = FilterResults()
                filterResults.values = listaFiltrada
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

    // Método para filtrar la lista
    fun filter(newText: String) {
        val busqueda = newText.lowercase()
        if (busqueda.isEmpty()) {
            listaFiltrada = listaEventos
        } else {
            listaFiltrada = listaEventos.filter {
                it.nombre.toString().lowercase().contains(busqueda)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}