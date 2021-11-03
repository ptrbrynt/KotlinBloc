package com.ptrbrynt.kotlin_bloc.test

import app.cash.turbine.test
import com.ptrbrynt.kotlin_bloc.core.BlocBase
import kotlin.time.ExperimentalTime

typealias StateAssertion<State> = State.() -> Boolean

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
 * @param expected A list of [StateAssertion]s which will be run on each newly emitted state in
 * order. Use this to check the correctness of each state emitted by the `bloc` under test.
 *
 * @param verify An optional callback which is invoked after all [expected] states have been
 * emitted, and can be used for additional verification/assertions.
 *
 * @param tearDown Can be used to execute any code after the test has run.
 */
@ExperimentalTime
suspend fun <B : BlocBase<State>, State> testBloc(
    setUp: suspend () -> Unit = {},
    build: () -> B,
    act: suspend B.() -> Unit = {},
    skip: Int = 0,
    expected: List<StateAssertion<State>>,
    verify: B.() -> Unit = {},
    tearDown: suspend () -> Unit = {},
) {
    assert(skip >= 0)

    setUp()

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

    tearDown()
}
