package com.example.ejercicio2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat

class ImageZoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)

        // Carga las imágenes si existen en drawable, si no usa iconos del sistema como fallback
        val pkg = packageName
        val ids = listOf("img_task1", "img_task2", "img_task3")
        val viewIds = listOf(
            R.id.zoomImage1,
            R.id.zoomImage2,
            R.id.zoomImage3
        )

        ids.forEachIndexed { index, name ->
            val resId = resources.getIdentifier(name, "drawable", pkg)
            val view = findViewById<ZoomableImageView>(viewIds[index])
            if (resId != 0) {
                view.setImageDrawable(ResourcesCompat.getDrawable(resources, resId, theme))
            } else {
                // fallback icon
                view.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }

        Toast.makeText(this, "Desliza/pellizca para hacer zoom. Doble tap para alternar.", Toast.LENGTH_LONG).show()
    }
}
