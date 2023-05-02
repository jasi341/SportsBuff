package com.example.myapplication

import android.app.Application
import com.buffup.sdk.BuffSdk

class ViewLiftApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        BuffSdk.initialize(
            context = this,
            clientAccount =  "sportbuff"
            //
        )
    }
}