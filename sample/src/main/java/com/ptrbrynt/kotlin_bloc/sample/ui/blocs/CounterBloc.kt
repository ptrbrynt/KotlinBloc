package com.ptrbrynt.kotlin_bloc.sample.ui.blocs

import android.os.Parcelable
import com.ptrbrynt.kotlin_bloc.core.Bloc
import kotlinx.parcelize.Parcelize

enum class CounterEvent { Increment, Decrement }

@Parcelize
class CounterBloc(private val initial: Int) : Bloc<CounterEvent, Int, Unit>(initial), Parcelable {

    init {
        on<CounterEvent> { event ->
            when (event) {
                CounterEvent.Increment -> emit(state + 1)
                CounterEvent.Decrement -> emit(state - 1)
            }
        }
    }
}
