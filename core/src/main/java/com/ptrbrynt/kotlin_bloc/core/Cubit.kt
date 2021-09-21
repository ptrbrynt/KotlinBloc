package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

/**
 * A [Cubit] is similar to a [Bloc] but has no notion of events,
 * instead relying on methods to [emit] [State]s.
 *
 * Every [Cubit] requires an initial state, which will be the state
 * of the [Cubit] before [emit] has been called.
 *
 * ```kotlin
 * class CounterCubit : Cubit<Int>(0) {
 *   fun increment() = emit(state + 1)
 *   fun decrement() = emit(state - 1)
 * }
 * ```
 *
 * @param initial The initial [State]
 * @see Bloc
 */
@FlowPreview
abstract class Cubit<State>(initial: State) : BlocBase<State>(initial) {
    /**
     * Causes the [Cubit] to emit a new [state].
     */
    protected fun emit(state: State) = scope.launch {
        mutableStateFlow.emit(state)
    }
}
