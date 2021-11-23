package com.ptrbrynt.kotlin_bloc.core

/**
 * An interface for observing the behavior of [Bloc] instances
 */
abstract class BlocObserver {
    /**
     * Called whenever a [Bloc] or [Cubit] is instantiated.
     *
     * In many cases, a cubit may be lazily instantiated and [onCreate]
     * can be used to observe exactly when the instance is created.
     *
     * @param bloc The [Bloc] or [Cubit] which was created.
     */
    open fun <B : BlocBase<*>> onCreate(bloc: B) {}

    /**
     * Called whenever an [event] is `add`ed to any [bloc].
     *
     * @param bloc The [Bloc] to which the [Event] was `add`ed
     * @param event The [Event] added to the [Bloc]
     */
    open fun <B : Bloc<Event, *>, Event> onEvent(bloc: B, event: Event) {}

    /**
     * Called whenever a [Change] occurs in any [Bloc] or [Cubit].
     *
     * A [change] occurs when a new state is emitted.
     * [onChange] is called before the [bloc]'s state is emitted.
     *
     * @param bloc The [Bloc] or [Cubit] which emitted the [change]
     * @param change The [Change] that occurred within the [bloc]
     */
    open fun <B : BlocBase<State>, State> onChange(bloc: B, change: Change<State>) {}

    /**
     * Called whenever a [Transition] occurs in any [Bloc].
     *
     * A [transition] occurs when a new `event` is `add`ed and handled.
     *
     * [onTransition] is called before a [bloc]'s state has been updated.
     *
     * @param bloc The [Bloc] in which the [transition] occurred
     * @param transition The [Transition] which occurred within the [bloc]
     */
    open fun <B : Bloc<Event, State>, Event, State> onTransition(
        bloc: B,
        transition: Transition<Event, State>,
    ) {
    }
}
