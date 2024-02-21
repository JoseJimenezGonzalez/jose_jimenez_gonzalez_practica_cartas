package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.databinding.ActivityRegistrarUsuarioBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegistrarUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarUsuarioBinding

    private lateinit var auth: FirebaseAuth

    lateinit var dbRef: DatabaseReference

    lateinit var nombreUsuariosAplicacion: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nombreUsuariosAplicacion = mutableListOf()

        // Initialize Firebase Auth
        auth = Firebase.auth

        obtenerNombreUsuariosAplicacion()

        configurarBotonIniciarSesion()

        configurarBotonRegistrarse()
    }

    private fun configurarBotonRegistrarse() {
        binding.btnRegistrarUsuario.setOnClickListener {
            //Primero nos traemos todos los datos del usuario
            val nombre = binding.tietNombre.text.toString().trim()
            val apellidos = binding.tietApellidos.text.toString().trim()
            val email = binding.tietCorreo.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()
            val repeatPassword = binding.tietRepetirPassword.text.toString().trim()
            //Declaramos los chivatos de estas variables
            var esNombreCorrecto = false
            var esApellidoCorrecto = false
            var esEmailCorrecto = false
            var tienePasswordLongitudCorrecta = false
            var tieneRepetirPasswordLongitudCorrecta = false
            var sonPasswordIguales = false
            //Comprobamos los campos
            //Nombre
            if (nombre.isNotBlank()){
                binding.tietNombre.error = null
                esNombreCorrecto = true
            }else{
                binding.tietNombre.error = "No puede estar vacío"
            }
            //Apellidos
            if (apellidos.isNotBlank()){
                binding.tietApellidos.error = null
                esApellidoCorrecto = true
            }else{
                binding.tietApellidos.error = "No puede estar vacío"
            }
            //Correo
            esEmailCorrecto = esCorreoElectronico(email)
            if(esEmailCorrecto){
                binding.tietCorreo.error = null
            }else{
                binding.tietCorreo.error = "No tiene formato de correo"
            }
            //Passwords
            //Tienen que ser iguales
            if(password == repeatPassword){
                sonPasswordIguales = true
                binding.tietPassword.error = null
                binding.tietRepetirPassword.error = null
            }else{
                binding.tietPassword.error = "Las contraseñas tienen que ser iguales"
                binding.tietRepetirPassword.error = "Las contraseñas tienen que ser iguales"
            }

            //Longitud
            if(password.length < 8){
                binding.tietPassword.error = "Tiene que tener mínimo longitud 8"
            }else{
                binding.tietPassword.error = null
                tienePasswordLongitudCorrecta = true

            }

            if(repeatPassword.length < 8){
                binding.tietRepetirPassword.error = "Tiene que tener mínimo longitud 8"
            }else{
                binding.tietRepetirPassword.error = null
                tieneRepetirPasswordLongitudCorrecta = true

            }

            if(esNombreCorrecto && esApellidoCorrecto && esEmailCorrecto && sonPasswordIguales && tienePasswordLongitudCorrecta && tieneRepetirPasswordLongitudCorrecta){
                val nombreCompleto = "$nombre $apellidos".lowercase()
                val existeUsuario = existeUsuario(nombreCompleto)
                //Logs
                Log.e("Nombre teclado", nombreCompleto)
                Log.e("Lista usuarios", nombreUsuariosAplicacion.toString())
                if(existeUsuario){
                    Toast.makeText(this, "Ya existe un usuario con ese nombre", Toast.LENGTH_SHORT).show()
                }else{
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Registro ok
                                Toast.makeText(baseContext, "Registro completado.", Toast.LENGTH_SHORT,).show()
                                //Vamos a registrarlo en nuetra base de datos
                                val user = auth.currentUser
                                val idUsuario = user?.uid
                                val esAdministrador = esUsuarioAdministrador(email)
                                val tipoUsuario = devolverTipoDeUsuario(esAdministrador)
                                registrarEnBaseDeDatos(idUsuario, nombre, apellidos, email, password, tipoUsuario)
                            } else {
                                // Ha fallado el registro
                                Toast.makeText(baseContext, "Hay una cuenta con ese email", Toast.LENGTH_SHORT,).show()
                            }
                        }
                }

            }
        }
    }

    private fun registrarEnBaseDeDatos(idUsuario: String?, nombre: String, apellidos: String, email: String, password: String, tipoDeUsuario: String) {
        dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("tienda").child("usuarios").child(idUsuario!!).setValue(
            Usuario(
                idUsuario,
                nombre,
                apellidos,
                email,
                password,
                tipoDeUsuario
            )
        )
    }

    private fun obtenerNombreUsuariosAplicacion(){
        dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("tienda").child("usuarios").addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    nombreUsuariosAplicacion.clear()
                    snapshot.children.forEach { usuario->
                        val pojoUsuario = usuario.getValue(Usuario::class.java)
                        val nombre = pojoUsuario!!.nombreUsuario
                        val apellidos = pojoUsuario.apellidosUsuario
                        val nombreCompleto = "$nombre $apellidos".lowercase()
                        Log.e("Nombre usuario", nombreCompleto)
                        nombreUsuariosAplicacion.add(nombreCompleto)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Nada
                }

            }
        )
    }

    private fun existeUsuario(nombreCompleto: String): Boolean = nombreCompleto in nombreUsuariosAplicacion

    private fun configurarBotonIniciarSesion() {
        binding.tvVolverIniciarSesion.setOnClickListener {
            val intent = Intent(this, IniciarSesionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun esCorreoElectronico(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun esUsuarioAdministrador(email: String): Boolean = email=="administrador@gmail.com"

    private fun devolverTipoDeUsuario(bol: Boolean): String = if (bol) "administrador" else "cliente"
}