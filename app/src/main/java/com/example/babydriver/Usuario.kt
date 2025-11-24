package com.example.babydriver

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    val key: String? = null, // Para guardar el ID de Firebase (user01, user02...)
    val rut: String? = null, // RUT será el nuevo identificador
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val tipo: String? = null,
    val contraseña: String? = null // Cambiado a String para guardar la contraseña cifrada
) : Parcelable
