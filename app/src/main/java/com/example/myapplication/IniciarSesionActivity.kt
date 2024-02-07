package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.administrador.PrincipalAdministradorActivity
import com.example.myapplication.cliente.PrincipalClienteActivity
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.data.model.UsuarioActual
import com.example.myapplication.databinding.ActivityIniciarSesionBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class IniciarSesionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIniciarSesionBinding

    private lateinit var auth: FirebaseAuth

    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        binding = ActivityIniciarSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        configurarBotonRegistroUsuario()

        configurarInicioSesion()

    }

    private fun configurarInicioSesion() {
        binding.btnIniciarSesion.setOnClickListener {
            val email = binding.tietCorreo.text.toString()
            val password = binding.tietPassword.text.toString()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        dbRef = FirebaseDatabase.getInstance().reference.database.reference.child("tienda").child("usuarios")
                        val user = auth.currentUser
                        val idUsuario = user!!.uid
                        //usuario
                        dbRef.child(idUsuario).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    //Toda la informacion del usuario
                                    val usuario = snapshot.getValue(Usuario::class.java)
                                    val tipoUsuario = usuario!!.tipoDeUsuario
                                    //Lo meto en el companion
                                    UsuarioActual.usuarioActual = usuario
                                    if(tipoUsuario == "administrador"){
                                        val intent = Intent(this@IniciarSesionActivity, PrincipalAdministradorActivity::class.java)
                                        startActivity(intent)
                                    }else{
                                        val intent = Intent(this@IniciarSesionActivity, PrincipalClienteActivity::class.java)
                                        startActivity(intent)
                                    }
                                } else {
                                    println("El usuario con ID $idUsuario no existe")
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                println("Error al obtener datos del usuario: $error")
                            }

                        })
                    } else {
                        Toast.makeText(baseContext, "Fallo al iniciar sesión.", Toast.LENGTH_SHORT,).show()
                    }
                }

        }
    }

    private fun configurarBotonRegistroUsuario() {
        binding.tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegistrarUsuarioActivity::class.java)
            startActivity(intent)
        }
    }
}