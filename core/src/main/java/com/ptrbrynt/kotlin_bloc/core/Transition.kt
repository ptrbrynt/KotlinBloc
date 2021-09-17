package com.ptrbrynt.kotlin_bloc.core

/**
 * Represents a transition from one [State] to a new [State] in a [Bloc],
 * as a result of an [Event].
 */
data class Transition<Event, State>(val state: State, val event: Event, val newState: State)
