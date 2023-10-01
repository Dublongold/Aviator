package one.two.aviator.pages.fathers

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import one.two.aviator.R
import one.two.aviator.leafs.ModifiedLeaf
import one.two.aviator.leafs.loading.LoadingLeaf

abstract class MainPage: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
        supportFragmentManager.addOnBackStackChangedListener {
            if(ModifiedLeaf.BACK_STACK[0] is LoadingLeaf) {
                ModifiedLeaf.BACK_STACK.removeFirst()
            }
            else {
                ModifiedLeaf.BACK_STACK.removeLast()
            }

        }
    }
}