package com.ptrbrynt.kotlin_bloc.core.blocs

import com.ptrbrynt.kotlin_bloc.core.Transition
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

@FlowPreview
class DebounceBloc : CounterBloc() {

    override fun transformTransitions(
        transitions: Flow<Transition<CounterEvent, Int>>,
    ): Flow<Transition<CounterEvent, Int>> {
        return transitions.onEach { delay(2000) }
    }
}
