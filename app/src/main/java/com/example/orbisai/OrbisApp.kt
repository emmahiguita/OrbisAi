package com.example.orbisai

import android.app.Application
import androidx.emoji2.text.EmojiCompat
import androidx.emoji2.text.DefaultEmojiCompatConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OrbisApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar EmojiCompat para evitar el error "EmojiCompat is not initialized"
        // Esta es la configuración mínima recomendada por Android
        val config = DefaultEmojiCompatConfig.create(this)
        EmojiCompat.init(config!!)
    }
}

/*
INSTRUCCIÓN PARA ANDROIDMANIFEST.XML:
Si el AndroidManifest.xml no tiene declarada una clase Application, agregar:
android:name=".OrbisApp"

Ejemplo:
<application
    android:name=".OrbisApp"
    ... otros atributos ...
>
*/
