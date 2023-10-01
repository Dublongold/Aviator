package one.two.aviator.addition

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebView

class WebViewActivityProperties {
    private var pwvView: WebView? = null
    var wvView: WebView
        get() = pwvView!!
        set(value) {
            pwvView = value
        }

    var mfCall: ValueCallback<Array<Uri>>? = null
    var uCall: Uri? = null
    var begDestin: String? = null
}