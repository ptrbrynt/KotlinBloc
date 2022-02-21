package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

/**
 * [BlocSelector] is analogous to [BlocComposer] but allows developers to
 * filter updates by selecting a new value based on the bloc state.
 *
 * ```kotlin
 * BlocSelector<BlocA, BlocAState, SelectedState>(
 *   bloc = bloc,
 *   selector = { state: BlocAState ->
 *     // Return a SelectedState based on the incoming BlocAState
 *   }
 * ) { state: SelectedState ->
 *   // Write your composable based on the incoming SelectedState
 * }
 * ```
 *
 * @param bloc The bloc or cubit with which [BlocSelector] will interact
 * @param selector A function invoked on each new bloc [State], responsible for returning a new value of [T] based on the incoming [State].
 * @param content The composable which will be composed on each [bloc] [State] change, based on the value returned by [selector].
 */

@Composable
fun <B : BlocBase<State>, State, T> BlocSelector(
    bloc: B,
    selector: (State) -> T,
    content: @Composable (T) -> Unit,
) {
    val state by produceState(initialValue = selector(bloc.state)) {
        bloc.stateFlow.map { selector(it) }.collect { value = it }
    }

    content(state)
}
