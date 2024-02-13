package com.example.myapplication.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReservarCarta(
    val idCarta: String = "",
    val nombreCarta: String = "",
    val nombreExpansion: String = "",
    val precio: Double = 0.0,
    val color: String = "",
    val urlImagenCarta: String = "",
    val idReserva: String = "",
    val idUsuario: String = "",
    val estado: String = "",
    val fecha: String = "",
    val estadoNotificacion: Int = 0,
    val idNotificacion: String = ""
) : Parcelable
