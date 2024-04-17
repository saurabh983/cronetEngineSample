package com.saurabh.cronetenginesample

import android.content.Context
import org.chromium.net.CronetEngine

object CronetEngineInstance {
    private var cronetEngine: CronetEngine ?= null

    fun getCronetInstance(context: Context): CronetEngine?{
        if (cronetEngine == null){
            cronetEngine = CronetEngine.Builder(context).build()
        }

        return cronetEngine
    }
}