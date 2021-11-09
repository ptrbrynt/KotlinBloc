package com.ptrbrynt.kotlin_bloc.sample

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ptrbrynt.kotlin_bloc.sample.ui.blocs.CounterBloc
import com.ptrbrynt.kotlin_bloc.sample.ui.blocs.CounterEvent
import com.ptrbrynt.kotlin_bloc.sample.ui.cubits.CounterCubit
import com.ptrbrynt.kotlin_bloc.test.mockBloc
import com.ptrbrynt.kotlin_bloc.test.mockCubit
import com.ptrbrynt.kotlin_bloc.test.whenListen
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composableTestRule = createComposeRule()

    @Test
    fun blocCounterDisplaysBlocState() {
        val bloc = mockBloc<CounterBloc, CounterEvent, Int, Unit>()

        val flow = MutableStateFlow(1)

        whenListen(bloc, flow, initialState = flow.value)

        composableTestRule.setContent {
            MaterialTheme {
                BlocCounter(bloc = bloc)
            }
        }

        composableTestRule.onNodeWithText("1").assertIsDisplayed()

        flow.tryEmit(2)

        composableTestRule.onNodeWithText("2").assertIsDisplayed()
    }

    @Test
    fun blocCounterIncrementButtonAddsIncrement() {
        val bloc = mockBloc<CounterBloc, CounterEvent, Int, Unit>()

        whenListen(bloc, emptyFlow(), initialState = 1)

        composableTestRule.setContent {
            MaterialTheme {
                BlocCounter(bloc = bloc)
            }
        }

        composableTestRule.onNodeWithContentDescription("Add").performClick()

        verify { bloc.add(CounterEvent.Increment) }
    }

    @Test
    fun cubitCounterDisplaysBlocState() {
        val cubit = mockCubit<CounterCubit, Int, Unit>()

        val flow = MutableStateFlow(1)

        whenListen(cubit, flow, initialState = flow.value)

        composableTestRule.setContent {
            MaterialTheme {
                CubitCounter(cubit = cubit)
            }
        }

        composableTestRule.onNodeWithText("1").assertIsDisplayed()

        flow.tryEmit(2)

        composableTestRule.onNodeWithText("2").assertIsDisplayed()
    }

    @Test
    fun cubitCounterIncrementButtonCallsIncrement() {
        val cubit = mockCubit<CounterCubit, Int, Unit>()

        whenListen(cubit, emptyFlow(), initialState = 1)

        composableTestRule.setContent {
            MaterialTheme {
                CubitCounter(cubit = cubit)
            }
        }

        composableTestRule.onNodeWithContentDescription("Add").performClick()

        verify { cubit.increment() }
    }

    @Test
    fun blocSelectorCounterDisplaysBlocState() {
        val bloc = mockBloc<CounterBloc, CounterEvent, Int, Unit>()

        val flow = MutableStateFlow(1)

        whenListen(bloc, flow, initialState = flow.value)

        composableTestRule.setContent {
            MaterialTheme {
                BlocSelectorCounter(bloc = bloc)
            }
        }

        composableTestRule.onNodeWithText("The counter is 1").assertIsDisplayed()

        flow.tryEmit(2)

        composableTestRule.onNodeWithText("The counter is 2").assertIsDisplayed()
    }

    @Test
    fun blocSelectorCounterIncrementButtonAddsIncrement() {
        val bloc = mockBloc<CounterBloc, CounterEvent, Int, Unit>()

        whenListen(bloc, emptyFlow(), initialState = 1)

        composableTestRule.setContent {
            MaterialTheme {
                BlocSelectorCounter(bloc = bloc)
            }
        }

        composableTestRule.onNodeWithContentDescription("Add").performClick()

        verify { bloc.add(CounterEvent.Increment) }
    }
}
