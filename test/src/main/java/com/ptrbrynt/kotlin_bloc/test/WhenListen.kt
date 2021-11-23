package com.ptrbrynt.kotlin_bloc.test

import com.ptrbrynt.kotlin_bloc.core.BlocBase
import io.mockk.every
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

/**
 * Creates a stub response for the `stateFlow` property of the given [bloc].
 *
 * Use [whenListen] when you want to return a canned [Flow] of [State]s for a [bloc] instance.
 *
 * [whenListen] also handles stubbing the `state` of the [bloc] to stay in sync with the emitted
 * state.
 */
fun <B : BlocBase<State>, State> whenListen(
    bloc: B,
    states: Flow<State>,
    initialState: State? = null,
) {
    every { bloc.stateFlow } answers {
        states.onEach {
            every { bloc.state } returns it
        }
    }
    if (initialState != null) {
        every { bloc.state } returns initialState
    }
}
