package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive

/**
 * This class contains common functionality for [Bloc] and [Cubit].
 *
 * @param initial The initial [State]
 */
abstract class BlocBase<State>(initial: State) {

    protected val scope = CoroutineScope(Dispatchers.Unconfined)

    protected val mutableStateFlow = MutableSharedFlow<State>()

    /**
     * The current [State] [Flow]
     */
    val stateFlow: Flow<State> = mutableStateFlow.onEach { newState ->
        onChange(Change(state = this.state, newState = newState))
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
        this.state = change.newState
    }

    /**
     * Should be called when the `bloc` or `cubit` is no longer needed.
     *
     * Can be overridden to implement additional cleanup functionality.
     *
     * **Note: `super.close()` should always be called first.**
     *
     * ```kotlin
     * override fun close() {
     *   // Always call super.close()
     *   super.close()
     *
     *   // Custom logic goes here
     * }
     * ```
     */
    open fun close() {
        if (scope.isActive) {
            scope.cancel()
        }
    }
}
