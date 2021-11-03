package com.ptrbrynt.kotlin_bloc.sample.ui.blocs

import com.ptrbrynt.kotlin_bloc.test.testBloc
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalTime
class CounterBlocTest {
    @Test
    fun counterBlocTest() = runBlocking {
        testBloc(
            build = { CounterBloc(0) },
            act = {
                add(CounterEvent.Increment)
                add(CounterEvent.Increment)
                add(CounterEvent.Decrement)
            },
            expected = listOf(
                { equals(1) },
                { equals(2) },
                { equals(1) },
            )
        )
    }
}
