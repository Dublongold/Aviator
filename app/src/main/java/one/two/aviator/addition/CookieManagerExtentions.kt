package one.two.aviator.addition

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView


fun CookieManager.setItRight(wvView: WebView) {
    if(!acceptCookie()) {
        setAcceptCookie(true)
    }
    else {
        Log.w("Cookie manager", "Accept cookie is already true.")
    }
    if(acceptThirdPartyCookies(wvView)) {
        setAcceptThirdPartyCookies(wvView, true)
    }
    else {
        Log.w("Cookie manager", "Accept third party cookies is already true.")
    }
}