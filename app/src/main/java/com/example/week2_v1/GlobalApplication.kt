package com.example.week2_v1

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    companion object{
        var appContext: Context? = null
        var loggedInUser: String? = null
        var v_url: String? = "https://late-paws-nail.loca.lt"
    }
    override fun onCreate() {
        super.onCreate()
        appContext=this
        KakaoSdk.init(this, "5774af0a6355455eadad8973f09b45b6")
    }
}