package com.ptrbrynt.kotlin_bloc.test.cubits

import com.ptrbrynt.kotlin_bloc.core.Cubit

class CounterCubit : Cubit<Int>(0) {

    suspend fun increment() = emit(state + 1)
}
