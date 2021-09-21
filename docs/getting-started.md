# Getting Started

## Overview

Kotlin Bloc consists of two packages:

* core - A pure Kotlin library providing the `Bloc` and `Cubit` classes
* compose - An Android library which allows you to integrate `Bloc` and `Cubit` into your Compose
  apps

## Installation

Kotlin Bloc is available on [Jitpack](https://jitpack.io).

First, add the Jitpack repository to your top-level `build.gradle` file:

```groovy
allprojects {
  repositories {
    // ...
    maven { url 'https://jitpack.io' }
  }
}
```

Then add the dependency to your module-level `build.gradle` file. 

!> Note: you only need one of the two available dependencies. The `compose` library includes the `core` library.

```groovy
dependencies {
  // ...

  // Choose EITHER:
  implementation 'com.github.ptrbrynt.KotlinBloc:compose:0.9' // For Jetpack Compose apps
  implementation 'com.github.ptrbrynt.KotlinBloc:core:0.9' // The pure Kotlin library, for other stuff
}
```
