package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

/**
 * Takes [Event]s as input and transforms them into a [Flow]
 * of [State]s as an output.
 *
 * @param initial The initial [State]
 * @see Cubit
 */
@Suppress("LeakingThis")
abstract class Bloc<Event, State>(initial: State) : BlocBase<State>(initial) {
    private val eventFlow = MutableSharedFlow<Event>()

    private val transitionFlow = eventFlow
        .onEach { onEvent(it) }
        .transformEvents()
        .onEach { mapEventToState(it) }
        .zip(mutableChangeFlow) { event, change ->
            Transition(change.state, event, change.newState)
        }
        .onEach { onTransition(it) }

    init {
        transitionFlow.launchIn(blocScope)
    }

    /**
     * Must be implemented when a class extends [Bloc].
     *
     * [mapEventToState] is called whenever an [event] is [add]ed
     * and is responsible for converting that [event] into a new
     * set of [State]s.
     *
     * [mapEventToState] can emit zero, one, or multiple [State]s for each [event].
     */
    abstract suspend fun mapEventToState(event: Event)

    /**
     * Notifies the [Bloc] of a new [event], which triggers [mapEventToState].
     */
    fun add(event: Event) = blocScope.launch {
        eventFlow.emit(event)
    }

    /**
     * Called whenever an [event] is [add]ed to the [Bloc].
     *
     * A great place to add logging/analytics.
     *
     * **Note: `super.onEvent` should always be called first.**
     *
     * ```kotlin
     * override fun onEvent(event: Event) {
     *   // Always call super.onEvent with the current event
     *   super.onEvent(event)
     *
     *   // Custom logic goes here
     * }
     * ```
     */
    protected open fun onEvent(event: Event) {
        observer.onEvent(this, event)
    }

    /**
     * Called when a new [Transition] occurs.
     *
     * [onTransition] is called before the `state` of the `bloc` is updated.
     * [onTransition] is a great place to add logging/analytics.
     *
     * **Note: `super.onTransition` should always be called first.**
     *
     * ```kotlin
     * override fun onTransition(transition: Transition<Event, State>) {
     *   // Always call super.onChange with the current change
     *   super.onTransition(transition)
     *
     *   // Custom logic goes here
     * }
     * ```
     */
    protected open fun onTransition(transition: Transition<Event, State>) {
        observer.onTransition(this, transition)
    }

    /**
     * Transforms the incoming [Flow] of [Event]s into a new [Flow] of [Event]s.
     *
     * By default, [transformEvents] returns the incoming [Event] [Flow] unchanged.
     *
     * You can override [transformEvents] for advanced usage in order to manipulate the
     * frequency and specificity at which events are passed to [mapEventToState].
     *
     * For example, to debounce incoming events:
     *
     * ```kotlin
     * override fun Flow<Event>transformEvents() = this.debounce(100)
     * ```
     */
    protected open fun Flow<Event>.transformEvents() = this

    companion object {
        var observer: BlocObserver = SilentBlocObserver()
    }
}
