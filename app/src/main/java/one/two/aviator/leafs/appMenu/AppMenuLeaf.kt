package one.two.aviator.leafs.appMenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import one.two.aviator.R
import one.two.aviator.leafs.ModifiedLeaf
import one.two.aviator.leafs.game.GameLeaf
import one.two.aviator.leafs.scoreLeaders.ScoreLeadersLeaf
import one.two.aviator.leafs.settings.SettingsLeaf

class AppMenuLeaf: ModifiedLeaf() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.doInflateWithoutAttaching(R.layout.leaf_app_menu, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<AppCompatButton>(R.id.gameButton).setOnClickListener {
            Log.i("App menu button", "Game.")
            openLeaf(GameLeaf())
        }
        view.findViewById<AppCompatButton>(R.id.scoreLeadersButton).setOnClickListener {
            Log.i("App menu button", "Score leaders.")
            openLeaf(ScoreLeadersLeaf())
        }
        view.findViewById<AppCompatButton>(R.id.settingsButton).setOnClickListener {
            Log.i("App menu button", "Settings.")
            openLeaf(SettingsLeaf())
        }
    }
}