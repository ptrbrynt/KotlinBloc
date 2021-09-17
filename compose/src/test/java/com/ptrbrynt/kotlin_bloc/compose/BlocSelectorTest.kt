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
import kotlinx.coroutines.FlowPreview
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@FlowPreview
@RunWith(AndroidJUnit4::class)
class BlocSelectorTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun blocSelectorTest() {
        val bloc = CounterBloc()

        rule.setContent {
            MaterialTheme {
                Scaffold {
                    BlocSelector(
                        bloc = bloc,
                        selector = {
                            "$it"
                        }
                    ) {
                        Text(it)
                    }
                }
            }
        }

        rule.onNodeWithText("0").assertIsDisplayed()

        bloc.add(CounterEvent.Increment)

        rule.onNodeWithText("1").assertIsDisplayed()
    }
}
