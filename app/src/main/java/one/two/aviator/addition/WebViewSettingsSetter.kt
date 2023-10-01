package one.two.aviator.addition

import android.util.Log
import android.webkit.WebSettings

class WebViewSettingsSetter(private val settings: WebSettings) {
    operator fun get(value: String) {
        settings.run {
            when (value) {
                ALLOWCONTENTACCESS -> {
                    if(!allowContentAccess) {
                        allowContentAccess = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$ALLOWCONTENTACCESS is true.")
                    }
                }
                ALLOWFILEACCESS -> {
                    if(!allowFileAccess) {
                        allowFileAccess = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$ALLOWFILEACCESS is true.")
                    }
                }
                JAVASCRIPTCANOPENWINDOWSAUTOMATICALLY -> {
                    if(!javaScriptCanOpenWindowsAutomatically) {
                        javaScriptCanOpenWindowsAutomatically = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$JAVASCRIPTCANOPENWINDOWSAUTOMATICALLY is true.")
                    }
                }
                ALLOWFILEACCESSFROMFILEURLS -> {
                    if(!allowFileAccessFromFileURLs) {
                        allowFileAccessFromFileURLs = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$ALLOWFILEACCESSFROMFILEURLS is true.")
                    }
                }
                DOMSTORAGEENABLED -> {
                    if(!domStorageEnabled) {
                        domStorageEnabled = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$DOMSTORAGEENABLED is true.")
                    }
                }
                JAVASCRIPTENABLED -> {
                    if(!javaScriptEnabled) {
                        javaScriptEnabled = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$JAVASCRIPTENABLED is true.")
                    }
                }
                DATABASEENABLED -> {
                    if(!databaseEnabled) {
                        databaseEnabled = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$DATABASEENABLED is true.")
                    }
                }
                ALLOWUNIVERSALACCESSFROMFILEURLS -> {
                    if(!allowUniversalAccessFromFileURLs) {
                        allowUniversalAccessFromFileURLs = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$ALLOWUNIVERSALACCESSFROMFILEURLS is true.")
                    }
                }
                USEWIDEVIEWPORT -> {
                    if(!useWideViewPort) {
                        useWideViewPort = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$USEWIDEVIEWPORT is true.")
                    }
                }
                LOADWITHOVERVIEWMODE -> {
                    if(!loadWithOverviewMode) {
                        loadWithOverviewMode = true
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$LOADWITHOVERVIEWMODE is true.")
                    }
                }
                else -> {
                    Log.w("WebViewSettingsSetter", "Wrong key ${value}.")
                }
            }
        }
    }

    operator fun set(key: String, value: Int) {
        settings.run {
            when(key) {
                MIXEDCONTENTMODE -> {
                    if(mixedContentMode != WebSettings.MIXED_CONTENT_ALWAYS_ALLOW) {
                        mixedContentMode = value
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$key is ${WebSettings.MIXED_CONTENT_ALWAYS_ALLOW}.")
                    }
                }
                CACHEMODE -> {
                    if(cacheMode != WebSettings.LOAD_DEFAULT) {
                        cacheMode = value
                    }
                    else {
                        Log.w("WebViewSettingsSetter", "$key is ${WebSettings.LOAD_DEFAULT}.")
                    }
                }
                else  -> {
                    Log.w("WebViewSettingsSetter", "$key is wrong.")
                }
            }
        }
    }

    operator fun set(key: String, value: String) {
        settings.run {
            when(key) {
                USERAGENTSTRING -> {
                    if(userAgentString.contains("; wv")) {
                        userAgentString = value
                    }

                    else {
                        Log.w("WebViewSettingsSetter", "$key is replaced.")
                    }
                }

                else -> {
                    Log.w("WebViewSettingsSetter", "$key is wrong.")
                }
            }
        }
    }

    companion object {
        const val ALLOWCONTENTACCESS = "allowContentAccess"
        const val ALLOWFILEACCESS = "allowFileAccess"
        const val JAVASCRIPTCANOPENWINDOWSAUTOMATICALLY = "javaScriptCanOpenWindowsAutomatically"
        const val ALLOWFILEACCESSFROMFILEURLS = "allowFileAccessFromFileURLs"
        const val MIXEDCONTENTMODE = "mixedContentMode"
        const val CACHEMODE = "cacheMode"
        const val USERAGENTSTRING = "userAgentString"
        const val DOMSTORAGEENABLED = "domStorageEnabled"
        const val JAVASCRIPTENABLED = "javaScriptEnabled"
        const val DATABASEENABLED = "databaseEnabled"
        const val ALLOWUNIVERSALACCESSFROMFILEURLS = "allowUniversalAccessFromFileURLs"
        const val USEWIDEVIEWPORT = "useWideViewPort"
        const val LOADWITHOVERVIEWMODE = "loadWithOverviewMode"
    }
}