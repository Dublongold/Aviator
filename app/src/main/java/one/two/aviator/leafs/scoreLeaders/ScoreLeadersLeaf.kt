package one.two.aviator.leafs.scoreLeaders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.allViews
import one.two.aviator.R
import one.two.aviator.dao.ScoreLeader
import one.two.aviator.leafs.ModifiedLeaf
import kotlin.random.Random

class ScoreLeadersLeaf: ModifiedLeaf() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.doInflateWithoutAttaching(R.layout.leaf_score_leaders, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scoreLeaders = mutableListOf<ScoreLeader>()
        val usersIds = listOf(
            R.string.user_1, R.string.user_2, R.string.user_3,
            R.string.user_4, R.string.user_5, R.string.user_6,
            R.string.user_7, R.string.user_8, R.string.user_9,
            R.string.user_10)
        for(i in 0..9) {
            scoreLeaders.add(
                ScoreLeader(
                    username = getString(usersIds[i], i + 1),
                    avatar = R.drawable.avatar_01 + i,
                    coins = Random.nextLong(10001, 11000) - Random.nextLong(150, 200) * i
                )
            )
        }
        val userData = getUserData()
        scoreLeaders.add(
            ScoreLeader(
                username = userData.username ?: getString(R.string.default_username),
                avatar = null,
                coins = userData.coins
            )
        )
        scoreLeaders.sortByDescending {
            it.coins
        }
        view.allViews.run {
            filter { it.tag == "user_avatar" }.map { it as ImageView }
                .forEachIndexed { index, imageView ->
                val scoreLeader = scoreLeaders[index]
                if(scoreLeader.avatar != null) {
                    imageView.setImageResource(scoreLeader.avatar)
                }
                else {
                    imageView.setImageDrawable(getAvatarAsBitmapDrawable())
                }
            }
        }
        getTextViewsByTag(view, "username").forEachIndexed { index, textView ->
            textView.text = getString(R.string.user, index+1, scoreLeaders[index].username)
        }
        getTextViewsByTag(view, "user_coins").forEachIndexed { index, textView ->
            textView.text = scoreLeaders[index].coins.toString()
        }

        view.findViewById<ImageButton>(R.id.burger).setOnClickListener {
            leaveLeaf()
        }
    }

    private fun getTextViewsByTag(view: View, tag: String) = view.allViews.filter {
        it.tag == tag && it is TextView
    }.map {
        it as TextView
    }
}