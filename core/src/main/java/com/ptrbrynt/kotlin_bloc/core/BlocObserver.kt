package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.FlowPreview

/**
 * An interface for observing the behavior of [Bloc] instances
 */
@FlowPreview
interface BlocObserver {
    /**
     * Called whenever a [Bloc] or [Cubit] is instantiated.
     *
     * In many cases, a cubit may be lazily instantiated and [onCreate]
     * can be used to observe exactly when the instance is created.
     *
     * @param bloc The [Bloc] or [Cubit] which was created.
     */
    fun <State> onCreate(bloc: BlocBase<State>)

    /**
     * Called whenever an [event] is `add`ed to any [bloc].
     *
     * @param bloc The [Bloc] to which the [Event] was `add`ed
     * @param event The [Event] added to the [Bloc]
     */
    fun <Event, State> onEvent(bloc: Bloc<Event, State>, event: Event)

    /**
     * Called whenever a [Change] occurs in any [Bloc] or [Cubit].
     *
     * A [change] occurs when a new state is emitted.
     * [onChange] is called before the [bloc]'s state is emitted.
     *
     * @param bloc The [Bloc] or [Cubit] which emitted the [change]
     * @param change The [Change] that occurred within the [bloc]
     */
    fun <State> onChange(bloc: BlocBase<State>, change: Change<State>)

    /**
     * Called whenever a [Transition] occurs in any [Bloc].
     *
     * A [transition] occurs when a new `event` is `add`ed and `mapEventToState`
     * is executed.
     *
     * [onTransition] is called before a [bloc]'s state has been updated.
     *
     * @param bloc The [Bloc] in which the [transition] occurred
     * @param transition The [Transition] which occurred within the [bloc]
     */
    fun <Event, State> onTransition(
        bloc: Bloc<Event, State>,
        transition: Transition<Event, State>,
    )
}
