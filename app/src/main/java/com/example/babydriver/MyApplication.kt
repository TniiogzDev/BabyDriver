package com.example.babydriver

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

class MyApplication : Application(), LifecycleEventObserver {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> MusicManager.onAppForegrounded()
            Lifecycle.Event.ON_STOP -> MusicManager.onAppBackgrounded()
            else -> {}
        }
    }
}
