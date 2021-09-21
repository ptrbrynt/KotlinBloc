# Naming Conventions

!> The following conventions are recommendations; as such they're completely optional. Feel free to use whatever naming conventions you prefer!

## Event Conventions

> Events should be named in the **past tense**, because events are things that have already occurred from the `Bloc`'s perspective.

### Anatomy

`BlocSubject` + `Noun (optional)` + `Verb (event)`

> Initial load events should follow the convention: `BlocSubject` + `Started`

### Examples

#### ✅ Good

`CounterStarted` `CounterIncremented` `CounterDecremented` `CounterIncrementRetried`

#### ❌ Bad

`Initial` `CounterInitialized` `Increment` `DoIncrement` `IncrementCounter`

## State Conventions

> States should be nouns, because a state is just a snapshot at a particular point in time.

### Anatomy

`BlocSubject` + `Verb (action)` + `State`

> State should be one of the following: `Initial` | `Success` | `Failure` | `InProgress` and initial states should follow the convention: `BlocSubject` + `Initial`.

### Examples

#### ✅ Good

`CounterInitial` `CounterLoadInProgress` `CounterLoadSuccess` `CounterLoadFailure`

#### ❌ Bad

`Initial` `Loading` `Success` `Succeeded` `Loaded` `Failure` `Failed`

## Avoiding Tautology

> If you're using something like an `enum class` to represent states or events, it's better to avoid tautology (repetition) in your naming, to avoid repetition at call sites.

#### ✅ Good

```kotlin
// Creating a CounterEvent looks like: CounterEvent.Incremented
enum class CounterEvent { Incremented, Decremented }
```

#### ❌ Bad

```kotlin
// Creating a CounterEvent looks like: CounterEvent.CounterIncremented
enum class CounterEvent { CounterIncremented, CounterDecremented }
```



