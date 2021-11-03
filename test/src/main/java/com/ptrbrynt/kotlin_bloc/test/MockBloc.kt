package com.ptrbrynt.kotlin_bloc.test

import com.ptrbrynt.kotlin_bloc.core.Bloc
import com.ptrbrynt.kotlin_bloc.core.Cubit
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlin.reflect.KClass

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
