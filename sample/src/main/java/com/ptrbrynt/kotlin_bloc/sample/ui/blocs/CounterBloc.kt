package com.ptrbrynt.kotlin_bloc.sample.ui.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc
import com.ptrbrynt.kotlin_bloc.core.Emitter

enum class CounterEvent { Increment, Decrement }

class CounterBloc : Bloc<CounterEvent, Int>(0) {
    override suspend fun Emitter<Int>.mapEventToState(event: CounterEvent) {
        when (event) {
            CounterEvent.Increment -> emit(state + 1)
            CounterEvent.Decrement -> emit(state - 1)
        }
    }
}
