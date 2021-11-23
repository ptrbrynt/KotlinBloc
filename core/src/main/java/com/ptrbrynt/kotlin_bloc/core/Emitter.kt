package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.flow.Flow

/**
 * Interface which can be implemented on any object which can [emit] a [State] or a [SideEffect]
 */
interface Emitter<State> {
    /**
     * Emit a new [State]
     */
    suspend fun emit(state: State)

    /**
     * [emit] each [State] which is emitted by the [states] [Flow].
     */
    suspend fun emitEach(states: Flow<State>)
}
