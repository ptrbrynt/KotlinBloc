package com.ptrbrynt.kotlin_bloc.test.blocs.counter

sealed class CounterEvent

object Incremented : CounterEvent()

object Decremented : CounterEvent()
