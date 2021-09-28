package com.ptrbrynt.kotlin_bloc.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ptrbrynt.kotlin_bloc.compose.blocs.CounterBloc
import com.ptrbrynt.kotlin_bloc.compose.blocs.CounterEvent
import kotlinx.coroutines.flow.filter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BlocComposerTest {

    @get:Rule
    val composableTestRule = createComposeRule()

    @Test
    fun blocComposerStateDisplayTest() {
        val bloc = CounterBloc()

        composableTestRule.setContent {
            MaterialTheme {
                Scaffold {
                    BlocComposer(bloc) {
                        Text("$it")
                    }
                }
            }
        }

        composableTestRule.onNodeWithText("0").assertIsDisplayed()

        bloc.add(CounterEvent.Increment)

        composableTestRule.onNodeWithText("1").assertIsDisplayed()
    }

    @Test
    fun blocComposerTransformTest() {
        val bloc = CounterBloc()

        composableTestRule.setContent {
            MaterialTheme {
                Scaffold {
                    BlocComposer(
                        bloc,
                        transformStates = { filter { it % 2 == 0 } }
                    ) {
                        Text("$it")
                    }
                }
            }
        }

        composableTestRule.onNodeWithText("0").assertIsDisplayed()

        bloc.add(CounterEvent.Increment)

        composableTestRule.onNodeWithText("0").assertIsDisplayed()

        bloc.add(CounterEvent.Increment)

        composableTestRule.onNodeWithText("2").assertIsDisplayed()
    }
}
