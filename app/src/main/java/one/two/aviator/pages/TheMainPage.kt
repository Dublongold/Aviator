package one.two.aviator.pages

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import one.two.aviator.pages.fathers.MainPage
import one.two.aviator.leafs.ModifiedLeaf
import one.two.aviator.leafs.loading.LoadingLeaf

class TheMainPage : MainPage() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoadingLeaf().openLeaf(leafManager = supportFragmentManager, addToBackStack = false)
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (supportFragmentManager.fragments.lastOrNull() as? ModifiedLeaf)?.leaveLeaf()
            }
        })
    }
}