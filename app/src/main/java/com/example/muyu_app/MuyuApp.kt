package com.example.muyu_app

import android.app.Application
import com.example.muyu_app.core.di.AppContainer

class MuyuApp : Application() {
    val container by lazy { AppContainer(this) }
}
