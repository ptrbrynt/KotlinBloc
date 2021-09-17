package com.ptrbrynt.kotlin_bloc.core.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@FlowPreview
class SeededBloc(private val seed: List<Int>, initial: Int) : Bloc<String, Int>(initial) {
    override fun mapEventToState(event: String): Flow<Int> = flow {
        for (value in seed) {
            emit(value)
        }
    }
}
