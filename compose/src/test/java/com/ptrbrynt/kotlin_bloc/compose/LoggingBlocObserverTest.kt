package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ptrbrynt.kotlin_bloc.compose.blocs.CounterBloc
import com.ptrbrynt.kotlin_bloc.compose.blocs.CounterEvent
import com.ptrbrynt.kotlin_bloc.core.Bloc
import io.mockk.mockk
import io.mockk.verifyOrder
import java.io.PrintStream
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(AndroidJUnit4::class)
@Config(shadows = [ShadowLog::class])
class LoggingBlocObserverTest {

    @get:Rule
    val composableTestRule = createComposeRule()

    @Test
    fun `LoggingBlocObserver prints correct data`() {
        val stream = mockk<PrintStream>(relaxed = true)

        ShadowLog.stream = stream

        Bloc.observer = LoggingBlocObserver()

        val bloc = CounterBloc()

        bloc.add(CounterEvent.Increment)

        verifyOrder {
            stream.println("I/CounterBloc: Created")
            stream.println("I/CounterBloc: Increment")
            stream.println("I/CounterBloc: Change(state=0, newState=1)")
            stream.println("I/CounterBloc: Transition(state=0, event=Increment, newState=1)")
            stream.println("I/CounterBloc: 1")
        }
    }
}
