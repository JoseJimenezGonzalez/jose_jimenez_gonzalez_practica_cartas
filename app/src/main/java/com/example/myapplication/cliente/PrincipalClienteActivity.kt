package com.example.myapplication.cliente

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.administrador.PrincipalAdministradorActivity
import com.example.myapplication.data.model.Estado
import com.example.myapplication.data.model.ReservarCarta
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.data.model.UsuarioActual
import com.example.myapplication.databinding.ActivityPrincipalClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@AndroidEntryPoint
class PrincipalClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalClienteBinding

    private lateinit var navController: NavController

    @Inject
    lateinit var dbRef: DatabaseReference

    @Inject
    lateinit var stRef: StorageReference

    private var idUsuario = "usuario"

    private lateinit var androidId: String
    private lateinit var generador: AtomicInteger
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //tema

        if(UsuarioActual.usuarioActual != null){
            val usuarioActual: Usuario = UsuarioActual.usuarioActual!!
            idUsuario = usuarioActual.idUsuario
        }
        //Codigo
        val nombrePref = idUsuario

        // Obtener el estado del modo día/noche almacenado en SharedPreferences
        val sharedPreferences = getSharedPreferences(nombrePref, Context.MODE_PRIVATE)
        val modoDia = sharedPreferences.getBoolean("modo_dia", true)

        // Aplicar el tema según el estado almacenado en SharedPreferences
        if (modoDia) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }


        initUI()

        crearCanalNotificaciones()
        androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        generador = AtomicInteger(0)


        //CONTROLADOR NOTIFICACIONES
        dbRef.child("tienda").child("reservas_carta")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojoReservaCarta = snapshot.getValue(ReservarCarta::class.java)
                    if (pojoReservaCarta != null && !pojoReservaCarta.idNotificacion.equals(androidId) && pojoReservaCarta.idUsuario == idUsuario) {
                        val estadoActual = pojoReservaCarta.estado
                        if (estadoActual == "preparado") {
                            // Aquí manejas la notificación para pedidos que pasaron de "en preparación" a "preparado"
                            dbRef.child("tienda").child("reservas_carta").child(pojoReservaCarta.idReserva!!)
                                .child("estadoNotificacion").setValue(Estado.NOTIFICADO)
                            generarNotificacion(generador.incrementAndGet(), pojoReservaCarta,
                                "Tu pedido de " + pojoReservaCarta.nombreCarta + " está preparado.",
                                "Pedido preparado",
                                PrincipalClienteActivity::class.java)
                        }
                    }
                }



                override fun onChildRemoved(snapshot: DataSnapshot) {


                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_cliente) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavigationViewCliente.setupWithNavController(navController)
    }

    @SuppressLint("ServiceCast")
    private fun generarNotificacion(
        id_noti: Int,
        pojo: Parcelable,
        contenido: String,
        titulo: String,
        destino: Class<*>
    ) {
        val id = "Canal de prueba"
        val actividad = Intent(applicationContext, destino).apply{
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //Para evitar bugs en la aplicacion
        }

        actividad.putExtra("carta", pojo)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, actividad, PendingIntent.FLAG_IMMUTABLE)

        val notificacion = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.ic_cart_shopping)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("Sistema de notificación")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id_noti, notificacion)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun crearCanalNotificaciones() {
        val nombre = "canal_basico"
        val id = "canal cliente"
        val descripcion = "Notificacion basica"
        val importancia = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(id, nombre, importancia).apply {
            description = descripcion
        }

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }
}