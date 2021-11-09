# Bloc Test

The `test` package includes some utilities to help you test and mock Blocs and Cubits.

## `testBloc`

The `testBloc` method provides some syntactic sugar, allowing you to write readable tests for your Blocs and Cubits.

```kotlin
class CounterBlocTest {
  @Test
  fun counterBlocEmitsCorrectStates() = runBlocking {
    testBloc(
      // The build function should return an instance of the bloc you want to test
      build = { CounterBloc() },
      // Use the act function to perform operations on your bloc (usually adding events)
      act = {
        add(Incremented)
        add(Incremented)
        add(Decremented)
      },
      // The expected parameter should be a list of functions. Each function takes the next state
      // emitted and should return a boolean indicating whether that state is correct.
      expected = listOf(
        { equals(1) },
        { equals(2) },
        { equals(1) },
      ),
    )
  }
}
```

As well as the parameters shown above, `blocTest` also provides the following options:

* `setUp`: A function executed before `build` which can be used to set up dependencies or do any other initialization
* `tearDown`: A function executed as the final step of the test, which can be used to perform cleanup
* `skip`: An optional `Int` which indicates the number of states to ignore before beginning to make assertions
* `skipSideEffects`: An optional `Int` which indicates the number of side-effects to ignore before beginning to make assertions
* `expectedSideEffects`: Like `expected`, but for side-effects.
* `verify`: A function executed after the assertion step, which can be used to perform additional checks and verification

## `mockBloc`,  `mockCubit` and `whenListen`

You can use the `mockBloc` and `mockCubit` functions to create mock versions of your Blocs and Cubits. Under-the-hood, these methods use the popular [mockk](https://mockk.io) framework.

Mocking is useful if you want to test a class or function which depends on a Bloc or Cubit. With a mocked version, you can simulate the emission of a particular state or set of states.

The `whenListen` method is provided as a simple way of stubbing the state flow emitted by the mocked Bloc or Cubit.

```kotlin
class MyComposableTest {
  @Test
  fun testWithMockCubit() {
    val cubit = mockCubit<CounterCubit, Int, Unit>() // Here, Int represents the state type and Unit is the side-effect type
    
    whenListen(cubit, flowOf(1,2,3)) // This line sets up the cubit to emit the numbers 1, 2, and 3 in that order.
    
    // Do some testing here...
  }
  
  @Test
  fun testWithMockBloc() {
    val bloc = mockBloc<CounterBloc, CounterEvent, Int, String>()
    
    whenListen(bloc, flowOf(1,2,3), initialState = 0) // You can pass an optional initial state.
    
    whenListenToSideEffects(bloc, flowOf("hello")) // You can also stub side-effect emission
    
    // Do some testing...
  }
}
```

