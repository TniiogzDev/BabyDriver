package com.example.babydriver

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class RutTextWatcher(private val editText: EditText) : TextWatcher {

    private var isFormatting = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return

        isFormatting = true

        // 1. Limpiar el texto de cualquier formato previo
        val rutDigits = s.toString().replace(Regex("[^0-9kK]"), "")

        // 2. Aplicar el nuevo formato
        val formattedRut = formatRut(rutDigits)

        // 3. Actualizar el EditText
        editText.setText(formattedRut)
        editText.setSelection(formattedRut.length)

        isFormatting = false
    }

    private fun formatRut(rut: String): String {
        if (rut.isEmpty()) return ""

        // Separar el cuerpo del dígito verificador
        var body = rut.substring(0, rut.length - 1)
        var dv = rut.substring(rut.length - 1)

        // Si el último caracter no es un dígito verificador válido, lo movemos al cuerpo
        if (!dv.matches(Regex("[0-9kK]"))) {
            body += dv
            dv = ""
        }

        // Formatear el cuerpo con puntos
        val formattedBody = body.reversed().chunked(3).joinToString(".").reversed()

        return if (dv.isNotEmpty()) "$formattedBody-$dv" else formattedBody
    }
}
