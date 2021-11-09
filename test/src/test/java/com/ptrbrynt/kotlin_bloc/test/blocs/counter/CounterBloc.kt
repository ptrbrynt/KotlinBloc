package com.ptrbrynt.kotlin_bloc.test.blocs.counter

import com.ptrbrynt.kotlin_bloc.core.Bloc

class CounterBloc : Bloc<CounterEvent, Int, Unit>(0) {
    init {
        on<Incremented> {
            emit(state + 1)
        }
        on<Decremented> {
            emit(state - 1)
        }
    }
}
