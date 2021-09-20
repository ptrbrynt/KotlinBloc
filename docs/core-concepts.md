# Core Concepts

## Kotlin Flow

?> Check out the [official Flow documentation](https://kotlinlang.org/docs/flow.html) for more information about Flow.

> A Flow is a stream of asynchronously computed values.

In order to use the bloc library, it's critical to have a basic understanding of how Kotlin Flow works.

> If you're unfamiliar with Flow, just think of a flow as a pipe with water flowing through it. The pipe is the Flow, and the water is the asynchronous data.

We can create a Flow in Kotlin using the flow builder syntax:

```kotlin
val countFlow(max: Int): Flow<Int> = flow {
  for (i in 0..max) {
    emit(i)
  }
}
```

By using the `flow` builder, we are able to use the `emit` keyword and return a `Flow` of data. In the above example, we are returning a `Flow` of integers up to the `max` integer parameter.

Every time we `emit` in a `flow`, we are pushing that piece of data through the `Flow`.

We can consume the above `Flow` in several ways. If we wanted to write a function to print each value emitted by a `Flow` to the console, it would look something like this:

```kotlin
suspend fun printFlow(flow: Flow<Int>) {
  flow.collect {
    println("$it")
  }
}
```

*Notice that this is a `suspend fun`, since the `collect` method we are using is also a `suspend fun`.*

## Cubit

> A `Cubit` is a class which extends `BlocBase` and can be extended to manage any type of state.

![Cubit Architecture](cubit_architecture_full.png)

A `Cubit` can expose functions which can be called to trigger state changes.

> States are the ouput of a `Cubit` and represent part of your application's state. UI components can be notified of states and redraw portions of themselves based on the current state.

### Creating a Cubit

We can create a `CounterCubit` like this:

```kotlin
class CounterCubit: Cubit<Int>(0)
```

When creating a `Cubit`, we need to define the type of state which the `Cubit` will be managing. In the case of the `CounterCubit` above, the state can be represented via an `Int`, but in more complex cases it may be necessary to use a `class` instead of a primitive type.

The second thing we need to do when creating a `Cubit` is specify the initial state. We do this by passing the initial state into the constructor of the `Cubit` subclass. In the snippet above, we are setting the initial state to `0` internally, but we could also allow the `CounterCubit` to be more flexible by accepting a value in its own constructor:

```kotlin
class CounterCubit(initial: Int): Cubit<Int>(initial)
```

This would allow us to instantiate different `CounterCubit` instances with different initial states, like this:

```kotlin
val cubitA = CounterCubit(0) // State starts at 0
val cubitB = CounterCubit(10) // State starts at 10
```

### State Changes

> Each `Cubit` has the ability to output a new state via `emit`.

```kotlin
class CounterCubit : Cubit<Int>(0) {
  fun increment() = emit(state + 1)
}
```

In the above snippet, the `CounterCubit` is exposing a public method called increment which can be called externally to notify the `CounterCubit` to increment its state. When `increment` is called, we can access the current state of the `Cubit` via the `state` getter and `emit` a new state by adding one to the current state.

!> The `emit` method is protected, meaning it should only be used inside of a `Cubit`.

### Using a Cubit

#### Basic Usage

```kotlin
fun main() {
  val cubit = CounterCubit()
  println(cubit.state) // 0
  cubit.increment()
  println(cubit.state) // 1
}
```

In the above snippet, we start by creating an instance of the `CounterCubit`. We then print the current state of the cubit which is the initial state (since no new states have been emitted yet). Next, we call the `increment` method to trigger a state change. Finally, we print the state of the `Cubit` again which went from `0` to `1`.

#### Flow Usage

We can subscribe to a `Cubit`'s state flow for real-time updates to its state:

```kotlin
suspend fun main() {
  val cubit = CounterCubit()
  scope.launch {
    cubit.stateFlow.collect { print(it) }
  }
  cubit.increment()
}
```

In the above snippet, we are `collect`ing the `CounterCubit`'s `stateFlow` and calling `println` on each state change. We are then invoking the `increment` function which will emit a new state.

!> Only subsequent state changes will be received when calling `collect` on a `Cubit`'s `stateFlow`.

### Observing a Cubit

> When a `Cubit` emits a new state, a `Change` occurs. We can observe all changes for a `Cubit` by overriding `onChange`.

```kotlin
class CounterCubit: Cubit<Int>(0) {
  fun increment() = emit(state + 1)
  
  @override
  void onChange(change: Change<Int>) {
    super.onChange(change)
    println(change)
  }
}
```

We can then interact with the `Cubit` and observe all changes printed to the console.

```kotlin
fun main() {
  CounterCubit().apply {
    increment()
  }
}
```

?> **Note**: A `Change` occurs just before the state of the `Cubit` is updated.

## Bloc

> A `Bloc` is a more advanced class which relies on `event`s to trigger `state` changes, rather than functions. `Bloc` also extends `BlocBase` which means it has a similar public API to `Cubit`. However, rather than calling a `function` on a `Bloc` and directly emitting a new `state`, `Bloc`s receive `event`s and convert them into outgoing `state`s.

![bloc_architecture_full](/Volumes/T7/Projects/KotlinBloc/docs/bloc_architecture_full.png)

### Creating a Bloc

Creating a `Bloc` is similar to creating a `Cubit` except, in addition to defining the state we'll be managin, we must also define the event type that the `Bloc` will be able to process.

> Events are input to a Bloc. They are commonly added in response to user interactions such as button presses, or lifecycle events like page loads.

```kotlin
enum class CounterEvent { Increment }

class CounterBloc: Bloc<CounterEvent, Int>(0) {
  // ...
}
```

Just like when creating the `CounterCubit`, we must specify an initial state by passing it to the superclass constructor.

### State Changes

Using `Bloc` requires us to override the `mapEventToState` method. This will be responsible for converting any incoming events into zero or more outgoing states.

```kotlin
enum class CounterEvent { Increment }

class CounterBloc: Bloc<CounterEvent, Int>(0) {
  override fun mapEventToState(event: CounterEvent): Flow<Int> = flow {
    
  }
}
```

> **Tip**: Notice we're using the `flow` builder syntax.

We can then update `mapEventToState` to handle the `CounterEvent.Increment` event:

```kotlin
enum class CounterEvent { Increment }

class CounterBloc: Bloc<CounterEvent, Int>(0) {
  override fun mapEventToState(event: CounterEvent): Flow<Int> = flow {
    when (event) {
      CounterEvent.Increment -> emit(state + 1)
    }
  }
}
```

In the above snippet, we are using a `when` statement to check the type of `event` we're handling. If it's an `Increment` event, we are `emit`ting a new state.

> **Note**: The `emit` method we're calling here is the standard `emit` method provided by the `flow` builder syntax.

!> `Bloc`s can't directly `emit` new states. Instead, every state change must take place in response to an incoming event as part of the `mapEventToState` method.

### Using a Bloc

At this point, we can create a new instance of our `CounterBloc` and put it to use!

#### Basic Usage

```kotlin
suspend fun main() {
  val bloc = CounterBloc()
  println(bloc.state) // 0
  bloc.add(CounterEvent.Increment)
  delay(100)
  print(bloc.state) // 1
}
```

In the above snippet, we start by creating a new instance of [CounterBloc]. We then print the current state of the `Bloc` which is the initial state. Next, we add the `Increment` event to trigger a state change. Finally, we print the state of the `Bloc` again, which has now changed to `1`.

#### Flow Usage

Just like `Cubit`, `Bloc` provides the ability to receive a `Flow` of `state`s.

```kotlin
suspend fun main() {
  val bloc = CounterBloc()
  scope.launch {
    bloc.collect { println(it) }
  }
  bloc.add(CounterEvent.Increment)
}
```

## BlocComposer

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

## BlocListener

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

