package one.two.aviator.pages.fathers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import one.two.aviator.addition.SetterForChooser
import one.two.aviator.addition.WebViewActivityProperties
import one.two.aviator.addition.WebViewSettingsSetter
import one.two.aviator.addition.setItRight


abstract class WebViewPage : AppCompatActivity() {
    protected val properties = WebViewActivityProperties()
    protected lateinit var forActivityResult: (ActivityResult) -> Unit
    protected lateinit var webViewSettingsSetter: WebViewSettingsSetter

    @SuppressLint("SetJavaScriptEnabled")
    protected fun doNecessarySettings() {
        webViewSettingsSetter[WebViewSettingsSetter.ALLOWCONTENTACCESS]
        webViewSettingsSetter[WebViewSettingsSetter.ALLOWFILEACCESS]
        webViewSettingsSetter[WebViewSettingsSetter.JAVASCRIPTCANOPENWINDOWSAUTOMATICALLY]
        webViewSettingsSetter[WebViewSettingsSetter.ALLOWFILEACCESSFROMFILEURLS]
        properties.wvView.settings.run {
            if (allowContentAccess && allowFileAccess && javaScriptCanOpenWindowsAutomatically
                && allowFileAccessFromFileURLs) {
                webViewSettingsSetter[WebViewSettingsSetter.MIXEDCONTENTMODE] = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                webViewSettingsSetter[WebViewSettingsSetter.CACHEMODE] = WebSettings.LOAD_DEFAULT
                webViewSettingsSetter[WebViewSettingsSetter.USERAGENTSTRING] =
                    properties.wvView.settings.userAgentString.replace("; wv", "")
                webViewSettingsSetter[WebViewSettingsSetter.DOMSTORAGEENABLED]
                webViewSettingsSetter[WebViewSettingsSetter.JAVASCRIPTENABLED]
                webViewSettingsSetter[WebViewSettingsSetter.DATABASEENABLED]
                if(databaseEnabled && domStorageEnabled
                    && mixedContentMode == WebSettings.MIXED_CONTENT_ALWAYS_ALLOW) {
                    webViewSettingsSetter[WebViewSettingsSetter.ALLOWUNIVERSALACCESSFROMFILEURLS]
                    webViewSettingsSetter[WebViewSettingsSetter.USEWIDEVIEWPORT]
                    webViewSettingsSetter[WebViewSettingsSetter.LOADWITHOVERVIEWMODE]
                }
            }
        }
    }

    protected fun other() {
        CookieManager.getInstance().setItRight(properties.wvView)

        properties.wvView.webChromeClient = CustomWebChromeClient()
        properties.wvView.webViewClient = CustomWebViewClient()
    }
    protected fun final() {
        properties.wvView.loadUrl(properties.begDestin!!)
    }

    inner class CustomWebChromeClient(): WebChromeClient() {
        private val camera = Manifest.permission.CAMERA
        private var lastGetVisitedHistoryIsOK = false
        private var progress = 0
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            doOnShowFileChooser(filePathCallback)
            return true
        }
        private fun doOnShowFileChooser(filePathCallback: ValueCallback<Array<Uri>>) {
            requestPermissionLauncher.launch()
            filePathCallback.setLikeThis()
        }

        private fun ValueCallback<Array<Uri>>.setLikeThis() {
            if(properties.mfCall == null) {
                Log.i("Set like this", "It been null, but not now...")
            }
            properties.mfCall = this
        }
        private fun ActivityResultLauncher<String>.launch() {
            Log.i("Launch from client", "Launched.")
            launch(camera)
        }

        override fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
            super.getVisitedHistory(callback)
            val tag = "Get visited history"
            if(callback != null) {
                Log.i(tag, "Callback is not null. It is OK.")
                lastGetVisitedHistoryIsOK = true
            }
            else {
                Log.i(tag, "Callback is null. It is NOT OK.")
                lastGetVisitedHistoryIsOK = false
            }
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            val tag = "Progress changed"
            progress = newProgress
            when(newProgress) {
                in 0..25 -> {
                    Log.i(tag, "Progress started.")
                }
                in 26..50 -> {
                    Log.i(tag, "Progress above start.")
                }
                in 51..75 -> {
                    Log.i(tag, "Progress almost reached the end.")
                }
                in 76..99 -> {
                    Log.i(tag, "There's a little time left until the end.")
                }
                else -> {
                    Log.i(tag, "Progress ended.")
                }
            }
        }
    }

    inner class CustomWebViewClient : WebViewClient() {
        private var lastLoadState = LastLoadState.NONE

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            val uri = request.url.toString()
            return if (uri.contains("/")) {
                Log.i("Uri", "Try load url ${uri}. It contains \"/\".")
                checkIfContainsHttp(uri)
            } else {
                lastLoadState = LastLoadState.BAD
                Log.i("Uri", "Try load url ${uri}. It NOT contains \"/\"...")
                true
            }
        }

        private fun checkIfContainsHttp(uri: String): Boolean {
            return if (uri.contains("http")) {
                Log.i("Uri", "Url contains \"http\". Checking is over.")
                lastLoadState = LastLoadState.GOOD
                lastLoadState == LastLoadState.BAD
            } else {
                somethingWrong(uri)
            }
        }
        private fun somethingWrong(uri: String): Boolean {
            Log.i("Uri", "Url contains not \"http\". Weird...")
            val tempIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(tempIntent)
            lastLoadState = LastLoadState.SOMETHING_WRONG
            return true
        }
        private var fontResubmittedInPast = false
        override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
            super.onFormResubmission(view, dontResend, resend)
            val tag = "On form resubmission"
            if(fontResubmittedInPast) {
                Log.i(tag, "Again...")
            }
            else {
                fontResubmittedInPast = true
                Log.i(tag, "It happend.")
            }
        }
        private var scales = 0f to 0f
        override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
            super.onScaleChanged(view, oldScale, newScale)
            val tag = "Scale changed"
            Log.i(tag,
            when {
                oldScale > newScale -> {
                    "Old scale is bigger. $oldScale to $newScale"
                }
                oldScale == newScale -> {
                    "Scales are same. $oldScale"
                }
                newScale > oldScale -> {
                    "New scale is bigger. $oldScale to $newScale"
                }
                else -> {
                    "Something wrong..."
                }
            })
            scales = oldScale to newScale
        }
    }

    enum class LastLoadState {
        NONE,
        GOOD,
        BAD,
        SOMETHING_WRONG
    }

    protected lateinit var getTempFileUri: () -> Uri

    lateinit var requestActivityResultSecond: ActivityResultLauncher<Intent>
    @Suppress("KotlinConstantConditions")
    val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var photoFile: Uri? = null

            photoFile = photoFile.setPhotoFile()
            photoFile.let {
                if(it == null) {
                    Log.w(
                        "Request permission",
                        "Try put in extra and set uCall null file uri."
                    )
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                properties.uCall = it
            }
            val (old, chooser) = SetterForChooser.takeOldAndChooser(takePictureIntent)
            SetterForChooser.set(chooser, old, requestActivityResultSecond)
        }

    @Suppress("UnusedReceiverParameter")
    private fun Uri?.setPhotoFile(): Uri? {
        val result = try {
            getTempFileUri()
        }
        catch (exception: Exception) {
            Log.e(
                "Temp photo file",
                "Something happened while creating temp photo file...",
                exception
            )
            null
        }
        if(result == null) {
            Log.w("Temp photo file", "Temp photo file is null...")
        }
        return result
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        properties.wvView.saveState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 4 && properties.wvView.canGoBack()) {
            properties.wvView.goBack()
            return true
        }
        return false
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        properties.wvView.restoreState(savedInstanceState)
    }
}