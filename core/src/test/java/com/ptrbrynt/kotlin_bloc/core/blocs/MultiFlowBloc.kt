package com.ptrbrynt.kotlin_bloc.core.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc
import com.ptrbrynt.kotlin_bloc.core.Emitter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

sealed class MultiFlowEvent

object MultiFlowInitialized : MultiFlowEvent()

data class MultiFlowNumberAdded(val number: Int) : MultiFlowEvent()

@ExperimentalCoroutinesApi

class MultiFlowBloc : Bloc<MultiFlowEvent, List<Int>>(emptyList()) {
    private val numbers = MutableStateFlow(emptyList<Int>())

    override suspend fun Emitter<List<Int>>.mapEventToState(event: MultiFlowEvent) {
        when (event) {
            is MultiFlowInitialized -> emitEach(numbers)
            is MultiFlowNumberAdded -> {
                numbers.tryEmit(numbers.value + event.number)
            }
        }
    }
}
