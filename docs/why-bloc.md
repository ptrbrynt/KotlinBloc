# Why Bloc?

> Bloc makes it easy to separate presentation from business logic, making your code *fast, easy to test, and reusable*.

When building production-quality apps, managing state is critical.

As developers, we want to:

* know what state our application is in at any point in time;
* easily test every case to make sure our app is responding appropriately;
* record every single user interaction in our application so we can make data-driven decisions;
* work as efficiently as possible and reuse components both within our application and across applications;
* have many developers seamlessly working within a single codebase, following the same patterns and conventions;
* develop fast and reactive apps.

Bloc was designed to meet all these needs and more.

There are many state management solutions and deciding which to use can be a daunting task. There is no perfect state management solution! What's important is that you pick the one that's best for your team and your project.

Bloc was designed with three core values in mind.

* **Simple**: Easy to understand, and can be used by developers with varying skill levels;
* **Powerful**: Help make amazing, complex applications by composing them from smaller components;
* **Testable**: Easily test every aspect of your app so you can iterate with confidence.

Overall, Bloc attempts to make state changes predictable by regulating when a state change can occur, and enforcing a single way to change state throughout an app.

## Bloc vs. ViewModel

MVVM has been the default state management pattern on Android for years, thanks to Google's official Lifecycle packages which included lifecycle-aware ViewModel and LiveData components.

But Jetpack Compose has no notion of the application lifecycle, which makes one of `androidx.lifecycle.ViewModel`'s main benefits moot. 

Of course, Compose still has full support for using `androidx.lifecycle` components. But thanks to the fundamentally new architecture of Compose, there's no inherent benefit to using them. This means that you can choose whichever pattern you prefer, or whichever works best for your project and team.

Let's consider our Counter example. Here's what it looks like in Bloc:

```kotlin
sealed class CounterEvent

object CounterIncremented: CounterEvent()

class CounterBloc: Bloc<CounterEvent, Int>(0) {
  init {
    on<CounterIncremented> {
      emit(state + 1)
    }
  }
}
```

And here's an equivalent ViewModel:

```kotlin
class CounterViewModel: ViewModel() {
  val counter = MutableLiveData<Int>(0)
  
  fun increment() {
    counter.postValue(counter.value!! + 1)
  }
}
```

The Bloc has a couple of advantages:

* The implementation enforces the use of a single type to represent the state; in this case, and `Int`. This is a great way of making sure you're following the single-responsibility principle of [SOLID](https://en.wikipedia.org/wiki/SOLID) object-oriented programming: the Bloc can only be used to represent one aspect of the application's state. By contrast, the ViewModel is fully open to being bloated with other values, which dilutes its ability to have a single responsibility.
* The Bloc is easier to instantiate: it doesn't require any special factories as it doesn't depend on anything from the Android framework.
* The Bloc is also cross-platform; you can use it in any Kotlin code, whereas the ViewModel is Android-only due to its dependency on the Android framework.
* Unlike ViewModels, which tend to represent all UI elements on a particular screen, Blocs can be totally separated from your UI and instead represent the state of a particular piece of data. It's much easier to use a Bloc across multiple UI elements than it is to share a ViewModel.

The ViewModel also has some advantages:

* It's much more familiar to a lot of Android developers, which may be an advantage if you're working on a team.
* It's backwards-compatible with legacy Android UI (Activities and Fragments), making it much easier to use in legacy codebases which are slowly moving towards Compose
* It's flexible, and allows developers to make their own rules about how they want to implement state management.
* There is a little less boilerplate

Of course, we musn't forget that Cubit is also an option, and provides many of the benefits of Bloc without the associated boilerplate:

```kotlin
class CounterCubit: Cubit<Int>(0) {
  suspend fun increment = emit(state + 1)
}
```

For simple states like the Counter, `Cubit` is probably the best of both worlds!