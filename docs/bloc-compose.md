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

For fine-grained control over when the `content` function is re-composed, you can add a `composeWhen`. This takes the new state and returns a boolean. If `composeWhen` returns true, the `content` will be updated with the new state. If `composeWhen` returns false, the `content` will not receive the new state.

```kotlin
val bloc = remember { CounterBloc() }

BlocComposer(
  bloc,
  // Only compose for even numbers
  composeWhen = { state -> state % 2 == 0 },
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

For fine-grained control over when the `onState` callback is invoked, an optional `reactWhen` can be provided. This takes the new state and returns a boolean. If `reactWhen` returns true, `onState` will be invoked with the new state. If `reactWhen` returns false, the new state will be ignored.

```kotlin
val bloc = remember { CounterBloc() }

BlocListener(
  bloc,
  // Only react to even numbers
  reactWhen = { state -> state % 2 == 0 },
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
  override fun mapEventToState(event: CounterEvent) = flow {
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