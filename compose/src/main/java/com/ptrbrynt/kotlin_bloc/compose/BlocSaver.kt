package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.ptrbrynt.kotlin_bloc.core.BlocBase

/**
 * A [Saver] which enables the state of a Bloc or Cubit to be saved
 * and restored.
 */
internal fun <State : Any, B : BlocBase<State, *>> blocSaver(
    save: SaverScope.(B) -> State = { it.state },
    restore: (State) -> B,
) = Saver(
    save = save,
    restore = { restore(it) },
)
