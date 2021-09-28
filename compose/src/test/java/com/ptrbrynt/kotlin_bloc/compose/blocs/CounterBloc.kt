package com.ptrbrynt.kotlin_bloc.compose.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc
import kotlinx.coroutines.FlowPreview

enum class CounterEvent { Increment, Decrement }

@FlowPreview
class CounterBloc : Bloc<CounterEvent, Int>(0) {
    override suspend fun mapEventToState(event: CounterEvent) {
        when (event) {
            CounterEvent.Increment -> emit(state + 1)
            CounterEvent.Decrement -> emit(state - 1)
        }
    }
}
