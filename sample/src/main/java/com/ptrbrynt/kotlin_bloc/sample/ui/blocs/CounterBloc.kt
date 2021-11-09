package com.ptrbrynt.kotlin_bloc.sample.ui.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc

enum class CounterEvent { Increment, Decrement }

class CounterBloc(initial: Int) : Bloc<CounterEvent, Int, Unit>(initial) {

    init {
        on<CounterEvent> { event ->
            when (event) {
                CounterEvent.Increment -> emit(state + 1)
                CounterEvent.Decrement -> emit(state - 1)
            }
        }
    }
}
