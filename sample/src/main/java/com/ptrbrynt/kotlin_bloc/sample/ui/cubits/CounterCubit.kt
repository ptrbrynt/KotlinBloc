package com.ptrbrynt.kotlin_bloc.sample.ui.cubits

import com.ptrbrynt.kotlin_bloc.core.Cubit
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@FlowPreview
class CounterCubit : Cubit<Int>(0) {
    fun increment() = blocScope.launch { emit(state + 1) }
}
