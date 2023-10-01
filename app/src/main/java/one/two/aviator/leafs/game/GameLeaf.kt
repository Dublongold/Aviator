package one.two.aviator.leafs.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.allViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import one.two.aviator.R
import one.two.aviator.leafs.ModifiedLeaf
import one.two.aviator.leafs.flyAway.FlyAwayLeaf
import kotlin.random.Random
import kotlin.random.nextInt

class GameLeaf: ModifiedLeaf() {
    val gameProcesses = GameProcesses()
    lateinit var plane: ImageView
    private var playJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.doInflateWithoutAttaching(R.layout.leaf_game, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameProcesses.properties.startState.value = StartState()
        gameProcesses.properties.coins.value = getUserData().coins
        view.run {
            plane = findViewById(R.id.plane)
            setWatches()
//            watch(gameProcesses.properties.planeRotation) {
//                plane.rotation = it
//                setPlaneDestinations(it)
//            }
            val burger = findViewById<ImageButton>(R.id.burger)
            burger.setOnClickListener {
                leaveLeaf()
            }
            val placeABet = findViewById<AppCompatButton>(R.id.placeABet)
            placeABet.setOnClickListener {
                switchPlayType()
            }
            val autoPlay = findViewById<AppCompatButton>(R.id.autoPlay)
            autoPlay.setOnClickListener {
                switchPlayType()
            }
            setArithmeticButtons(view)
            findViewById<AppCompatButton>(R.id.placeYourBet).setOnClickListener {
                gameProcesses.properties.startState.value?.start()
            }
        }
        setBets(view)
        setAutoPlayMultipliers(view)
//        CoroutineScope(Dispatchers.Main).launch {
//            while (true) {
//                delay(100)
//                gameProcesses.properties.planeRotation.value += 1f
//            }
//        }
    }

//    private fun setPlaneDestinations(rotation: Float) {
//        plane.layoutParams.width = (126.dp - 91 * sin(rotation)).toInt()
//        plane.layoutParams.height = (35.dp + 91 * sin(rotation)).toInt()
//    }

    private fun View.setWatches() {
        watch(gameProcesses.properties.coins) {
            findViewById<TextView>(R.id.coins).text = it.toString()
            saveCoins()
        }
        watch(gameProcesses.properties.winMultiplier) {
            findViewById<TextView>(R.id.gameWin).text = getString(R.string.multiplier, it)
            val layoutParams = (plane.layoutParams as ConstraintLayout.LayoutParams)

            val forCalculation = if(it > 10.0) 10.0 else it
            layoutParams.horizontalBias = (forCalculation / 10.0).toFloat()
            layoutParams.verticalBias = if(forCalculation == 0.0) {
                0.8f
            }
            else {
                (forCalculation * -0.027 + 0.8).toFloat()
            }
            plane.layoutParams = layoutParams
            plane.rotation = (forCalculation * -0.9 - 20).toFloat()
        }
        watch(gameProcesses.properties.bet) {
            findViewById<TextView>(R.id.currentBet).text = it.toString()
        }
        watch(gameProcesses.properties.autoPlayMultiplier) {
            findViewById<TextView>(R.id.currentMultiplier).text =
                getString(R.string.multiplier, it)
        }
        watch(gameProcesses.properties.additionalMultiplier) {
            findViewById<LinearLayout>(R.id.multipliers).allViews
                .filter { it is TextView }
                .forEachIndexed { index, textView ->
                    if(additionalMultiplies[index] ==
                        gameProcesses.properties.additionalMultiplier.value) {
                        textView.foreground = ResourcesCompat.getDrawable(resources,
                            R.drawable.picked_multiplier, null)
                    }
                    else {
                        textView.foreground = null
                    }
                }
        }
    }
    private fun setBets(view: View) {
        view.findViewById<AppCompatButton>(R.id.oneBet).setOnClickListener {
            gameProcesses.properties.bet.value = 1
        }
        view.findViewById<AppCompatButton>(R.id.fiveBet).setOnClickListener {
            gameProcesses.properties.bet.value = 5
        }
        view.findViewById<AppCompatButton>(R.id.tenBet).setOnClickListener {
            gameProcesses.properties.bet.value = 10
        }
        view.findViewById<AppCompatButton>(R.id.fiftyBet).setOnClickListener {
            gameProcesses.properties.bet.value = 50
        }
    }

    private fun setArithmeticButtons(view: View) {
        view.run {
            findViewById<ImageButton>(R.id.minusBet).setOnClickListener {
                gameProcesses.properties.bet.value.let {
                    if(it > 0) {
                        gameProcesses.properties.bet.value--
                    }
                }
            }
            findViewById<ImageButton>(R.id.minusBet).setOnLongClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    var times = 0
                    while(it.isPressed && gameProcesses.properties.bet.value > 0) {
                        gameProcesses.properties.bet.value--
                        if(times < 10) {
                            delay(200)
                            times++
                        }
                        else {
                            delay(100)
                        }
                    }
                }
                true
            }
            findViewById<ImageButton>(R.id.plusBet).setOnClickListener {
                gameProcesses.properties.bet.value++
            }
            findViewById<ImageButton>(R.id.plusBet).setOnLongClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    var times = 0
                    while(it.isPressed) {
                        gameProcesses.properties.bet.value++
                        if(times < 10) {
                            delay(200)
                            times++
                        }
                        else {
                            delay(100)
                        }
                    }
                }
                true
            }

            findViewById<ImageButton>(R.id.minusMultiplier).setOnClickListener {
                gameProcesses.properties.autoPlayMultiplier.value.let {
                    if(it > 0) {
                        gameProcesses.properties.autoPlayMultiplier.value -= 0.1
                    }
                }
            }
            findViewById<ImageButton>(R.id.minusMultiplier).setOnLongClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    var times = 0
                    while(it.isPressed && gameProcesses.properties.autoPlayMultiplier.value > 0) {
                        gameProcesses.properties.autoPlayMultiplier.value -= 0.1
                        if(times < 10) {
                            delay(200)
                            times++
                        }
                        else {
                            delay(100)
                        }
                    }
                }
                true
            }
            findViewById<ImageButton>(R.id.plusMultiplier).setOnClickListener {
                gameProcesses.properties.autoPlayMultiplier.value += 0.1
            }
            findViewById<ImageButton>(R.id.plusMultiplier).setOnLongClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    var times = 0
                    while(it.isPressed) {
                        gameProcesses.properties.autoPlayMultiplier.value += 0.1
                        if(times < 10) {
                            delay(200)
                            times++
                        }
                        else {
                            delay(100)
                        }
                    }
                }
                true
            }
        }
    }

    private fun setAutoPlayMultipliers(view: View) {
        view.findViewById<AppCompatButton>(R.id.reset).setOnClickListener {
            gameProcesses.properties.autoPlayMultiplier.value = 0.0
        }
        view.findViewById<AppCompatButton>(R.id.twoMultiplier).setOnClickListener {
            gameProcesses.properties.autoPlayMultiplier.value = 2.0
        }
        view.findViewById<AppCompatButton>(R.id.tenMultiplier).setOnClickListener {
            gameProcesses.properties.autoPlayMultiplier.value = 10.0
        }
    }

    private fun switchPlayType() {
        gameProcesses.properties.startState.value?.let {
            val result = if(!gameProcesses.properties.gameType.value) {
                it.setAutoPlay()
            }
            else {
                it.setPlaceYourBet()
            }
            if(result) {
                gameProcesses.properties.gameType.update { gameType ->  !gameType }
                view?.run {
                    setButtonActive(
                        findViewById(R.id.placeABet),
                        gameProcesses.properties.gameType.value
                    )
                    setButtonActive(
                        findViewById(R.id.autoPlay),
                        !gameProcesses.properties.gameType.value
                    )
                }
            }
        }
    }

    private fun setButtonActive(button: AppCompatButton, value: Boolean) {
        button.isEnabled = value
        button.setTextColor(resources.getColor(
            if(value) R.color.red else R.color.white,
            null
        ))
    }

    private fun changeTranslucent(set: Boolean) {
        view?.allViews?.filter{ it.tag == "needSetTranslucent" }?.forEach {
            if(set) {
                it.alpha = 0.4f
            }
            else {
                it.alpha = 1f
            }
        }
        view?.findViewById<LinearLayout>(R.id.options)?.allViews?.filter {
            it is AppCompatButton || it is ImageButton
        }?.forEach {
            it.isEnabled = !set
        }
    }

    private fun addWin() {
        gameProcesses.properties.coins.value +=
            (gameProcesses.properties.bet.value
                    * gameProcesses.properties.additionalMultiplier.value
                    * gameProcesses.properties.winMultiplier.value)
                .toLong()
    }

    inner class StartState {
        private var hiddenState = StartStates.PLACE_YOUR_BET
        val state
            get() = hiddenState

        private val button
            get() = view?.findViewById<AppCompatButton>(R.id.placeYourBet)

        fun setPlaceYourBet(): Boolean {
            return if(hiddenState == StartStates.AUTO_PLAY || hiddenState == StartStates.TAKE_OUT) {
                hiddenState = StartStates.PLACE_YOUR_BET
                button?.text = getString(R.string.place_your_bet)
                true
            }
            else {
                false
            }
        }

        fun setAutoPlay(): Boolean {
            return if(hiddenState == StartStates.PLACE_YOUR_BET || hiddenState == StartStates.STOP_AUTO_PLAY) {
                hiddenState = StartStates.AUTO_PLAY
                button?.text = getString(R.string.auto_play)
                true
            }
            else {
                false
            }
        }

        private fun setStopAutoPlay(): Boolean {
            hiddenState = StartStates.STOP_AUTO_PLAY
            button?.text = getString(R.string.stop_auto_play)
            return true
        }

        private fun setTakeOut(): Boolean {
            hiddenState = StartStates.TAKE_OUT
            button?.text = getString(R.string.take_out)
            return true
        }

        private fun decreaseCoins(): Boolean {
            return if(gameProcesses.properties.coins.value >= gameProcesses.properties.bet.value) {
                gameProcesses.properties.coins.value -= gameProcesses.properties.bet.value
                true
            }
            else {
                false
            }
        }

        fun start() {
            when(hiddenState) {
                StartStates.PLACE_YOUR_BET -> {
                    if(decreaseCoins()) {
                        placeYourBet()
                    }
                }
                StartStates.AUTO_PLAY -> {
                    autoPlay()
                }
                StartStates.STOP_AUTO_PLAY -> stopAutoPlay()
                StartStates.TAKE_OUT -> takeOut()
            }
        }

        private fun placeYourBet() {
            playJob = CoroutineScope(Dispatchers.Main).launch {
                button?.isEnabled = false
                changeTranslucent(true)
                setTakeOut()
                launch {
                    delay(500)
                    button?.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                }
                doTry()
                button?.apply {
                    isEnabled = true
                    alpha = 1f
                }
                changeTranslucent(false)
                setPlaceYourBet()
                openFlyAway(0.0)
                gameProcesses.properties.winMultiplier.value = 0.0
            }
        }

        private suspend fun doTry(additionalCheck: (() -> Boolean)? = null) {
            gameProcesses.properties.winMultiplier.value = 0.0
            gameProcesses.properties.additionalMultiplier.value = additionalMultiplies.random()
            while(true) {
                if(additionalCheck?.invoke() == false) {
                    addWin()
                    break
                }
                val currentNumber = Random.nextInt(1..200)
                val chance = (gameProcesses.properties.winMultiplier.value * 10).toInt()
                if((if(chance > 99) 99 else chance) >= currentNumber) {
                    delay(250)
                    break
                }
                else {
                    delay(150)
                    gameProcesses.properties.winMultiplier.value = (
                            gameProcesses.properties.winMultiplier.value * 10 + 1) / 10
                }
            }
        }

        private fun autoPlay() {
            if(gameProcesses.properties.autoPlayMultiplier.value == 0.0) return
            button?.isEnabled = false
            changeTranslucent(true)
            setStopAutoPlay()
            playJob = CoroutineScope(Dispatchers.Main).launch {
                launch {
                    delay(500)
                    button?.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                }
                while (true) {
                    if(decreaseCoins()) {
                        doTry {
                            gameProcesses.properties.winMultiplier.value <
                                    gameProcesses.properties.autoPlayMultiplier.value
                        }
                        Log.i("Cycle", "Do cycle")
                        delay(500)
                    }
                    else {
                        stopAutoPlay()
                    }
                }
            }
        }

        private fun takeOut() {
            playJob?.cancel()
            openFlyAway()
            addWin()
            changeTranslucent(false)
            setPlaceYourBet()
        }
        private fun stopAutoPlay() {
            playJob?.cancel()
            addWin()
            changeTranslucent(false)
            setAutoPlay()
        }
    }

    private fun saveCoins() {
        getSharedPreferences(SHARED_PREFERENCES_NAME_DEFAULT, MODE_PRIVATE)
            .edit().putLong(KEY_COINS, gameProcesses.properties.coins.value).apply()
    }

    private fun openFlyAway(multiplier: Double = gameProcesses.properties.winMultiplier.value) {
        FlyAwayLeaf().run {
            win = (gameProcesses.properties.bet.value
                    * gameProcesses.properties.additionalMultiplier.value
                    * multiplier)
                .toLong()
            this.multiplier = multiplier
            additionalMultiplier = gameProcesses.properties.additionalMultiplier.value
            openLeafAsDialog(leafManager = this@GameLeaf.parentFragmentManager)
        }
    }

    enum class StartStates {
        PLACE_YOUR_BET,
        AUTO_PLAY,
        STOP_AUTO_PLAY,
        TAKE_OUT,
    }
    companion object {
        val additionalMultiplies = listOf(
            0.7,
            2.4,
            3.5,
            0.9,
            2.2,
            1.8,
            0.4
        )
    }
}