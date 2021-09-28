package com.ptrbrynt.kotlin_bloc.core.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc
import com.ptrbrynt.kotlin_bloc.core.Transition
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

enum class CounterEvent { Increment, Decrement }

@FlowPreview
open class CounterBloc(
    private val onTransitionCallback: ((Transition<CounterEvent, Int>) -> Unit)? = null,
    private val onEventCallback: ((CounterEvent) -> Unit)? = null,
) : Bloc<CounterEvent, Int>(0) {
    override suspend fun mapEventToState(event: CounterEvent) {
        when (event) {
            CounterEvent.Increment -> emit(state + 1)
            CounterEvent.Decrement -> emit(state - 1)
        }
    }

    override fun onTransition(transition: Transition<CounterEvent, Int>) {
        super.onTransition(transition)
        onTransitionCallback?.invoke(transition)
    }

    override fun onEvent(event: CounterEvent) {
        super.onEvent(event)
        onEventCallback?.invoke(event)
    }
}

@FlowPreview
class IncrementOnlyCounterBloc : CounterBloc() {
    override fun Flow<CounterEvent>.transformEvents(): Flow<CounterEvent> {
        return filter { it == CounterEvent.Increment }
    }
}
