package com.ptrbrynt.kotlin_bloc.core

/**
 * Represents a state change in a [Bloc] or [Cubit]
 */
data class Change<State>(val state: State, val newState: State)
