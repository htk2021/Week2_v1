package com.example.week2_v1

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "5774af0a6355455eadad8973f09b45b6")
    }
}