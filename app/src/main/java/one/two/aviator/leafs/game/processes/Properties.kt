package one.two.aviator.leafs.game.processes

import kotlinx.coroutines.flow.MutableStateFlow
import one.two.aviator.leafs.game.GameLeaf

class Properties {
    val winMultiplier = MutableStateFlow(0.0)
    val autoPlayMultiplier = MutableStateFlow(0.0)
    val bet = MutableStateFlow(10)
    val coins = MutableStateFlow(0L)
    val gameType = MutableStateFlow(false)
    val startState: MutableStateFlow<GameLeaf.StartState?> = MutableStateFlow(null)
    val additionalMultiplier = MutableStateFlow(0.0)
    val planeRotation = MutableStateFlow(0f)
}