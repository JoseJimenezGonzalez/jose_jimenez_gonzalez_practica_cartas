package com.example.myapplication.administrador.model.adapter.rv

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.administrador.fragments.AdministradorGestionarCartasModificarFragment
import com.example.myapplication.administrador.fragments.AdministradorGestionarEventosModificarFragment
import com.example.myapplication.data.model.Carta
import com.example.myapplication.data.model.Evento

class AdaptadorEventoAdministrador (private val listaEventos: MutableList<Evento>, private val navController: NavController): RecyclerView.Adapter<AdaptadorEventoAdministrador.EventoViewHolder>(){
    private lateinit var contexto: Context
    private var listaFiltrada = listaEventos

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorEventoAdministrador.EventoViewHolder {
        val vistaItem = LayoutInflater.from(parent.context).inflate(R.layout.item_evento_administrador, parent, false)
        contexto = parent.context
        return EventoViewHolder(vistaItem)
    }

    override fun onBindViewHolder(holder: AdaptadorEventoAdministrador.EventoViewHolder, position: Int) {
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

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("evento", itemActual)
            val fragment = AdministradorGestionarEventosModificarFragment()
            fragment.arguments = bundle
            //Cambiar la direccion
            navController.navigate(R.id.action_administradorGestionarCartasFragment_to_administradorGestionarCartasModificarFragment, bundle)
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