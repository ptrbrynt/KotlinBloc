package com.ptrbrynt.kotlin_bloc.compose.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

enum class CounterEvent { Increment, Decrement }

@FlowPreview
class CounterBloc : Bloc<CounterEvent, Int>(0) {
    override fun mapEventToState(event: CounterEvent): Flow<Int> = flow {
        when (event) {
            CounterEvent.Increment -> emit(state + 1)
            CounterEvent.Decrement -> emit(state - 1)
        }
    }
}
