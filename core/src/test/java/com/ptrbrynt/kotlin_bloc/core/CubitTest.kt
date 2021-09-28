package com.ptrbrynt.kotlin_bloc.core

import app.cash.turbine.test
import com.ptrbrynt.kotlin_bloc.core.cubits.CounterCubit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class CubitTest {
    @Test
    fun `CounterCubit initial state is 0`() = runBlocking {
        val cubit = CounterCubit()

        assertEquals(0, cubit.state)
    }

    @Test
    fun `CounterCubit emits correct states`() = runBlocking {
        val cubit = CounterCubit()

        cubit.stateFlow.test {
            cubit.increment()

            assertEquals(1, awaitItem())

            cubit.increment()

            assertEquals(2, awaitItem())

            cubit.decrement()

            assertEquals(1, awaitItem())
        }
    }

    @Test
    fun `CounterCubit state property stays in sync`() = runBlocking {
        val cubit = CounterCubit()

        cubit.increment()

        assertEquals(1, cubit.state)

        cubit.increment()

        assertEquals(2, cubit.state)

        cubit.decrement()

        assertEquals(1, cubit.state)
    }

    @Test
    fun `CounterCubit onChangeCallback is called`() = runBlocking {
        var change: Change<Int>? = null
        val cubit = CounterCubit {
            change = it
        }

        cubit.increment()

        assertEquals(Change(0, 1), change)
    }
}
