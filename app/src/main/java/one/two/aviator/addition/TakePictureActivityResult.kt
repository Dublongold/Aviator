package one.two.aviator.addition

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResult

object TakePictureActivityResult {
    fun setIfNotNull(
        mfCall: ValueCallback<Array<Uri>>?,
        uCall: Uri?,
        activityResult: ActivityResult
    ): Boolean {
        return if(mfCall != null) {
            val resultCode = activityResult.resultCode
            val data = activityResult.data
            if (resultCode == -1) {
                if (doIfDataExists(data, mfCall)) {
                    return true
                }
                else {
                    fun Uri.toArray() = arrayOf(this)
                    mfCall.onReceiveValue(uCall?.toArray())
                }
            } else {
                mfCall.onReceiveValue(null)
            }
            true
        }
        else {
            false
        }
    }
    private fun doIfDataExists(data: Intent?, mfCall: ValueCallback<Array<Uri>>): Boolean {
        return if(data != null && data.dataString != null) {
            val u = Uri.parse(data.dataString)
            mfCall.onReceiveValue(arrayOf(u))
            true
        }
        else {
            false
        }
    }
    private fun isNull(any: Any?) = any == null
}