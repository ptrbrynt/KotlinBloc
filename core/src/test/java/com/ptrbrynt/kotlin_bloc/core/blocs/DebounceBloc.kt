package com.ptrbrynt.kotlin_bloc.core.blocs

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class DebounceBloc : CounterBloc() {

    override fun Flow<CounterEvent>.transformEvents(): Flow<CounterEvent> {
        return onEach { delay(2000) }
    }
}
