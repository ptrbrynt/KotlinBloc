package com.ptrbrynt.kotlin_bloc.core.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc

class SeededBloc(private val seed: List<Int>, initial: Int) : Bloc<String, Int>(initial) {
    override suspend fun mapEventToState(event: String) {
        for (value in seed) {
            emit(value)
        }
    }
}
