package com.ptrbrynt.kotlin_bloc.core

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

abstract class Cubit<State>(initial: State) : BlocBase<State>(initial)
