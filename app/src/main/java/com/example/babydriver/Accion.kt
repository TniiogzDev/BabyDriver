package com.example.babydriver

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Accion(
    val fecha: String? = null,
    val hora: String? = null,
    val usuario: String? = null,
    val accion: String? = null,
    val distancia: String? = null
) : Parcelable
