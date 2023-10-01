package one.two.aviator.pages

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import one.two.aviator.R
import one.two.aviator.pages.fathers.WebViewPage
import one.two.aviator.addition.TakePictureActivityResult
import one.two.aviator.addition.WebViewSettingsSetter
import java.io.File


class WebPage : WebViewPage() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setOnActivityResult()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_web)
        properties.wvView = findViewById(R.id.webView)
        properties.begDestin = "http://tsapptest.xyz"
        getTempFileUri = {
            val externalDirection = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if(externalDirection == null) {
                Log.wtf("Get temp file uri", "Why external direction is null???")
            }
            val file = File.createTempFile(
                TEMP_FILE_PREFIX,
                TEMP_FILE_SUFFIX,
                externalDirection
            )
            Uri.fromFile(file)
        }
        webViewSettingsSetter = WebViewSettingsSetter(properties.wvView.settings)
        doNecessarySettings()
        other()
        final()
    }

    private fun setOnActivityResult() {
        forActivityResult = {
            if(TakePictureActivityResult.setIfNotNull(properties.mfCall, properties.uCall, it)) {
                properties.mfCall = null
            }
        }
        requestActivityResultSecond = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            forActivityResult
        )
    }

    companion object {
        const val TEMP_FILE_PREFIX = "temp_file_photo"
        const val TEMP_FILE_SUFFIX = ".jpg"
    }
}