package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.FlowPreview

/**
 * A [BlocObserver] that does nothing.
 */
@FlowPreview
internal class SilentBlocObserver : BlocObserver {
    override fun <State> onCreate(bloc: BlocBase<State>) {
    }

    override fun <Event, State> onEvent(bloc: Bloc<Event, State>, event: Event) {
    }

    override fun <State> onChange(bloc: BlocBase<State>, change: Change<State>) {

    }

    override fun <Event, State> onTransition(
        bloc: Bloc<Event, State>,
        transition: Transition<Event, State>,
    ) {
    }
}
