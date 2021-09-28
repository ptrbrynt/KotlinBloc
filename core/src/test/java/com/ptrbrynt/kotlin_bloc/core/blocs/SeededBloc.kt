package com.ptrbrynt.kotlin_bloc.core.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc

class SeededBloc(private val seed: List<Int>, initial: Int) : Bloc<String, Int>(initial) {
    init {
        on<String> {
            for (value in seed) {
                emit(value)
            }
        }
    }
}
