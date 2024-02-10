package com.example.myapplication.cliente.model.adapter.rv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.data.model.DivisaActual
import com.example.myapplication.data.model.ReservarEvento

class AdaptadorEventosCliente (private val listaEventosUsuario: MutableList<ReservarEvento>): RecyclerView.Adapter<AdaptadorEventosCliente.ReservarEventoViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var listaFiltrada = listaEventosUsuario
    val equivalenciaDolares = DivisaActual.dolar

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorEventosCliente.ReservarEventoViewHolder {
        val vistaItem = LayoutInflater.from(parent.context).inflate(R.layout.item_evento_usuario, parent, false)
        contexto = parent.context
        return ReservarEventoViewHolder(vistaItem)
    }

    override fun onBindViewHolder(holder: AdaptadorEventosCliente.ReservarEventoViewHolder, position: Int) {

        val itemActual = listaFiltrada[position]

        holder.nombreEvento.text = "Nombre del torneo: " + itemActual.nombre
        holder.formatoEvento.text = "Formato del torneo: " + itemActual.formato
        if(equivalenciaDolares == 0.0){
            holder.precio.text = "Precio del torneo: " + itemActual.precio.toString() + " euros"
        }else{
            val precioEnDolares = itemActual.precio * equivalenciaDolares!!
            val precioFormateado = String.format("%.2f", precioEnDolares)
            holder.precio.text = "Precio: $precioFormateado dolares"
        }
        holder.fechaEvento.text = "Fecha del evento: " + itemActual.fecha
        holder.aforoMaximo.text = "Aforo maximo del torneo: " + itemActual.aforo.toString()
        holder.aforoOcupado.text = "Aforo ocupado del torneo: " + itemActual.aforoOcupado.toString()

        val URL: String? = when(itemActual.urlImagenEvento){
            "" -> null
            else -> itemActual.urlImagenEvento
        }

        Glide.with(contexto)
            .load(URL)
            .apply(opcionesGlide(contexto))
            .transition(transicion)
            .into(holder.foto)

    }

    override fun getItemCount(): Int = listaFiltrada.size

    class ReservarEventoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val foto: ImageView = itemView.findViewById(R.id.ivFotoEvento)
        val nombreEvento: TextView = itemView.findViewById(R.id.tvNombreEvento)
        val formatoEvento: TextView = itemView.findViewById(R.id.tvFormatoEvento)
        val fechaEvento: TextView = itemView.findViewById(R.id.tvFechaEvento)
        val precio: TextView = itemView.findViewById(R.id.tvPrecioEvento)
        val aforoMaximo: TextView = itemView.findViewById(R.id.tvAforoMaximo)
        val aforoOcupado: TextView = itemView.findViewById(R.id.tvAforoOcupado)
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
                    listaFiltrada = listaEventosUsuario
                }else {
                    listaFiltrada = (listaEventosUsuario.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<ReservarEvento>
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
            listaFiltrada = listaEventosUsuario
        } else {
            listaFiltrada = listaEventosUsuario.filter {
                it.nombre.toString().lowercase().contains(busqueda)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}