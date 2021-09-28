package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import kotlinx.coroutines.flow.Flow

/**
 * [BlocComposer] handles re-composition of its [content] in response to new [State]s.
 *
 * Please use [BlocListener] if you want to *do* anything in response to [State] changes
 * e.g. display a Snackbar.
 *
 * ```kotlin
 * BlocComposer(myBloc) { state ->
 *   // Add your composables here
 * }
 * ```
 *
 * An optional [transformStates] can be implemented for more granular control over
 * the frequency and specificity with which transitions occur.
 *
 * For example, to debounce the state changes:
 *
 * ```kotlin
 * BlocComposer(
 *   myBloc,
 *   transformStates = { this.debounce(1000) },
 * ) {
 *   // Your content goes here
 * }
 * ```
 *
 * @param bloc The bloc or cubit that the [BlocComposer] will interact with.
 * @param content The composable function which is re-composed on each new [bloc] state.
 * @param transformStates Provides more granular control over the [State] flow.
 * @see BlocListener
 */

@Composable
fun <B : BlocBase<State>, State> BlocComposer(
    bloc: B,
    transformStates: Flow<State>.() -> Flow<State> = { this },
    content: @Composable (State) -> Unit,
) {
    val state by bloc.stateFlow
        .transformStates()
        .collectAsState(initial = bloc.state)

    content(state)
}
