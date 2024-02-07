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

        holder.nombreCarta.text = itemActual.nombreCarta
        holder.nombreExpansion.text = itemActual.nombreExpansion
        holder.precio.text = itemActual.precio.toString()
        holder.stock.text = itemActual.stock.toString()
        holder.disponibilidad.text = itemActual.disponibilidad
        holder.colorCarta.text = itemActual.color

        val URL: String? = when(itemActual.url){
            "" -> null
            else -> itemActual.urlImagenCarta
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
        val foto: ImageView = itemView.findViewById(R.id.ivFotoCarta)
        val nombreCarta: TextView = itemView.findViewById(R.id.tvNombreCarta)
        val nombreExpansion: TextView = itemView.findViewById(R.id.tvExpansion)
        val colorCarta: TextView = itemView.findViewById(R.id.tvColor)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val stock: TextView = itemView.findViewById(R.id.tvStock)
        val disponibilidad: TextView = itemView.findViewById(R.id.tvDisponibilidad)
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