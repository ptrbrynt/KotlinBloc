package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import kotlinx.coroutines.flow.Flow

/**
 * Takes a [bloc] and an [onSideEffect] callback and invokes [onSideEffect] in response to side
 * effects in the [bloc].
 *
 * It should be used for side-effects resulting from new side-effects being emitted by the [bloc] e.g.
 * navigation, showing a snackbar etc.
 *
 * If you want to build composables in response to new states, use [BlocComposer]
 *
 * ```kotlin
 * BlocListener(bloc) { sideEffect ->
 *   // React to the new side effect here
 * }
 * ```
 *
 * * An optional [transformSideEffects] can be implemented for more granular control over
 * the frequency and specificity with which transitions occur.
 *
 * For example, to debounce the side effects:
 *
 * ```kotlin
 * BlocListener(
 *   myBloc,
 *   transformSideEffects = { this.debounce(1000) },
 * ) {
 *   // React to the new side-effect here
 * }
 * ```
 *
 * @param bloc The bloc or cubit that the [BlocListener] will interact with.
 * @param onSideEffect The callback function which will be invoked whenever a new `state` is emitted by the [bloc].
 * @param transformSideEffects Provides more granular control over the [State] flow.
 * @see BlocComposer
 */

@Composable
fun <B : BlocBase<*, SideEffect>, SideEffect> BlocListener(
    bloc: B,
    transformSideEffects: Flow<SideEffect>.() -> Flow<SideEffect> = { this },
    onSideEffect: suspend (SideEffect) -> Unit,
) {
    val state by bloc.sideEffectFlow.transformSideEffects().collectAsState(initial = null)

    state?.let {
        LaunchedEffect(it) {
            onSideEffect(it)
        }
    }
}
