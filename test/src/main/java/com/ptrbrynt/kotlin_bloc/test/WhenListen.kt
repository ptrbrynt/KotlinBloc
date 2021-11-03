package com.ptrbrynt.kotlin_bloc.test

import com.ptrbrynt.kotlin_bloc.core.BlocBase
import io.mockk.every
import kotlinx.coroutines.flow.Flow

fun <B : BlocBase<State>, State> whenListen(
    bloc: B,
    states: Flow<State>,
    initialState: State? = null,
) {
    every { bloc.stateFlow } answers { states }
    if (initialState != null) {
        every { bloc.state } returns initialState
    }
}
