# Counter Tutorial

> In this tutorial, we'll build a Counter in Android using Compose and the Bloc library.

![Counter](counter.gif ':size=33%')

## Key Topics

* Observe state changes with [BlocObserver](../core-concepts?id=blocobserver)
* [BlocComposer](../bloc-compose.md?id=bloccomposer), which handles re-composing a widget in response to new states
* Using Cubit instead of Bloc
* Adding events

## Setup

Start by [creating a new Android Studio project with Compose enabled](https://developer.android.com/jetpack/compose/setup#create-new).

Then add the [Jitpack](https://jitpack.io) repository to your project-level `settings.gradle` file:

```groovy
repositories {
  // ...
  maven { url 'https://jitpack.io' }
}
```

Finally, just add the following dependency to your module-level `build.gradle` file:

```groovy
dependencies {
  // ...
	implementation 'com.github.ptrbrynt.KotlinBloc:compose:0.9'
}
```

Re-sync your project in Android Studio, and you'll be good to go!

## BlocObserver

The first thing we're going to look at is how to create a `BlocObserver` which will help us observe all state changes.

Let's create a new class called `CounterObserver`:

```kotlin
/**
 * [BlocObserver] for the counter application which observers all state changes
 */

class CounterObserver : BlocObserver() {
    override fun <B : BlocBase<State>, State> onChange(bloc: B, change: Change<State>) {
        super.onChange(bloc, change)
        Log.i(bloc::class.simpleName, "$change")
    }
}
```

In this case, we're only overriding `onChange` to see all state changes that occur.

> **Note**: `onChange` works the same for both `Bloc`s and `Cubit`s

## MainActivity

Next, let's set the global Bloc Observer to be the one we just implemented. In the `onCreate` method of  `MainActivity.kt`, just before the `setContent` call, add this:

```kotlin
Bloc.observer = CounterObserver()
```

## CounterCubit

Let's now create a `CounterCubit.kt` file, and implement our cubit class.

It will expose an `increment` method which will add `1` to the current state.

The type of state the `CounterCubit` is managing will just be an `Int`, and the initial state will be `0`.

```kotlin
class CounterCubit : Cubit<Int>(0) {
  suspend fun increment() = emit(state + 1)
}
```

## Counter Composable

Let's create a new file called `Counter.kt`. This will contain our composable functions.

```kotlin
/**
 * A widget which reacts to the provided [CounterCubit] and notifies it in response to user input.
 */

@Composable
fun Counter(
  scope: CoroutineScope = rememberCoroutineScope(),
  cubit: CounterCubit = remember { CounterCubit() },
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counter") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                  scope.launch { cubit.increment() } 
                },
            ) {
                Icon(Icons.Default.Add, "Increment")
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            BlocComposer(bloc = cubit) {
                Text("$it", style = MaterialTheme.typography.h4)
            }
        }
    }
}

@Preview

@Composable
fun CounterPreview() {
    Counter()
}
```

A `BlocComposer` is used to wrap the `Text` widget, in order to update the text any time the `CounterCubit` state changes. In addition, the `FloatingActionButton`'s `onClick` callback is implemented to invoke the `increment` method on the `CounterCubit`.

!> **Note**: We are injecting `CounterCubit` as a parameter for the composable function, with a default value. This makes testing much easier as we can inject a mocked version of `CounterCubit` in our tests.

## MainActivity (again)

Let's go back to our `MainActivity` class, and remove all the example code. Then, within your theme composable, add the `Counter` widget.

```kotlin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Bloc.observer = CounterObserver()
        setContent {
            CounterTheme {
                Counter()
            }
        }
    }
}
```

## Testing

?> There's a comprehensive tutorial for testing Compose apps [here](https://developer.android.com/jetpack/compose/testing).

### Testing the Cubit

We can write a set of simple unit tests to verify the behavior of the `CounterCubit`. Create a new `CounterCubitTest.kt` file in the `test` source set, with the following contents:

```kotlin

class CounterCubitTest {
    @Test
    fun initialState() {
        val cubit = CounterCubit()

        assertEquals(0, cubit.state)
    }

    @Test
    fun increment() = runBlocking {
        val cubit = CounterCubit()

        cubit.increment()

        assertEquals(1, cubit.state)
    }
}
```

The first test verifies the initial state of the cubit, which should be `0`. The second test then ensures that the `increment` method does in fact increment the state of the cubit.

### Testing the Counter widget

First, ensure you have the following dependencies in your `build.gradle` file:

```groovy
androidTestImplementation "androidx.compose.ui:ui-test-junit4:$compose_version"
debugImplementation "androidx.compose.ui:ui-test-manifest:$compose_version"
```

Then, in the `androidTest` source set, create a `CounterTest.kt` file with the following contents:

```kotlin

class CounterTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun counterTest() {
        rule.setContent {
            CounterTheme {
                Counter()
            }
        }

        rule.onNodeWithText("0").assertIsDisplayed()

        rule.onNodeWithContentDescription("Increment").performClick()

        rule.onNodeWithText("1").assertIsDisplayed()
    }
}
```

This test runs on a real device or emulator, and verifies that the Counter widget is behaving as expected.

#### One step further: Mocking CounterCubit

If we wanted to decouple our widget test from our `CounterCubit` implementation, we could use something like the [`mockk`](https://mockk.io) framework to create a fake version of `CounterCubit` for use in this test.

```kotlin

class CounterTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun counterDisplaysCurrentStateOfCubit() {
        val cubit = mockk<CounterCubit>(relaxed = true)

        every { cubit.state } returns 3

        rule.setContent {
            CounterTheme {
                Counter(cubit = cubit)
            }
        }

        rule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun addButtonIncrementsCounter() {
        val cubit = mockk<CounterCubit>(relaxed = true)

        every { cubit.state } returns 0

        rule.setContent {
            CounterTheme {
                Counter(cubit = cubit)
            }
        }

        rule.onNodeWithContentDescription("Increment").performClick()

        coVerify {
            cubit.increment()
        }
    }
}
```

