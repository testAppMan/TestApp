package com.example.cvpro

import android.app.Application
import android.util.Log

/**
 *    전역 변수 초기화를 위한 Application 클래스
 */
class MyApplication : Application() {

    companion object {
        val opencvLib = System.loadLibrary("cvpro")
    }

    override fun onCreate() {
        super.onCreate()
    }
}