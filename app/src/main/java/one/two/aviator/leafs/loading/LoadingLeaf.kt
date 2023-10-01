package one.two.aviator.leafs.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import one.two.aviator.R
import one.two.aviator.leafs.ModifiedLeaf
import one.two.aviator.leafs.appMenu.AppMenuLeaf


class LoadingLeaf: ModifiedLeaf() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.doInflateWithoutAttaching(R.layout.leaf_loading, container)
    }

    private lateinit var plane: ImageView

    private val layoutParams
        get() = plane.layoutParams as ConstraintLayout.LayoutParams

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        plane = view.findViewById(R.id.plane)
        CoroutineScope(Dispatchers.Main).launch {
            delay(250)
            move(0.25f)
            delay(250)
            move(0.5f)
            delay(250)
            move(0.75f)
            delay(250)
            move(1f)
            delay(250)
            openLeaf(AppMenuLeaf())
            /*
            requireActivity().run {
                startActivity(
                    Intent(
                        requireContext(),
                        WebPage::class.java
                        )
                )
                finish()
            }
             */
        }
    }

    private fun move(position: Float) {
        plane.layoutParams = layoutParams.apply {
            horizontalBias = position
        }
    }
}