# Core Concepts

?> The concepts and classes described on this page apply to the `core` and `compose` libraries, and can be used in any Kotlin application.

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
  suspend fun increment() = emit(state + 1)
}
```

In the above snippet, the `CounterCubit` is exposing a public method called increment which can be called externally to notify the `CounterCubit` to increment its state. When `increment` is called, we can access the current state of the `Cubit` via the `state` getter and `emit` a new state by adding one to the current state.

!> The `emit` method is protected, meaning it should only be used inside of a `Cubit`.

### Using a Cubit

#### Basic Usage

```kotlin
suspend fun main() {
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
  suspend fun increment() = emit(state + 1)
  
  @override
  void onChange(change: Change<Int>) {
    super.onChange(change)
    println(change)
  }
}
```

We can then interact with the `Cubit` and observe all changes printed to the console.

```kotlin
suspend fun main() {
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
enum class CounterEvent { Incremented }

class CounterBloc: Bloc<CounterEvent, Int>(0) {
  // ...
}
```

Just like when creating the `CounterCubit`, we must specify an initial state by passing it to the superclass constructor.

### State Changes

Using `Bloc` requires us to override the `mapEventToState` method. This will be responsible for converting any incoming events into zero or more outgoing states.

```kotlin
enum class CounterEvent { Incremented }

class CounterBloc: Bloc<CounterEvent, Int>(0) {
  override suspend fun Emitter<Int>.mapEventToState(event: CounterEvent) {
    
  }
}
```

?> The `mapEventToState` method receives an `Emitter`, which provides the `emit` and `emitEach` methods we'll use below. This means that, while a `Cubit` can `emit` from anywhere, a `Bloc` can only `emit` from within the `mapEventToState` method.

We can then update `mapEventToState` to handle the `CounterEvent.Incremented` event:

```kotlin
enum class CounterEvent { Incremented }

class CounterBloc: Bloc<CounterEvent, Int>(0) {
  override suspend fun Emitter<Int>.mapEventToState(event: CounterEvent) {
    when (event) {
      CounterEvent.Incremented -> emit(state + 1)
    }
  }
}
```

In the above snippet, we are using a `when` statement to check the type of `event` we're handling. If it's an `Incremented` event, we are `emit`ting a new state.

### Using a Bloc

At this point, we can create a new instance of our `CounterBloc` and put it to use!

#### Basic Usage

```kotlin
suspend fun main() {
  val bloc = CounterBloc()
  println(bloc.state) // 0
  bloc.add(CounterEvent.Incremented)
  delay(100)
  print(bloc.state) // 1
}
```

In the above snippet, we start by creating a new instance of `CounterBloc`. We then print the current state of the `Bloc` which is the initial state. Next, we add the `Incremented` event to trigger a state change. Finally, we print the state of the `Bloc` again, which has now changed to `1`.

#### Flow Usage

Just like `Cubit`, `Bloc` provides the ability to receive a `Flow` of `state`s.

```kotlin
suspend fun main() {
  val bloc = CounterBloc()
  scope.launch {
    bloc.collect { println(it) }
  }
  bloc.add(CounterEvent.Incremented)
}
```

## BlocObserver

Most applications will include a potentially large number of `Bloc`s and `Cubit`s. Implementing observation on every one of these classes individually can be cumbserome, especially if you want to do the same thing for each `Bloc` and `Cubit`.

To that end, the library includes a way of creating a global `BlocObserver`. 

```kotlin
/**
 * A [BlocObserver] which logs all bloc events to the console.
 */
class LoggingBlocObserver : BlocObserver() {

    override fun <B : BlocBase<State>, State> onCreate(bloc: B) {
        super.onCreate(bloc)
        Log.i(bloc::class.simpleName, "Created")
    }

    override fun <B : BlocBase<State>, State> onChange(bloc: B, change: Change<State>) {
        super.onChange(bloc, change)
        Log.i(bloc::class.simpleName, change.toString())
    }

    override fun <B : Bloc<Event, State>, Event, State> onEvent(bloc: B, event: Event) {
        super.onEvent(bloc, event)
        Log.i(bloc::class.simpleName, event.toString())
    }

    override fun <B : Bloc<Event, State>, Event, State> onTransition(
        bloc: B,
        transition: Transition<Event, State>,
    ) {
        super.onTransition(bloc, transition)
        Log.i(bloc::class.simpleName, transition.toString())
    }
}
```

?> All methods in `BlocObserver` subclasses are optional; if you omit a particular handler method, the observer will do nothing.

?> This implementation of `LoggingBlocObserver` is actually available as part of the `compose` library. You're welcome 😊

To apply your global observer, add this code before you create and use any `Bloc`s or `Cubit`s:

```kotlin
Bloc.observer = LoggingBlocObserver()
```

If you want to stop logging at any point, you can set this to use `SilentBlocObserver`:

```kotlin
Bloc.observer = SilentBlocObserver()
```

## Cubit vs. Bloc

You might be wondering whether to use `Cubit` or `Bloc`. There are advantages to each!

### Cubit Advantages

#### Simplicity

One of the biggest advantages of using `Cubit` is simplicity. When creating a `Cubit`, we only have to define the state and the methods we want to expose to change the state. By comparison, when creating a `Bloc`, we have to define the states, events, and how they are mapped. This makes `Cubit` easier to understand and more concise.

Here's an example of a `CounterCubit`, and a `CounterBloc` with equivalent functionality.

##### CounterCubit

```kotlin
class CounterCubit : Cubit<Int>(0) {
  suspend fun increment = emit(state + 1)
}
```

##### CounterBloc

```kotlin
enum class CounterEvent { Increment }

class CounterBloc : Bloc<CounterEvent, Int>(0) {
  override suspend fun Emitter<Int>.mapEventToState(event: CounterEvent) {
    when (event) {
      is CounterEvent.Increment -> emit(state + 1)
    }
  }
}
```

The `Cubit` implementation is much more concise, and instead of defining events separately the methods act like events.

### Bloc Advantages

#### Traceability

One of the biggest advantages of `Bloc` is that you can track much more precisely the sequence of state changes as well as exactly what triggered those changes. For state that is critical to the functionality of an application, it might be beneficial to use a more event-driven approach in order to capture all events in addition to state changes.

A common use case might be managing `AuthenticationState`. In our example we'll represent this as an `enum class` for simplicity.

```kotlin
enum class AuthenticationState { Unknown, Authenticated, Unauthenticated }
```

There could be many reasons for the application state to change from `Authenticated` to `Unauthenticated`. The user might have tapped the logout button, or perhaps the user's access token was revoked. When using `Bloc`, we can clearly trace how the application ended up in a particular state.

```
Transition {
  state: AuthenticationState.Authenticated,
  event: LogoutRequested,
  nextState: AuthenticationState.Unauthenticated
}
```

The above `Transition` gives us all the information we need to understand why the state changed. By contrast, if we had just used a `Cubit`, our logs would only give us a `Change`:

```
Change {
  state: AuthenticationState.Authenticated,
  nextState: AuthenticationState.Unauthenticated
}
```

This tells us the user was logged out, but doesn't explain why that happened.

#### Advanced Reactive Operations

Another area in which `Bloc` excels over `Cubit` is when we need to take advantage of reactive operators such as `debounce` and `filter`. 

`Bloc` has an event sink which allows us to control and transform the incoming flow of events.

For example, if we were building real-time search, we would probably want to debounce the requests to the backend in order to avoid getting rate-limited, as well as to cut down on network requests.

With `Bloc`, we can override the `transformEvents` method to change the way incoming events are processed by the `Bloc`.

```kotlin
override fun Flow<CounterEvent>.transformEvents(): Flow<CounterEvent> {
  return onEach { delay(2000) }
}
```

?> As a general tip, if you're unsure about whether to use `Cubit` or `Bloc`, start with `Cubit`. You can always refactor if you need to scale up to `Bloc` later.
