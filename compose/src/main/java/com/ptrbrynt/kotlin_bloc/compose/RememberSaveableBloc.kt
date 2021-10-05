package com.ptrbrynt.kotlin_bloc.compose

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.ptrbrynt.kotlin_bloc.core.BlocBase

/**
 * Remembers the [State] of a Bloc or Cubit.
 *
 * The [State] will survive activity or process recreation (e.g. when the screen is rotated).
 *
 * You must provide a [create] function, which should return an instance of the Bloc or Cubit
 * with the given state as its initial state.
 *
 * If the [State] **cannot** be saved in a [Bundle] (i.e. it's not a primitive or [Parcelable])
 * then you should provide a custom [save] function which converts your [State] into something which
 * can be saved in a [Bundle].
 *
 * If you omit the [save] parameter, [rememberSaveableBloc] assumes that the [State] type can be
 * saved in a [Bundle].
 *
 * ```kotlin
 * enum class CounterEvent { Incremented, Decremented }
 *
 * class CounterBloc(initial: Int): Bloc<CounterEvent, Int>(initial) {
 *
 *   init {
 *     on<CounterEvent> {
 *       // ...
 *     }
 *   }
 * }
 *
 * @Composable
 * fun Counter() {
 *   val bloc = rememberSaveableBloc(initialState = 0) { CounterBloc(it) }
 *
 *   // ...
 * }
 * ```
 *
 * @throws AssertionError if no [save] parameter is provided and the [State] type cannot be saved.
 */
@Composable
fun <State : Any, B : BlocBase<State>> rememberSaveableBloc(
    save: SaverScope.(B) -> State = {
        assert(canBeSaved(it))
        it.state
    },
    initialState: State,
    create: (State) -> B,
) = rememberSaveable(saver = blocSaver(save, create), init = { create(initialState) })
