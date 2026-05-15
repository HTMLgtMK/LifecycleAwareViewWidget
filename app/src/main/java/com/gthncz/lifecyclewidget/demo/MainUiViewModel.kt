package com.gthncz.lifecyclewidget.demo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the ViewWidget + ViewModel usage demo.
 *
 * Exposes a [count] [StateFlow] that a [BaseViewWidget] subclass can observe.
 * State survives configuration changes via Android's ViewModel framework.
 */
class MainUiViewModel : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        _count.value++
    }

    fun decrement() {
        _count.value--
    }
}
