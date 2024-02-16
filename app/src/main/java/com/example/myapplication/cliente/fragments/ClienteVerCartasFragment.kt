package com.example.myapplication.cliente.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.administrador.model.adapter.rv.AdaptadorPedidosAdministrador
import com.example.myapplication.cliente.model.adapter.rv.OnClickListener
import com.example.myapplication.data.model.ReservarCarta
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.data.model.UsuarioActual
import com.example.myapplication.databinding.FragmentClienteVerCartasBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ClienteVerCartasFragment : Fragment(), OnClickListener {

    private var _binding: FragmentClienteVerCartasBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<ReservarCarta>
    private lateinit var adaptador: AdaptadorPedidosAdministrador
    @Inject
    lateinit var dbRef: DatabaseReference

    var idUsuario = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClienteVerCartasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        if(UsuarioActual.usuarioActual != null){
            val usuarioActual: Usuario = UsuarioActual.usuarioActual!!
            idUsuario = usuarioActual.idUsuario
        }

        configurarRecyclerView()
        configurarMenuPopup()
        configurarSearchView()

    }

    private fun configurarRecyclerView() {
        lista = mutableListOf()
        dbRef.child("tienda").child("reservas_carta").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach{ cartaReserva ->
                    val pojoCartaReserva = cartaReserva?.getValue(ReservarCarta::class.java)
                    if(pojoCartaReserva!!.idUsuario == idUsuario && pojoCartaReserva.estado == "preparado"){
                        lista.add(pojoCartaReserva)
                    }
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        //
        adaptador = AdaptadorPedidosAdministrador(lista, this)
        apply {
            recycler = binding.rvMostrarCartas
            recycler.adapter = adaptador
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.setHasFixedSize(true)
        }

    }

    override fun onClick(posicion: Int) {
        //
    }

    private fun configurarSearchView() {
        // Configurar el SearchView
        binding.svBuscarCarta.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun configurarMenuPopup() {
        //Boton popup
        binding.ivMenuOpciones.setOnClickListener {
            mostrarPopupMenu(it)
        }
    }

    private fun mostrarPopupMenu(view: View?) {
        // Crear instancia de PopupMenu
        val popupMenu = view?.let { android.widget.PopupMenu(context, it) }

        // Inflar el menú desde el archivo XML
        popupMenu?.menuInflater?.inflate(R.menu.popup_menu_administrador_ventas, popupMenu.menu)

        // Establecer un listener para manejar clics en las opciones del menú
        popupMenu?.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_sort_name -> {
                    // Lógica para la opción "ordenar alfabeticamente"
                    lista.sortBy { venta ->
                        venta.nombreCarta
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_precio -> {
                    // Lógica para la opción "ordenar por precio"
                    lista.sortByDescending { venta ->
                        venta.precio
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_color -> {
                    // Lógica para la opción "ordenar por color"
                    lista.sortByDescending { venta ->
                        venta.color
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_stock -> {
                    // Lógica para la opción "ordenar por fecha"
                    lista.sortByDescending { venta ->
                        venta.fecha
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                else -> false
            }
        }
        // Mostrar el menú emergente
        popupMenu?.show()
    }

}