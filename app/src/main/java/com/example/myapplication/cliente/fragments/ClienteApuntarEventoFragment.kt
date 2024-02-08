package com.example.myapplication.cliente.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    }
    private fun configurarRecyclerView() {
        var listaIdEventos = obtenerListaEventosUsuario()
        lista = mutableListOf()
        dbRef.child("tienda").child("eventos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach{hijo: DataSnapshot? ->
                    val pojoEvento = hijo?.getValue(Evento::class.java)
                    val idEvento = pojoEvento!!.id
                    if(!listaIdEventos.contains(idEvento)){
                        lista.add(pojoEvento)
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
}