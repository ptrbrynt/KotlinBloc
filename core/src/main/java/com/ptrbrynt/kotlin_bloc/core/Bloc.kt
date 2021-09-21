package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Takes [Event]s as input and transforms them into a [Flow]
 * of [State]s as an output.
 *
 * @param initial The initial [State]
 * @see Cubit
 */
@FlowPreview
abstract class Bloc<Event, State>(initial: State) : BlocBase<State>(initial) {
    private val eventFlow = MutableSharedFlow<Event>()

    init {
        scope.launch {
            transformTransitions(
                transformEvents(
                    eventFlow.onEach { onEvent(it) },
                ) { event ->
                    mapEventToState(event)
                        .map { newState -> Transition(state, event, newState) }
                        .onEach { onTransition(it) }
                }
            ).collect {
                mutableStateFlow.emit(it.newState)
            }
        }
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
    abstract fun mapEventToState(event: Event): Flow<State>

    /**
     * Notifies the [Bloc] of a new [event], which triggers [mapEventToState].
     */
    fun add(event: Event) = scope.launch {
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
     * Transforms the [Flow] of [Transition]s into a new [Flow] of [Transition]s.
     *
     * By default, [transformTransitions] returns the incoming [transitions] [Flow].
     *
     * You can override [transformTransitions] for advanced usage in order to
     * manipulate the frequency and specificity at which `transitions` may occur.
     *
     * For example, to debounce ongoing state changes:
     *
     * ```kotlin
     * override fun transformTransitions(transitions: Flow<Transition<Event, State>>): Flow<Transition<Event, State>> {
     *   return transitions.debounce(100)
     * }
     * ```
     */
    protected open fun transformTransitions(
        transitions: Flow<Transition<Event, State>>,
    ) = transitions

    /**
     * Transforms the [events] flow along with a [transitionFn] function into
     * a [Flow] of [Transition]s.
     *
     * Events that should be processed by [mapEventToState] must be passed into
     * [transitionFn].
     *
     * By default [flatMapConcat] is used to ensure all [events] are processed
     * in the order in which they are received.
     *
     * You can override [transformEvents] for advanced usage in order to manipulate
     * the frequency and specificity with which [mapEventToState] is called, as
     * well as which [events] are processed.
     *
     * For example, if you only want [mapEventToState] to be called on the most recent
     * [Event], you can use [flatMapLatest] instead of [flatMapConcat]:
     *
     * ```kotlin
     * override fun transformEvents(
     *   events: Flow<Event>,
     *   transitionFn: (Event) -> Flow<Transition<Event, State>>,
     * ) = events.flatMapLatest { transitionFn(it) }
     * ```
     *
     * Alternatively, if you only want [mapEventToState] to be called for distinct
     * [events]:
     *
     * ```kotlin
     * override fun transformEvents(
     *   events: Flow<Event>,
     *   transitionFn: (Event) -> Flow<Transition<Event, State>>,
     * ) = super.transformEvents(events.distinctUntilChanged(), transitionFn)
     * ```
     */
    protected open fun transformEvents(
        events: Flow<Event>,
        transitionFn: (Event) -> Flow<Transition<Event, State>>,
    ) = events.flatMapConcat { transitionFn(it) }

    companion object {
        var observer: BlocObserver = SilentBlocObserver()
    }
}
