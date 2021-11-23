package com.ptrbrynt.kotlin_bloc.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

/**
 * Takes [Event]s as input and transforms them into a [Flow]
 * of [State]s as an output.
 *
 * @param initial The initial [State]
 * @param Event The type of event this can receive
 * @param State The type of state this emits
 * @see Cubit
 */
@Suppress("LeakingThis")
abstract class Bloc<Event, State>(initial: State) :
    BlocBase<State>(initial) {
    protected val eventFlow = MutableSharedFlow<Event>()

    init {
        eventFlow
            .onEach { onEvent(it) }
            .zip(mutableChangeFlow) { event, change ->
                Transition(change.state, event, change.newState)
            }
            .onEach { onTransition(it) }
            .launchIn(blocScope)
    }

    @PublishedApi
    internal val emitter = object : Emitter<State> {
        override suspend fun emit(state: State) {
            mutableChangeFlow.emit(Change(this@Bloc.state, state))
        }

        override suspend fun emitEach(states: Flow<State>) {
            states.onEach { emit(it) }.launchIn(blocScope)
        }
    }

    /**
     * Registers an event handler for events of type [E].
     *
     * There should only ever be one event handler per event type [E].
     *
     * ```kotlin
     * sealed class CounterEvent
     *
     * object Increment: CounterEvent()
     *
     * class CounterBloc : Bloc<CounterEvent, Int> {
     *   init {
     *     on<Increment> { emit(state + 1) }
     *   }
     * }
     * ```
     *
     * @param mapEventToState Function which responds to each event of the given type
     * @since 0.13
     * @param E The type of [Event] that this handles
     */
    protected inline fun <reified E : Event> on(
        noinline mapEventToState: suspend Emitter<State>.(E) -> Unit,
    ) {
        eventFlow
            .transformEvents()
            .filterIsInstance<E>()
            .onEach { emitter.mapEventToState(it) }
            .launchIn(blocScope)
    }

    /**
     * Notifies the [Bloc] of a new [event], which triggers the event handler registered by [on].
     */
    fun add(event: Event) {
        blocScope.launch {
            eventFlow.emit(event)
        }
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
     * frequency and specificity at which events are passed into their event handlers.
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
