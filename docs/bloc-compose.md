# Bloc + Compose

?> The classes described on this page are part of the `compose` library, designed for use in Android apps which use [Jetpack Compose](https://developer.android.com/jetpack/compose).

## Bloc Composables

### BlocComposer

`BlocComposer` is a composable which takes a `bloc` and a `content` composable. `BlocComposer` handles composing the content in response to new states.

?> See `BlocListener` if you want to "do" anything in response to state changes (e.g. navigation, showing a dialog etc)

```kotlin
val bloc = remember { CounterBloc() }
BlocComposer(bloc) { state ->
  // Compose views here based on the current state
}
```

For fine-grained control over when the `content` function is re-composed, you can add a `transformStates`. This allows you to transform the flow of states emitted by the `bloc`. For example, you could filter out certain states, or debounce the flow to prevent state changes from happening too quickly.

```kotlin
val bloc = remember { CounterBloc() }

BlocComposer(
  bloc,
  // Only compose for even numbers, at most once per second
  transformStates = { this.filter { it % 2 == 0 }.debounce(1000) },
) { state ->
  // Compose views here based on the current state
}
```

### BlocListener

`BlocListener` is a composable which takes a `bloc` and an `onState` callback. `onState` is invoked whenever the `bloc` emits a new state.

`BlocListener` should be used to handle [side-effects](https://developer.android.com/jetpack/compose/side-effects) e.g. showing a dialog, or navigation.

```kotlin
val bloc = remember { CounterBloc() }

BlocListener(bloc) { state -> 
  // Do something here based on the bloc state
}
```

For fine-grained control over when the `onState` callback is invoked, you can add a `transformStates`. This allows you to transform the flow of states emitted by the `bloc`. For example, you could filter out certain states, or debounce the flow to prevent state changes from happening too quickly.

```kotlin
val bloc = remember { CounterBloc() }

BlocListener(
  bloc,
  // Only react to even numbers, at most once per second
  transformStates = { this.filter { it % 2 == 0 }.debounce(1000) },
) { state -> 
  // Do something here based on the bloc state
}
```

## Usage

Let's take a look at how to use `BlocComposer` to hook up a `Counter` widget to a `CounterBloc`.

### CounterBloc

```kotlin
enum class CounterEvent { Incremented }

class CounterBloc: Bloc<CounterEvent, Int>(0) {
  override suspend fun Emitter<Int>.mapEventToState(event: CounterEvent) {
    when (event) {
      is CounterEvent.Incremented -> emit(state + 1)
    }
  }
}
```

### Counter

```kotlin
@Composable
fun Counter() {
  val bloc = remember { CounterBloc() }
  
  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
      	onClick = { bloc.add(CounterEvent.Incremented) }
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
      BlocComposer(bloc = bloc) { state ->
        Text("$state", style = MaterialTheme.typography.h5)
      }
    }
  }
}
```

At this point, we have successfully separated our presentation layer from our business logic layer. Notice that the `Counter` composable knows nothing about what happens when a user taps the buttons. The widget simply tells the `CounterBloc` that the user has pressed the Increment button.