package com.ptrbrynt.kotlin_bloc.core

import app.cash.turbine.test
import com.ptrbrynt.kotlin_bloc.core.blocs.CounterBloc
import com.ptrbrynt.kotlin_bloc.core.blocs.CounterEvent
import com.ptrbrynt.kotlin_bloc.core.blocs.DebounceBloc
import com.ptrbrynt.kotlin_bloc.core.blocs.Decrement
import com.ptrbrynt.kotlin_bloc.core.blocs.Increment
import com.ptrbrynt.kotlin_bloc.core.blocs.IncrementOnlyCounterBloc
import com.ptrbrynt.kotlin_bloc.core.blocs.MultiFlowBloc
import com.ptrbrynt.kotlin_bloc.core.blocs.MultiFlowInitialized
import com.ptrbrynt.kotlin_bloc.core.blocs.MultiFlowNumberAdded
import com.ptrbrynt.kotlin_bloc.core.blocs.SeededBloc
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class BlocTest {

    @Test
    fun `CounterBloc calls observer correctly`() = runBlocking {
        val observer = mockk<BlocObserver>(relaxed = true)

        Bloc.observer = observer

        val bloc = CounterBloc()

        bloc.add(Increment)

        verifyOrder {
            observer.onCreate(bloc)

            observer.onEvent(bloc, Increment)

            observer.onChange(bloc, Change(0, 1))

            observer.onTransition(
                bloc,
                Transition(0, Increment, 1)
            )
        }
    }

    @Test
    fun `CounterBloc initial state is 0`() = runBlocking {
        val bloc = CounterBloc()

        assertEquals(0, bloc.state)
    }

    @Test
    fun `CounterBloc emits correct states`() = runBlocking {
        val bloc = CounterBloc()

        bloc.stateFlow.test {
            bloc.add(Increment)

            assertEquals(1, awaitItem())

            bloc.add(Increment)

            assertEquals(2, awaitItem())

            bloc.add(Decrement)

            assertEquals(1, awaitItem())
        }
    }

    @Test
    fun `CounterBloc onEvent callback is invoked for all events`() = runBlocking {
        val events = mutableListOf<CounterEvent>()
        val bloc = CounterBloc(
            onEventCallback = {
                events.add(it)
            }
        )

        bloc.add(Increment)
        bloc.add(Decrement)

        assertContains(events, Increment)
        assertContains(events, Decrement)
    }

    @Test
    fun `CounterBloc state value stays up-to-date`() = runBlocking {
        val bloc = CounterBloc()

        bloc.add(Increment)

        assertEquals(1, bloc.state)

        bloc.add(Increment)

        assertEquals(2, bloc.state)

        bloc.add(Decrement)

        assertEquals(1, bloc.state)
    }

    @Test
    fun `CounterBloc onTransition callback works as expected`() = runBlocking {
        var transition: Transition<CounterEvent, Int>? = null
        val bloc = CounterBloc(
            onTransitionCallback = {
                transition = it
            }
        )

        bloc.add(Increment)

        assertEquals(
            Transition<CounterEvent, Int>(0, Increment, 1),
            transition,
        )
    }

    @Test
    fun `SeededBloc emits correct states`() = runBlocking {
        val bloc = SeededBloc(listOf(1, 2), 0)

        assertEquals(0, bloc.state)

        bloc.stateFlow.test {
            bloc.add("Hello")

            assertEquals(1, awaitItem())

            assertEquals(2, awaitItem())
        }
    }

    @Test
    fun `DebounceBloc emits correct states`() = runBlocking {
        val bloc = DebounceBloc()

        bloc.stateFlow.test(Duration.seconds(2.5)) {
            val time = measureTimeMillis {
                bloc.add(Increment)

                assertEquals(1, awaitItem())
            }

            assertTrue(time >= 2000)
        }
    }

    @Test
    fun `IncrementOnlyCounterBloc only responds to Increment events`() = runBlocking {
        val bloc = IncrementOnlyCounterBloc()

        bloc.stateFlow.test {
            bloc.add(Increment)

            assertEquals(1, awaitItem())

            bloc.add(Decrement)

            expectNoEvents()

            bloc.add(Increment)

            assertEquals(2, awaitItem())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `MultiFlowBloc still receives events after initialize`() = runBlocking {
        val bloc = MultiFlowBloc()

        bloc.stateFlow.test {
            bloc.add(MultiFlowInitialized)

            assertEquals(emptyList(), awaitItem())

            bloc.add(MultiFlowNumberAdded(1))

            assertEquals(listOf(1), awaitItem())
        }
    }
}
