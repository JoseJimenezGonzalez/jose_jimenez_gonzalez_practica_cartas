package com.example.myapplication.administrador.model.adapter.rv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.cliente.model.adapter.rv.OnClickListener
import com.example.myapplication.data.model.ReservarCarta

class AdaptadorPedidosAdministrador(private val listaCartas: MutableList<ReservarCarta>, private val listener: OnClickListener): RecyclerView.Adapter<AdaptadorPedidosAdministrador.PedidoCartaViewHolder>() {
    private lateinit var contexto: Context
    private var listaFiltrada = listaCartas

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorPedidosAdministrador.PedidoCartaViewHolder {
        val vistaItem = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        contexto = parent.context
        return PedidoCartaViewHolder(vistaItem)
    }

    override fun onBindViewHolder(holder: AdaptadorPedidosAdministrador.PedidoCartaViewHolder, position: Int) {

        val itemActual = listaFiltrada[position]

        holder.nombreCarta.text = itemActual.nombreCarta
        holder.nombreExpansion.text = itemActual.nombreExpansion
        holder.precio.text = itemActual.precio.toString()
        holder.colorCarta.text = itemActual.color
        holder.estadoCarta.text = itemActual.estado
        holder.idCarta.text = itemActual.idCarta
        holder.idUsuario.text = itemActual.idUsuario
        holder.idReserva.text = itemActual.idReserva

        val URL: String? = when(itemActual.urlImagenCarta){
            "" -> null
            else -> itemActual.urlImagenCarta
        }

        Glide.with(contexto)
            .load(URL)
            .apply(opcionesGlide(contexto))
            .transition(transicion)
            .into(holder.foto)

        holder.botonReserva.setOnClickListener {
            //Pasarlo a procesado


        }

        if(itemActual.estado == "preparado"){
            holder.botonReserva.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = listaFiltrada.size

    class PedidoCartaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val nombreCarta: TextView = itemView.findViewById(R.id.tvNombreCarta)
        val nombreExpansion: TextView = itemView.findViewById(R.id.tvExpansion)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val colorCarta: TextView = itemView.findViewById(R.id.tvColor)
        val estadoCarta: TextView = itemView.findViewById(R.id.tvEstadoCarta)
        val foto: ImageView = itemView.findViewById(R.id.ivFotoCarta)
        val idCarta: TextView = itemView.findViewById(R.id.tvIdCarta)
        val idUsuario: TextView = itemView.findViewById(R.id.tvIdUsuario)
        val idReserva: TextView = itemView.findViewById(R.id.tvIdReserva)
        val botonReserva: Button = itemView.findViewById(R.id.btnProcesarPedido)
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