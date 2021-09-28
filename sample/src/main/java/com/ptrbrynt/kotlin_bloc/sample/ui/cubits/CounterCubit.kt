package com.ptrbrynt.kotlin_bloc.sample.ui.cubits

import com.ptrbrynt.kotlin_bloc.core.Cubit
import kotlinx.coroutines.launch

class CounterCubit : Cubit<Int>(0) {
    fun increment() = blocScope.launch { emit(state + 1) }
}
