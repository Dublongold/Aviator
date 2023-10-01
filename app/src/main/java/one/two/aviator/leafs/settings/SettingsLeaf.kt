package one.two.aviator.leafs.settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import one.two.aviator.R
import one.two.aviator.leafs.ModifiedLeaf
import one.two.aviator.leafs.privacyPolicy.PrivacyPolicyLeaf
import java.io.ByteArrayOutputStream


class SettingsLeaf: ModifiedLeaf() {

    private lateinit var avatarImage: ShapeableImageView

    private val getContentLauncher = registerForActivityResult<String, Uri?>(
        ActivityResultContracts.GetContent()
    ) { result ->
        if(result != null) {
            CoroutineScope(Dispatchers.IO).launch {
                if (context != null) {
                    Log.i("Get", "Part 1")
                    val resolver = context?.contentResolver
                    if (resolver != null) {
                        Log.i("Get", "Part 2")
                        resolver.openInputStream(result)?.let {
                            var ogBitmap = BitmapFactory.decodeStream(it)

                            var widthOffset = 0
                            var heightOffset = 0
                            var widthOrHeight = if(ogBitmap.width > ogBitmap.height) {
                                widthOffset = (ogBitmap.width - ogBitmap.height) / 2
                                ogBitmap.height
                            } else {
                                heightOffset = (ogBitmap.height - ogBitmap.width) / 2
                                ogBitmap.width
                            }

                            Log.i("Get", """widthOffset: $widthOffset
                                |heightOffset: $heightOffset
                                |width: ${ogBitmap.width}
                                |height: ${ogBitmap.height}
                                |widthOrHeight: $widthOrHeight
                            """.trimMargin())

                            ogBitmap = Bitmap.createBitmap(ogBitmap, widthOffset, heightOffset, widthOrHeight, widthOrHeight)

                            if(widthOrHeight > 400) {
                                widthOrHeight = 400
                            }

                            val scBitmap = Bitmap.createScaledBitmap(ogBitmap, widthOrHeight,
                                widthOrHeight, true)

                            val resultByteArray = ByteArrayOutputStream()

                            scBitmap.compress(Bitmap.CompressFormat.JPEG, 50, resultByteArray)

                            setAvatar(resultByteArray.toByteArray())
                            view?.handler?.post {
                                setAvatarImage()
                            }
                        }
                    }
                }
            }
        }
        else {
            avatarImage.isEnabled = true
        }
    }

    private val editMode = MutableStateFlow(false)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.leaf_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        avatarImage = view.findViewById(R.id.userAvatar)
        avatarImage.apply {
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(16.dp.toFloat())
                .build()
        }
        //
        view.findViewById<ImageButton>(R.id.burger).setOnClickListener {
            if(avatarImage.isEnabled) {
                leaveLeaf()
            }
        }
        val userData = getUserData()
        val editText = view.findViewById<EditText>(R.id.editText)

        userData.username?.let {
            editText.setText(it)
        }
        setAvatarImage()
        avatarImage.setOnClickListener {
            avatarImage.isEnabled = false
            getContentLauncher.launch("image/jpeg")
        }
        view.findViewById<ImageButton>(R.id.editButton).setOnClickListener {
            it as ImageButton
            if(editMode.value) {
                it.setImageResource(R.drawable.edit)
                editText.isEnabled = false
                setText(editText.text.toString().trimEnd())
            }
            else {
                it.setImageResource(R.drawable.apply)
                editText.isEnabled = true
            }
            editMode.value = !editMode.value
        }
        view.findViewById<TextView>(R.id.privacyPolicyButton).setOnClickListener {
            openLeaf(PrivacyPolicyLeaf())
        }
    }

    private fun setAvatarImage() {
        getAvatarAsBitmapDrawable()?.let {
            avatarImage.setImageDrawable(it)
            avatarImage.isEnabled = true
        }
    }

    private fun setText(value: String) {
        getSharedPreferences(SHARED_PREFERENCES_NAME_DEFAULT, MODE_PRIVATE).edit()
            .putString(KEY_USERNAME, value).apply()
    }

    private fun setAvatar(value: ByteArray) {
        val str = value.joinToString(",")
        getSharedPreferences(SHARED_PREFERENCES_FOR_AVATAR, MODE_PRIVATE)
            .edit().putString(KEY_USER_AVATAR, str).apply()
    }
}