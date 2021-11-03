package com.ptrbrynt.kotlin_bloc.test

import com.ptrbrynt.kotlin_bloc.core.Bloc
import com.ptrbrynt.kotlin_bloc.core.Cubit
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlin.reflect.KClass

/**
 * Creates a mocked instance of the given [Cubit] type [C].
 *
 * Uses [mockk] under the hood.
 *
 * @param name Mock name
 * @param relaxed Allows creation without any explicit behavior
 * @param moreInterfaces Additional interfaces for this mock to implement
 * @param relaxUnitFun Allows creation with no specific behaviour for Unit function
 * @param block Block to execute after the mock has been created, with the mock as the receiver
 * @see mockk
 */
inline fun <reified C : Cubit<State>, reified State> mockCubit(
    name: String? = null,
    relaxed: Boolean = false,
    vararg moreInterfaces: KClass<*>,
    relaxUnitFun: Boolean = true,
    block: C.() -> Unit = {},
): C {
    return mockk(
        name = name,
        relaxed = relaxed,
        moreInterfaces = moreInterfaces,
        relaxUnitFun = relaxUnitFun
    ) {
        every { stateFlow } answers { emptyFlow() }
        coEvery { emit(any()) } returns Unit
        block()
    }
}

/**
 * Creates a mocked instance of the given [Bloc] type [B].
 *
 * Uses [mockk] under the hood.
 *
 * @param name Mock name
 * @param relaxed Allows creation without any explicit behavior
 * @param moreInterfaces Additional interfaces for this mock to implement
 * @param relaxUnitFun Allows creation with no specific behaviour for Unit function
 * @param block Block to execute after the mock has been created, with the mock as the receiver
 * @see mockk
 */
inline fun <reified B : Bloc<Event, State>, reified Event, reified State> mockBloc(
    name: String? = null,
    relaxed: Boolean = false,
    vararg moreInterfaces: KClass<*>,
    relaxUnitFun: Boolean = true,
    block: B.() -> Unit = {},
): B {
    return mockk(
        name = name,
        relaxed = relaxed,
        moreInterfaces = moreInterfaces,
        relaxUnitFun = relaxUnitFun,
    ) {
        every { stateFlow } answers { emptyFlow() }
        every { add(any()) } returns Unit

        block()
    }
}
