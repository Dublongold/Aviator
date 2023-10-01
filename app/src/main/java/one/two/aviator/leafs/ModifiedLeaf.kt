package one.two.aviator.leafs

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import one.two.aviator.R
import one.two.aviator.dao.UserData
import kotlin.coroutines.resume

abstract class ModifiedLeaf: Fragment() {

    val Int.dp: Int
        get() = (resources.displayMetrics.density * this).toInt()
    protected val Float.dp: Float
        get() = resources.displayMetrics.density * this
    protected val Double.dp: Double
        get() = resources.displayMetrics.density * this

    protected fun LayoutInflater.doInflateWithoutAttaching(layoutId: Int, container: ViewGroup?): View? {
        return inflate(layoutId, container, false)
    }

    fun openLeaf(
        leaf: ModifiedLeaf? = null,
        leafManager: FragmentManager? = null,
        addToBackStack: Boolean = true
    ) {
        if(addToBackStack) {
            BACK_STACK.add(leaf ?: this)
        }
        (leafManager ?: parentFragmentManager).beginTransaction()
            .replace(R.id.leafsInPages, leaf ?: this)
            .commitAllowingStateLoss()
    }

    fun openLeafAsDialog(
        leaf: ModifiedLeaf? = null,
        leafManager: FragmentManager? = null,
        addToBackStack: Boolean = true
    ) {
        if(addToBackStack) {
            BACK_STACK.add(leaf ?: this)
        }
        (leafManager ?: parentFragmentManager).beginTransaction()
            .add(R.id.leafsInPages, leaf ?: this)
            .commitAllowingStateLoss()
    }

    fun<T> leaveLeaf(leafManager: FragmentManager? = null, leaveTo: Class<T>, inclusive: Boolean = false) {
        var lastTook = BACK_STACK.removeLast()
        var last = BACK_STACK.last()
        val needDetach = mutableListOf<ModifiedLeaf>()
        val check = {
            if(inclusive) {
                lastTook::class.java != leaveTo
            }
            else {

                last::class.java != leaveTo
            }
        }
        needDetach.add(lastTook)
        while(check()) {
            lastTook = BACK_STACK.removeLast()
            last = BACK_STACK.last()
            needDetach.add(lastTook)
        }
        leaveLeafSameCode(leafManager, needDetach)
    }

    fun leaveLeaf(leafManager: FragmentManager? = null) {
        val last = BACK_STACK.removeLast()
        leaveLeafSameCode(leafManager, listOf(last))
    }

    protected fun getAvatarAsBitmapDrawable(): BitmapDrawable? {
        return getUserData().userAvatar?.let {
            if(it.all {c -> c.isDigit() || c == ',' || c == '-'}) {
                val byteArray = it.split(",").map { e -> e.toByte() }
                    .toByteArray()
                BitmapDrawable(
                    resources,
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                )
            }
            else {
                Log.e("Error", it.toSet().joinToString(", "))
                null
            }
        }
    }

    private fun leaveLeafSameCode(leafManager: FragmentManager? = null, needDetach: List<Fragment>) {
        if(BACK_STACK.isNotEmpty()) {
            if((leafManager ?: parentFragmentManager).fragments.size > 1) {
                (leafManager ?: parentFragmentManager).beginTransaction()
                    .apply {
                        needDetach.forEach {
                            detach(it)
                        }
                    }
                    .commitAllowingStateLoss()
            }
            else {
                (leafManager ?: parentFragmentManager).beginTransaction()
                    .replace(R.id.leafsInPages, BACK_STACK.last())
                    .commitAllowingStateLoss()
            }
        }
        else {
            activity?.finish()
        }
    }

    protected fun getUserData(): UserData {
        val default = getSharedPreferences(SHARED_PREFERENCES_NAME_DEFAULT, MODE_PRIVATE)
        val photo = getSharedPreferences(SHARED_PREFERENCES_FOR_AVATAR, MODE_PRIVATE)

        val coins = default.getLong(KEY_COINS, 5000)

        return UserData(
            default.getString(KEY_USERNAME, null),
            photo.getString(KEY_USER_AVATAR, null),
            if(coins > 10) coins else 5000
        )
    }

    protected fun<T> watch(stateFlow: StateFlow<T>, collect: (T) -> Unit): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stateFlow.collect (collect)
            }
        }
    }

    protected suspend fun Lifecycle.repeatOnLifecycle(
        state: Lifecycle.State,
        block: suspend CoroutineScope.() -> Unit
    ) {
        require(state !== Lifecycle.State.INITIALIZED) {
            "repeatOnLifecycle cannot start work with the INITIALIZED lifecycle state."
        }

        if (currentState === Lifecycle.State.DESTROYED) {
            return
        }

        coroutineScope {
            withContext(Dispatchers.Main.immediate) {
                if (currentState === Lifecycle.State.DESTROYED) return@withContext

                var launchedJob: Job? = null

                var observer: LifecycleEventObserver? = null
                try {
                    suspendCancellableCoroutine<Unit> { cont ->
                        val startWorkEvent = Lifecycle.Event.upTo(state)
                        val cancelWorkEvent = Lifecycle.Event.downFrom(state)
                        val mutex = Mutex()
                        observer = LifecycleEventObserver { _, event ->
                            if (event == startWorkEvent) {
                                launchedJob = this@coroutineScope.launch {
                                    mutex.withLock {
                                        coroutineScope {
                                            block()
                                        }
                                    }
                                }
                                return@LifecycleEventObserver
                            }
                            if (event == cancelWorkEvent) {
                                launchedJob?.cancel()
                                launchedJob = null
                            }
                            if (event == Lifecycle.Event.ON_DESTROY) {
                                cont.resume(Unit)
                            }
                        }
                        this@repeatOnLifecycle.addObserver(observer as LifecycleEventObserver)
                    }
                } finally {
                    launchedJob?.cancel()
                    observer?.let {
                        this@repeatOnLifecycle.removeObserver(it)
                    }
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    protected fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
        return requireActivity().getSharedPreferences(name, mode)
    }

    companion object {
        val BACK_STACK = mutableListOf<ModifiedLeaf>()
        const val MODE_PRIVATE = 0
        const val MODE_APPEND = 32768
        const val MODE_ENABLE_WRITE_AHEAD_LOGGING = 8
        const val MODE_NO_LOCALIZED_COLLATORS = 16
        const val SHARED_PREFERENCES_NAME_DEFAULT = "user_data"
        const val SHARED_PREFERENCES_FOR_AVATAR = "photo_data"
        const val KEY_USERNAME = "username"
        const val KEY_USER_AVATAR = "user_image"
        const val KEY_COINS = "coins"
    }
}