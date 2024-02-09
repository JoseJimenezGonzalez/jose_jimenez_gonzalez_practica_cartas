package com.example.myapplication.administrador.model.adapter.rv

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
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
import com.example.myapplication.cliente.model.adapter.rv.OnClickListener
import com.example.myapplication.data.model.Carta
import com.example.myapplication.data.model.ReservarCarta
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.data.model.UsuarioActual
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AdaptadorCartaAdministrador(private val listaCartas: MutableList<Carta>, private val navController: NavController, private val listener: OnClickListener): RecyclerView.Adapter<AdaptadorCartaAdministrador.CartaViewHolder>(), Filterable {


    private lateinit var contexto: Context
    private var listaFiltrada = listaCartas
    var tipoUsuario = ""
    var idUsuario = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdaptadorCartaAdministrador.CartaViewHolder {
        val vistaItem = LayoutInflater.from(parent.context).inflate(R.layout.item_carta_administrador, parent, false)
        contexto = parent.context
        return CartaViewHolder(vistaItem)
    }

    override fun onBindViewHolder(holder: AdaptadorCartaAdministrador.CartaViewHolder, position: Int) {

        val itemActual = listaFiltrada[position]

        holder.nombreCarta.text = itemActual.nombreCarta
        holder.nombreExpansion.text = itemActual.nombreExpansion
        holder.precio.text = itemActual.precio.toString()
        holder.stock.text = itemActual.stock.toString()
        holder.disponibilidad.text = itemActual.disponibilidad
        holder.colorCarta.text = itemActual.color

        val URL: String? = when(itemActual.urlImagenCarta){
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
            bundle.putParcelable("carta", itemActual)
            val fragment = AdministradorGestionarCartasModificarFragment()
            fragment.arguments = bundle
            navController.navigate(R.id.action_administradorGestionarCartasFragment_to_administradorGestionarCartasModificarFragment, bundle)
        }

        //Obtenemos el usuario
        if(UsuarioActual.usuarioActual != null){
            val usuarioActual: Usuario = UsuarioActual.usuarioActual!!
            tipoUsuario = usuarioActual.tipoDeUsuario
            idUsuario = usuarioActual.tipoDeUsuario
        }

        if(tipoUsuario == "administrador"){

            holder.boton.visibility = View.GONE

        }else{

            //Añadimos la carta a la base de datos
            holder.boton.setOnClickListener {

                val dbRef = FirebaseDatabase.getInstance().reference

                val idCarta = itemActual.idCarta
                val nombreCarta = itemActual.nombreCarta
                val nombreExpansion = itemActual.nombreExpansion
                val precio = itemActual.precio
                val stock = itemActual.stock
                val nuevoStock = stock - 1
                val disponibilidad = itemActual.disponibilidad
                val color = itemActual.color
                val urlImagenCarta = itemActual.urlImagenCarta
                //Lo metemos como en preparacion, hasta que el admin no confirme no pasa a preparado
                val estadoPedido = "preparacion"
                val idReservaCarta = dbRef.child("tienda").child("reservas_carta").push().key
                //Obtenemos la fecha actual
                val fechaActualSinFormatear = Date()
                val fechaActualFormateada = obtenerFormateada(fechaActualSinFormatear)
                //La añadimos a preparacion
                dbRef.child("tienda").child("reservas_carta").child(idReservaCarta!!).setValue(
                    ReservarCarta(
                        idCarta,
                        nombreCarta,
                        nombreExpansion,
                        precio,
                        color,
                        urlImagenCarta,
                        idReservaCarta,
                        idUsuario,
                        estadoPedido,
                        fechaActualFormateada
                    )
                )
                //Le quitamos 1 de stock
                dbRef.child("tienda").child("cartas").child(idCarta).setValue(
                    Carta(
                        idCarta,
                        nombreCarta,
                        nombreExpansion,
                        precio,
                        nuevoStock,
                        disponibilidad,
                        color,
                        urlImagenCarta
                    )
                )
                listener.onClick(position)

            }
        }
    }

    override fun getItemCount(): Int = listaFiltrada.size

    class CartaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val foto: ImageView = itemView.findViewById(R.id.ivFotoCarta)
        val nombreCarta: TextView = itemView.findViewById(R.id.tvNombreCarta)
        val nombreExpansion: TextView = itemView.findViewById(R.id.tvExpansion)
        val colorCarta: TextView = itemView.findViewById(R.id.tvColor)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val stock: TextView = itemView.findViewById(R.id.tvStock)
        val disponibilidad: TextView = itemView.findViewById(R.id.tvDisponibilidad)
        val boton: Button = itemView.findViewById(R.id.btnComprarCarta)
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

    private fun obtenerFormateada(fechaLanzamiento: Date): String {
        return SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(fechaLanzamiento)
    }

    val transicion = DrawableTransitionOptions.withCrossFade(500)

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()){
                    listaFiltrada = listaCartas
                }else {
                    listaFiltrada = (listaCartas.filter {
                        it.nombreCarta.toString().lowercase().contains(busqueda)
                    }) as MutableList<Carta>
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
            listaFiltrada = listaCartas
        } else {
            listaFiltrada = listaCartas.filter {
                it.nombreCarta.toString().lowercase().contains(busqueda)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}