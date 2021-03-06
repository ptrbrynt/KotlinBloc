package com.ptrbrynt.kotlin_bloc.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ptrbrynt.kotlin_bloc.compose.BlocComposer
import com.ptrbrynt.kotlin_bloc.compose.BlocSelector
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import com.ptrbrynt.kotlin_bloc.sample.ui.blocs.CounterBloc
import com.ptrbrynt.kotlin_bloc.sample.ui.blocs.CounterEvent
import com.ptrbrynt.kotlin_bloc.sample.ui.cubits.CounterCubit
import com.ptrbrynt.kotlin_bloc.sample.ui.theme.KotlinBlocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinBlocTheme {
                BlocCounter()
            }
        }
    }
}

/**
 * Creates a Counter based on [CounterBloc]
 */
@Composable
fun BlocCounter(
    bloc: CounterBloc = rememberSaveable { CounterBloc(0) },
) {
    CounterBase(
        bloc,
        onIncrement = {
            bloc.add(CounterEvent.Increment)
        }
    )
}

/**
 * Creates a Counter based on [CounterCubit]
 */
@Composable
fun CubitCounter(
    cubit: CounterCubit = remember { CounterCubit() },
) {
    CounterBase(
        cubit,
        onIncrement = { cubit.increment() }
    )
}

@Composable
fun CounterBase(
    bloc: BlocBase<Int>,
    onIncrement: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = onIncrement) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BlocComposer(bloc = bloc) {
                Text("$it", style = MaterialTheme.typography.h5)
            }
        }
    }
}

/**
 * Creates a Counter based on [CounterBloc], using [BlocSelector] to transform each state into a String to display.
 */

@Composable
fun BlocSelectorCounter(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    bloc: CounterBloc = rememberSaveable { CounterBloc(0) },
) {
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    bloc.add(CounterEvent.Increment)
                }
            ) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BlocSelector(
                bloc = bloc,
                selector = {
                    "The counter is $it"
                }
            ) {
                Text(it, style = MaterialTheme.typography.h5)
            }
        }
    }
}

@Preview("BlocCounter")
@Composable

fun BlocCounterPreview() {
    KotlinBlocTheme {
        BlocCounter()
    }
}

@Preview("CubitCounter")
@Composable

fun CubitCounterPreview() {
    KotlinBlocTheme {
        CubitCounter()
    }
}

@Preview("BlocSelectorCounter")
@Composable

fun BlocSelectorCounterPreview() {
    KotlinBlocTheme {
        BlocSelectorCounter()
    }
}
