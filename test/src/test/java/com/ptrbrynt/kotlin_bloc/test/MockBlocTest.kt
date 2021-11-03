package com.ptrbrynt.kotlin_bloc.test

import com.ptrbrynt.kotlin_bloc.test.blocs.counter.CounterBloc
import com.ptrbrynt.kotlin_bloc.test.blocs.counter.CounterEvent
import com.ptrbrynt.kotlin_bloc.test.cubits.CounterCubit
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MockBlocTest {

    @Test
    fun mockCubitTest() = runBlocking {
        val cubit = mockCubit<CounterCubit, Int>()

        whenListen(cubit, flowOf(0, 1, 2))

        assertFails { cubit.state }

        testBloc(
            build = { cubit },
            expected = listOf(
                { equals(0) },
                { equals(1) },
                { equals(2) },
            ),
        )
    }

    @Test
    fun mockCubitTestWithInitialState() = runBlocking {
        val cubit = mockCubit<CounterCubit, Int>()

        whenListen(cubit, flowOf(0, 1, 2), initialState = 1)

        assertEquals(1, cubit.state)

        testBloc(
            build = { cubit },
            expected = listOf(
                { equals(0) },
                { equals(1) },
                { equals(2) },
            ),
        )

        assertEquals(2, cubit.state)
    }

    @Test
    fun mockBlocTest() = runBlocking {
        val bloc = mockBloc<CounterBloc, CounterEvent, Int>()

        whenListen(bloc, flowOf(0, 1, 2))

        assertFails { bloc.state }

        testBloc(
            build = { bloc },
            expected = listOf(
                { equals(0) },
                { equals(1) },
                { equals(2) },
            ),
        )
    }

    @Test
    fun mockBlocTestWithInitialState() = runBlocking {
        val bloc = mockBloc<CounterBloc, CounterEvent, Int>()

        whenListen(bloc, flowOf(0, 1, 2), initialState = 2)

        assertEquals(2, bloc.state)

        testBloc(
            build = { bloc },
            expected = listOf(
                { equals(0) },
                { equals(1) },
                { equals(2) },
            ),
        )

        assertEquals(2, bloc.state)
    }
}
