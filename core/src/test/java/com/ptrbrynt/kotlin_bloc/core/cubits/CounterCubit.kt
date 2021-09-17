package com.ptrbrynt.kotlin_bloc.core.cubits

import com.ptrbrynt.kotlin_bloc.core.Change
import com.ptrbrynt.kotlin_bloc.core.Cubit

class CounterCubit(private val onChangeCallback: ((Change<Int>) -> Unit)? = null) : Cubit<Int>(0) {
    fun increment() = emit(state + 1)
    fun decrement() = emit(state - 1)

    override fun onChange(change: Change<Int>) {
        super.onChange(change)
        onChangeCallback?.invoke(change)
    }
}
