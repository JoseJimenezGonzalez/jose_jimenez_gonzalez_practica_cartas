package com.example.myapplication.administrador.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.administrador.model.adapter.rv.AdaptadorCartaAdministrador
import com.example.myapplication.cliente.model.adapter.rv.OnClickListener
import com.example.myapplication.data.model.Carta
import com.example.myapplication.databinding.FragmentAdministradorGestionarCartasVerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.widget.SearchView
import com.example.myapplication.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdministradorGestionarCartasVerFragment : Fragment(), OnClickListener {

    private var _binding: FragmentAdministradorGestionarCartasVerBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Carta>
    private lateinit var adaptador: AdaptadorCartaAdministrador
    @Inject
    lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdministradorGestionarCartasVerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        configurarRecyclerView()
        configurarSearchView()
        configurarMenuPopup()
        configurarBotonFab()

    }

    private fun configurarBotonFab() {
        binding.fab.setOnClickListener {
            Toast.makeText(context, "Si pulsas sobre la carta la editas", Toast.LENGTH_LONG).show()
        }
    }

    private fun configurarRecyclerView() {
        lista = mutableListOf()
        dbRef.child("tienda").child("cartas").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach{hijo: DataSnapshot? ->
                    val pojoCarta = hijo?.getValue(Carta::class.java)
                    lista.add(pojoCarta!!)
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        //
        adaptador = AdaptadorCartaAdministrador(lista, findNavController(), this)
        apply {
            recycler = binding.rvMostrarCartas
            recycler.adapter = adaptador
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.setHasFixedSize(true)
        }

    }

    override fun onClick(posicion: Int) {
        //Nada
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
        popupMenu?.menuInflater?.inflate(R.menu.popup_menu_administrador_ver_cartas, popupMenu.menu)

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

                R.id.action_sort_stock -> {
                    // Lógica para la opción "ordenar por stock"
                    lista.sortByDescending { venta ->
                        venta.stock
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_filtrar_color ->{
                    // Crear instancia de PopupMenu para el filtrado por color
                    val filtrarColorPopupMenu = android.widget.PopupMenu(context, view)
                    filtrarColorPopupMenu.menuInflater.inflate(R.menu.popup_menu_filtrar_color, filtrarColorPopupMenu.menu)
                    // Establecer un listener para manejar clics en las opciones del menú de filtrado por color
                    filtrarColorPopupMenu.setOnMenuItemClickListener { filtrarColorMenuItem: MenuItem ->
                        // Aquí maneja la lógica para las opciones de filtrado por color según el item seleccionado
                        when (filtrarColorMenuItem.itemId) {

                            R.id.action_negro -> {
                                lista.retainAll { carta ->
                                    carta.color == "Negro"
                                }
                                adaptador.notifyDataSetChanged()
                                true
                            }
                            R.id.action_blanco -> {
                                lista.retainAll { carta ->
                                    carta.color == "Blanco"
                                }
                                adaptador.notifyDataSetChanged()
                                true
                            }

                            R.id.action_rojo -> {
                                lista.retainAll { carta ->
                                    carta.color == "Rojo"
                                }
                                adaptador.notifyDataSetChanged()
                                true
                            }

                            R.id.action_verde -> {
                                lista.retainAll { carta ->
                                    carta.color == "Verde"
                                }
                                adaptador.notifyDataSetChanged()
                                true
                            }

                            R.id.action_azul -> {
                                lista.retainAll { carta ->
                                    carta.color == "Azul"
                                }
                                adaptador.notifyDataSetChanged()
                                true
                            }

                            R.id.action_artefacto -> {
                                lista.retainAll { carta ->
                                    carta.color == "Incoloro"
                                }
                                adaptador.notifyDataSetChanged()
                                true
                            }

                            R.id.action_multicolor -> {
                                lista.retainAll { carta ->
                                    carta.color == "Multicolor"
                                }
                                adaptador.notifyDataSetChanged()
                                true
                            }
                            else -> false
                        }
                    }
                    filtrarColorPopupMenu.show()
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

                else -> false
            }
        }
        // Mostrar el menú emergente
        popupMenu?.show()
    }
}