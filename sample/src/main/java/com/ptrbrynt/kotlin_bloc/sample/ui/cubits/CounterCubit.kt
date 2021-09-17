package com.ptrbrynt.kotlin_bloc.sample.ui.cubits

import com.ptrbrynt.kotlin_bloc.core.Cubit

class CounterCubit : Cubit<Int>(0) {
    fun increment() = emit(state + 1)
    fun decrement() = emit(state - 1)
}
