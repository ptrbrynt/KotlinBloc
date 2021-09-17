package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import kotlinx.coroutines.flow.filter

/**
 * [BlocComposer] handles re-composition of its [content] in response to new [State]s.
 *
 * Please use [BlocListener] if you want to *do* anything in response to [State] changes
 * e.g. display a Snackbar.
 *
 * ```kotlin
 * BlocBuilder(myBloc) { state ->
 *   // Add your composables here
 * }
 * ```
 *
 * An optional [composeWhen] can be implemented for more granular control over
 * how often [BlocComposer] re-composes.
 * [composeWhen] will be invoked whenever the [bloc] state changes.
 * [composeWhen] is optional and, when omitted, will default to `true`.
 *
 * @param bloc The bloc or cubit that the [BlocComposer] will interact with.
 * @param content The composable function which is re-composed on each new [bloc] state.
 * @param composeWhen Provides more granular control over how often [BlocComposer] re-composes.
 * @see BlocListener
 */
@Composable
fun <B : BlocBase<State>, State> BlocComposer(
    bloc: B,
    composeWhen: (State) -> Boolean = { true },
    content: @Composable (State) -> Unit,
) {
    val state by bloc.stateFlow.filter { composeWhen(it) }.collectAsState(initial = bloc.state)

    content(state)
}
