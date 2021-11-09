package com.ptrbrynt.kotlin_bloc.test

import app.cash.turbine.test
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import kotlin.time.ExperimentalTime

typealias StateAssertion<State> = State.() -> Boolean
typealias SideEffectAssertion<SideEffect> = SideEffect.() -> Boolean

/**
 * Handles asserting that a `bloc` emits the [expected] states (in order) after [act] has been
 * executed.
 *
 * @param setUp Should be used to set up any dependencies prior to initializing the
 * `bloc` under test.
 *
 * @param build Should construct and return the `bloc` under test.
 *
 * @param act An optional callback which will be invoked with the `bloc` under test. It should be
 * used to interact with the `bloc`.
 *
 * @param skip An optional [Int] which can be used to skip any number of states. Defaults to `0`.
 *
 * @param skipSideEffects An optional [Int] which can be used to skip any number of side effects.
 * Defaults to `0`.
 *
 * @param expected A list of [StateAssertion]s which will be run on each newly emitted state in
 * order. Use this to check the correctness of each state emitted by the `bloc` under test.
 *
 * @param expectedSideEffects A list of [SideEffectAssertion]s which will be run on each emitted
 * side-effect in order. Use this to check the correctness of each side-effect emitted by the
 * `bloc` under test.
 *
 * @param verify An optional callback which is invoked after all [expected] states have been
 * emitted, and can be used for additional verification/assertions.
 *
 * @param tearDown Can be used to execute any code after the test has run.
 */
@ExperimentalTime
suspend fun <B : BlocBase<State, SideEffect>, State, SideEffect> testBloc(
    setUp: suspend () -> Unit = {},
    build: () -> B,
    act: suspend B.() -> Unit = {},
    skip: Int = 0,
    skipSideEffects: Int = 0,
    expected: List<StateAssertion<State>> = emptyList(),
    expectedSideEffects: List<SideEffectAssertion<SideEffect>> = emptyList(),
    verify: B.() -> Unit = {},
    tearDown: suspend () -> Unit = {},
) {
    assert(skip >= 0)
    assert(skipSideEffects >= 0)

    setUp()


    testStates(
        build = build,
        act = act,
        skip = skip,
        expected = expected,
        verify = verify,
    )

    testSideEffects(
        build = build,
        act = act,
        skip = skipSideEffects,
        expected = expectedSideEffects,
        verify = verify,
    )

    tearDown()
}

@ExperimentalTime
private suspend fun <B : BlocBase<State, SideEffect>, State, SideEffect> testStates(
    build: () -> B,
    act: suspend B.() -> Unit = {},
    skip: Int = 0,
    expected: List<StateAssertion<State>> = emptyList(),
    verify: B.() -> Unit = {},
) {
    val bloc = build()

    bloc.stateFlow.test {
        bloc.act()

        for (i in 0 until skip) {
            awaitItem()
        }

        for (assertion in expected) {
            val item = awaitItem()
            assert(assertion(item))
        }

        cancelAndIgnoreRemainingEvents()
    }

    bloc.verify()
}

@ExperimentalTime
private suspend fun <B : BlocBase<State, SideEffect>, State, SideEffect> testSideEffects(
    build: () -> B,
    act: suspend B.() -> Unit = {},
    skip: Int = 0,
    expected: List<SideEffectAssertion<SideEffect>> = emptyList(),
    verify: B.() -> Unit = {},
) {
    val bloc = build()

    bloc.sideEffectFlow.test {
        bloc.act()

        for (i in 0 until skip) {
            awaitItem()
        }

        for (assertion in expected) {
            val item = awaitItem()
            assert(assertion(item))
        }

        cancelAndIgnoreRemainingEvents()
    }

    bloc.verify()
}