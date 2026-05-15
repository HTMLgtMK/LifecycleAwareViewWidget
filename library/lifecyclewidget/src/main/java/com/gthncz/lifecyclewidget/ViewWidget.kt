package com.gthncz.lifecyclewidget

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry


/**
 * Domain 层抽象的 View Widget
 */
interface IViewWidget: LifecycleOwner {

    val rootView: View?

    fun onBind()

    fun onUnBind()

    fun show()

    fun hide()

    fun onShow()

    fun onHide()

}

abstract class BaseViewWidget(override val rootView: View?): IViewWidget {

    private val lifecycleRegistry = LifecycleRegistry(this)

    private val combinedLifecycleRegistry = CombinedLifecycleRegistry()

    override val lifecycle: Lifecycle
        get() = combinedLifecycleRegistry.lifecycle

    override fun onBind() {

    }

    override fun onUnBind() {

    }

    override fun show() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        onShow()
    }

    override fun onShow() {
        this.rootView?.visibility = View.VISIBLE
    }

    override fun hide() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        onHide()
    }

    override fun onHide() {
        this.rootView?.visibility = View.GONE
    }

    internal fun bind(lifecycleOwner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        combinedLifecycleRegistry.combine(lifecycleRegistry, lifecycleOwner.lifecycle)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    internal fun unbind() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        combinedLifecycleRegistry.uncombine()
    }

    private class CombinedLifecycleRegistry():  LifecycleOwner {

        private val lifecycleRegistry = LifecycleRegistry(this)

        private val observerMap = mutableMapOf<Lifecycle, LifecycleEventObserver>()

        fun combine(vararg lifecycleOwners: Lifecycle) {
            uncombine()
            val observer = LifecycleEventObserver { source, event ->
                updateCombinedState(lifecycleOwners)
            }
            for (lifecycleOwner in lifecycleOwners) {
                lifecycleOwner.addObserver(observer)
                observerMap[lifecycleOwner] = observer
            }
            updateCombinedState(lifecycleOwners)
        }

        fun uncombine() {
            observerMap.forEach { (source, observer) ->
                source.removeObserver(observer)
            }
            observerMap.clear()
        }

        private fun updateCombinedState(sources: Array<out Lifecycle>) {
            val minState = sources.minOf { lifecycle ->
                lifecycle.currentState
            }
            lifecycleRegistry.currentState = minState
        }

        override val lifecycle: Lifecycle
            get() = lifecycleRegistry
    }

}
