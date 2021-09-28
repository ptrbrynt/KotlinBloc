package com.ptrbrynt.kotlin_bloc.core.blocs

import com.ptrbrynt.kotlin_bloc.core.Bloc
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed class MultiFlowEvent

object MultiFlowInitialized : MultiFlowEvent()

data class MultiFlowNumberAdded(val number: Int) : MultiFlowEvent()

@ExperimentalCoroutinesApi

class MultiFlowBloc : Bloc<MultiFlowEvent, List<Int>>(emptyList()) {
    private val numbers = MutableStateFlow(emptyList<Int>())

    override suspend fun mapEventToState(event: MultiFlowEvent) {
        when (event) {
            is MultiFlowInitialized -> numbers.onEach { emit(it) }.launchIn(blocScope)
            is MultiFlowNumberAdded -> {
                numbers.tryEmit(numbers.value + event.number)
            }
        }
    }
}
