package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * This class contains common functionality for [Bloc] and [Cubit].
 *
 * @param initial The initial [State]
 */
@Suppress("LeakingThis")
abstract class BlocBase<State>(initial: State) {

    init {
        Bloc.observer.onCreate(this)
    }

    protected val blocScope = CoroutineScope(Dispatchers.Unconfined)

    protected val mutableChangeFlow = MutableSharedFlow<Change<State>>()
        .apply {
            blocScope.launch {
                collect { onChange(it) }
            }
        }

    /**
     * The current [State] [Flow]
     */
    val stateFlow = mutableChangeFlow.map { it.newState }

    /**
     * Causes this to emit a new [state].
     */
    protected suspend fun emit(state: State) {
        mutableChangeFlow.emit(Change(this@BlocBase.state, state))
    }

    /**
     * The current [State]
     */
    var state: State = initial
        private set

    /**
     * Called when the [state] changes.
     *
     * [onChange] is called before the `state` of the `cubit` is updated.
     * [onChange] is a great place to add logging/analytics.
     *
     * **Note: `super.onChange` should always be called first.**
     *
     * ```kotlin
     * override fun onChange(change: Change<State>) {
     *   // Always call super.onChange with the current change
     *   super.onChange(change)
     *
     *   // Custom logic goes here
     * }
     * ```
     */
    open fun onChange(change: Change<State>) {
        Bloc.observer.onChange(this, change)
        this.state = change.newState
    }
}
