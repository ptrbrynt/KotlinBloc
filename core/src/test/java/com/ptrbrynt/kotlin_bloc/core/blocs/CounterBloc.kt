package com.ptrbrynt.kotlin_bloc.core.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc
import com.ptrbrynt.kotlin_bloc.core.Transition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

sealed class CounterEvent

object Increment : CounterEvent()

object Decrement : CounterEvent()

open class CounterBloc(
    private val onTransitionCallback: ((Transition<CounterEvent, Int>) -> Unit)? = null,
    private val onEventCallback: ((CounterEvent) -> Unit)? = null,
) : Bloc<CounterEvent, Int>(0) {

    init {
        on<Increment> {
            emit(state + 1)
        }
        on<Decrement> {
            emit(state - 1)
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

class IncrementOnlyCounterBloc : CounterBloc() {
    override fun Flow<CounterEvent>.transformEvents(): Flow<CounterEvent> {
        return filter { it is Increment }
    }
}
