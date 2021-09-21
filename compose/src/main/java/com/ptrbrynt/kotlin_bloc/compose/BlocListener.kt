package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filter

/**
 * Takes a [bloc] and an [onState] callback and invokes [onState] in response to `state` changes
 * in the [bloc].
 *
 * It should be used for side-effects resulting from new `state`s being emitted by the [bloc] e.g.
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
 * An optional [reactWhen] can be implemented for more granular control over when [onState] is
 * called.
 * [reactWhen] will be invoked on every [bloc] state change.
 * [reactWhen] is optional and, when omitted, will default to `true`.
 *
 * @param bloc The bloc or cubit that the [BlocListener] will interact with.
 * @param onState The callback function which will be invoked whenever a new `state` is emitted by the [bloc].
 * @param reactWhen Provides more granular control over when [onState] is invoked.
 * @see BlocComposer
 */
@FlowPreview
@Composable
fun <B : BlocBase<State>, State> BlocListener(
    bloc: B,
    reactWhen: (State) -> Boolean = { true },
    onState: suspend (State) -> Unit,
) {
    val state by bloc.stateFlow.filter { reactWhen(it) }.collectAsState(initial = null)

    state?.let {
        LaunchedEffect(it) {
            onState(it)
        }
    }
}
