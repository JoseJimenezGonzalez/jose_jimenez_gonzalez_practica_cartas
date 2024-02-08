package com.example.myapplication.administrador.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.administrador.model.adapter.rv.AdaptadorPedidosAdministrador
import com.example.myapplication.cliente.model.adapter.rv.OnClickListener
import com.example.myapplication.data.model.ReservarCarta
import com.example.myapplication.databinding.FragmentAdministradorGestionarVentasNoProcesadosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdministradorGestionarVentasNoProcesadosFragment : Fragment(), OnClickListener {

    private var _binding: FragmentAdministradorGestionarVentasNoProcesadosBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<ReservarCarta>
    private lateinit var adaptador: AdaptadorPedidosAdministrador
    @Inject
    lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdministradorGestionarVentasNoProcesadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        configurarRecyclerView()
    }

    private fun configurarRecyclerView() {
        lista = mutableListOf()
        dbRef.child("tienda").child("reservas_carta").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach{ reserva ->
                    val pojoReserva = reserva?.getValue(ReservarCarta::class.java)
                    if(pojoReserva!!.estado == "preparacion"){
                        lista.add(pojoReserva!!)
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
            recycler = binding.rvGestionarVentasNoProcesadas
            recycler.adapter = adaptador
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.setHasFixedSize(true)
        }

    }

    override fun onClick(posicion: Int) {
        configurarRecyclerView()
    }
}