package com.ptrbrynt.kotlin_bloc.compose

import android.util.Log
import com.ptrbrynt.kotlin_bloc.core.Bloc
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import com.ptrbrynt.kotlin_bloc.core.BlocObserver
import com.ptrbrynt.kotlin_bloc.core.Change
import com.ptrbrynt.kotlin_bloc.core.Transition

/**
 * A [BlocObserver] which logs all bloc events to the console.
 */

class LoggingBlocObserver : BlocObserver() {

    override fun <B : BlocBase<State>, State> onCreate(bloc: B) {
        super.onCreate(bloc)
        Log.i(bloc::class.simpleName, "Created")
    }

    override fun <B : BlocBase<State>, State> onChange(bloc: B, change: Change<State>) {
        super.onChange(bloc, change)
        Log.i(bloc::class.simpleName, change.toString())
    }

    override fun <B : Bloc<Event, State>, Event, State> onEvent(bloc: B, event: Event) {
        super.onEvent(bloc, event)
        Log.i(bloc::class.simpleName, event.toString())
    }

    override fun <B : Bloc<Event, State>, Event, State> onTransition(
        bloc: B,
        transition: Transition<Event, State>,
    ) {
        super.onTransition(bloc, transition)
        Log.i(bloc::class.simpleName, transition.toString())
    }
}
