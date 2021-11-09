package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * A [Cubit] is similar to a [Bloc] but has no notion of events,
 * instead relying on methods to [emit] [State]s and [SideEffect]s.
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
 * @param State The type of state this emits
 * @param SideEffect The type of side-effect this can emit
 * @see Bloc
 */
abstract class Cubit<State, SideEffect>(initial: State) :
    BlocBase<State, SideEffect>(initial), Emitter<State, SideEffect> {
    override suspend fun emit(state: State) {
        mutableChangeFlow.emit(Change(this.state, state))
    }

    override suspend fun emitEach(states: Flow<State>) {
        states.onEach { emit(it) }.launchIn(blocScope)
    }

    override suspend fun emitSideEffect(sideEffect: SideEffect) {
        mutableSideEffectFlow.emit(sideEffect)
    }

    override suspend fun emitSideEffects(sideEffects: Flow<SideEffect>) {
        sideEffects.onEach { emitSideEffect(it) }.launchIn(blocScope)
    }
}
