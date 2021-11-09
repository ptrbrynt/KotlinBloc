package com.ptrbrynt.kotlin_bloc.compose.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc

enum class CounterEvent { Increment, Decrement }

class CounterBloc : Bloc<CounterEvent, Int, Int>(0) {
    init {
        on<CounterEvent> { event ->
            when (event) {
                CounterEvent.Increment -> emit(state + 1)
                CounterEvent.Decrement -> emit(state - 1)
            }
            emitSideEffect(state)
        }
    }
}
