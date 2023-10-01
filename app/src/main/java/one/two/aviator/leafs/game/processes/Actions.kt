package one.two.aviator.leafs.game.processes

import one.two.aviator.leafs.game.GameLeaf
import kotlin.math.sin

class Actions {
    fun GameLeaf.setPlaneDestinations(rotation: Float) {
        plane.layoutParams.width = (126.dp - 91 * sin(rotation)).toInt()
        plane.layoutParams.height = (35.dp + 91 * sin(rotation)).toInt()
    }
    fun GameLeaf.checkState() = gameProcesses.properties.startState.value?.state
}