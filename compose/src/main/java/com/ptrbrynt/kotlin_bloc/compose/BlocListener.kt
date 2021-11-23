package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import kotlinx.coroutines.flow.Flow

/**
 * Takes a [bloc] and an [onState] callback and invokes [onState] in response to state changes in the [bloc].
 *
 * It should be used for side-effects resulting from new states being emitted by the [bloc] e.g.
 * navigation, showing a snackbar etc.
 *
 * If you want to build composables in response to new states, use [BlocComposer]
 *
 * ```kotlin
 * BlocListener(bloc) { state ->
 *   // React to the new state here
 * }
 * ```
 *
 * An optional [transformStates] can be implemented for more granular control over
 * the frequency and specificity with which transitions occur.
 *
 * For example, to debounce the states:
 *
 * ```kotlin
 * BlocListener(
 *   myBloc,
 *   transformStates = { this.debounce(1000) },
 * ) {
 *   // React to the new state here
 * }
 * ```
 *
 * @param bloc The bloc or cubit that the [BlocListener] will interact with.
 * @param onState The callback function which will be invoked whenever a new `state` is emitted by the [bloc].
 * @param transformStates Provides more granular control over the [State] flow.
 * @see BlocComposer
 */

@Composable
fun <B : BlocBase<State>, State> BlocListener(
    bloc: B,
    transformStates: Flow<State>.() -> Flow<State> = { this },
    onState: suspend (State) -> Unit,
) {
    val state by bloc.stateFlow.transformStates().collectAsState(initial = null)

    state?.let {
        LaunchedEffect(it) {
            onState(it)
        }
    }
}
