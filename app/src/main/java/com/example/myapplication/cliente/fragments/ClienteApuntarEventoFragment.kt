package com.example.myapplication.cliente.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.administrador.model.adapter.rv.AdaptadorEventoAdministrador
import com.example.myapplication.cliente.model.adapter.rv.OnClickListener
import com.example.myapplication.data.model.Evento
import com.example.myapplication.data.model.ReservarEvento
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.data.model.UsuarioActual
import com.example.myapplication.databinding.FragmentClienteApuntarEventoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClienteApuntarEventoFragment : Fragment(), OnClickListener {

    private var _binding: FragmentClienteApuntarEventoBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Evento>
    private lateinit var adaptador: AdaptadorEventoAdministrador
    @Inject
    lateinit var dbRef: DatabaseReference
    private var idUsuario = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClienteApuntarEventoBinding.inflate(inflater, container, false)
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
        var listaIdEventos = obtenerListaEventosUsuario()
        lista = mutableListOf()
        dbRef.child("tienda").child("eventos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach{ evento ->
                    val pojoEvento = evento?.getValue(Evento::class.java)
                    if(pojoEvento!!.aforoOcupado != pojoEvento.aforo){
                        val idEvento = pojoEvento!!.id
                        if(!listaIdEventos.contains(idEvento)){
                            lista.add(pojoEvento)
                        }
                    }
                }
                recycler.adapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        //
        adaptador = AdaptadorEventoAdministrador(lista, findNavController(),this)
        apply {
            recycler = binding.rvMostrarEventos
            recycler.adapter = adaptador
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.setHasFixedSize(true)
        }

    }
    private fun obtenerListaEventosUsuario(): MutableList<String>{
        var lista = mutableListOf<String>()
        dbRef.child("tienda").child("reservas_eventos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach{ evento ->
                    val pojoEvento = evento?.getValue(ReservarEvento::class.java)
                    if(idUsuario == pojoEvento!!.idUsuario){
                        //Añado la id del evento
                        lista.add(pojoEvento.idEvento)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
        return lista
    }

    override fun onClick(posicion: Int) {
        configurarRecyclerView()
    }

    private fun configurarSearchView() {
        // Configurar el SearchView
        binding.svBuscaEvento.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        popupMenu?.menuInflater?.inflate(R.menu.popup_menu_administrador_eventos, popupMenu.menu)

        // Establecer un listener para manejar clics en las opciones del menú
        popupMenu?.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_sort_name -> {
                    // Lógica para la opción "ordenar alfabeticamente"
                    lista.sortBy { evento ->
                        evento.nombre
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_precio -> {
                    // Lógica para la opción "ordenar por precio"
                    lista.sortByDescending { evento ->
                        evento.precio
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_ocupacion -> {
                    // Lógica para la opción "ordenar por ocupacion"
                    lista.sortByDescending { evento ->
                        evento.aforoOcupado
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_aforo -> {
                    // Lógica para la opción "ordenar por aforo"
                    lista.sortByDescending { evento ->
                        evento.aforo
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_fecha -> {
                    // Lógica para la opción "ordenar por fecha"
                    lista.sortByDescending { evento ->
                        evento.fecha
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