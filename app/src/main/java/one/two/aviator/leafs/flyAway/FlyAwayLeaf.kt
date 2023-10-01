package one.two.aviator.leafs.flyAway

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import one.two.aviator.R
import one.two.aviator.leafs.ModifiedLeaf

class FlyAwayLeaf: ModifiedLeaf() {
    var win: Long = 0
    var multiplier = 0.0
    var additionalMultiplier = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.doInflateWithoutAttaching(R.layout.leaf_fly_away, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
            findViewById<AppCompatButton>(R.id.playAgain).setOnClickListener { leaveLeaf() }
            findViewById<TextView>(R.id.winMultiply).text =
                getString(R.string.multiplier, multiplier)
            findViewById<TextView>(R.id.additionalMultiply).text =
                getString(R.string.additional_multiplier, additionalMultiplier)
            findViewById<TextView>(R.id.winInCoins).text =
                getString(R.string.win, win)
        }
    }
}