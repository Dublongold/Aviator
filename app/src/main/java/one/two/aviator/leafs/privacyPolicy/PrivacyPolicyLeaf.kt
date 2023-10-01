package one.two.aviator.leafs.privacyPolicy

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.allViews
import one.two.aviator.R
import one.two.aviator.leafs.ModifiedLeaf
import one.two.aviator.leafs.appMenu.AppMenuLeaf

class PrivacyPolicyLeaf: ModifiedLeaf() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.doInflateWithoutAttaching(R.layout.leaf_privacy_policy, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageButton>(R.id.burger).setOnClickListener {
            leaveLeaf(leaveTo = AppMenuLeaf::class.java)
        }
        (view.allViews.filter {
            it is TextView
        }.lastOrNull() as? TextView)?.movementMethod = ScrollingMovementMethod.getInstance()
    }
}