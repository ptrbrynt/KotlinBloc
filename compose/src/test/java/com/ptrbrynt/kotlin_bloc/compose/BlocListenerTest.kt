package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ptrbrynt.kotlin_bloc.compose.blocs.CounterBloc
import com.ptrbrynt.kotlin_bloc.compose.blocs.CounterEvent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BlocListenerTest {
    @get:Rule
    val composableTestRule = createComposeRule()

    @Test
    fun blocListenerTest() {
        val bloc = CounterBloc()

        composableTestRule.setContent {
            MaterialTheme {
                val scaffoldState = rememberScaffoldState()
                Scaffold(
                    scaffoldState = scaffoldState,
                ) {
                    BlocListener(bloc) {
                        scaffoldState.snackbarHostState.showSnackbar("$it")
                    }
                }
            }
        }

        // Should not react to initial state
        composableTestRule.onNodeWithText("0").assertDoesNotExist()

        bloc.add(CounterEvent.Increment)

        composableTestRule.onNodeWithText("1").assertIsDisplayed()
    }
}
