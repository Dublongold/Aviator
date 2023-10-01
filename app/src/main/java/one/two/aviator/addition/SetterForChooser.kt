package one.two.aviator.addition

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher

object SetterForChooser {
    val takeOldAndChooser: (Intent) -> Pair<Intent, Intent> by lazy {
        {
            val old = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)

            old.type = receiveOldType(false)
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(it))
            if(chooserIntent.action != Intent.ACTION_CHOOSER) {
                val newType = receiveOldType(true)
            }

            Log.i("Take old and chooser", "Created with successful.")
            old to chooserIntent
        }
    }
    private fun receiveOldType(isDestroyed: Boolean): String {
        return if(!isDestroyed) {
            "*/*"
        }
        else {
            "image/jpeg"
        }
    }
    fun set(chooser: Intent, old: Intent, requestActivity: ActivityResultLauncher<Intent>) {
        chooser.putExtra(Intent.EXTRA_INTENT, old)
        requestActivity.launch(chooser)
    }
}