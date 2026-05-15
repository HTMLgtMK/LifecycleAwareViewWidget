package com.gthncz.lifecyclewidget

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class ViewWidgetOwner(private val lifecycleOwner: LifecycleOwner): LifecycleEventObserver {

    private val widgets = mutableListOf<BaseViewWidget>()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun attach(vararg widgets: BaseViewWidget) {
        widgets.forEach { widget ->
            widget.bind(lifecycleOwner)
            this.widgets.add(widget)
        }

        widgets.forEach { widget ->
            widget.onBind()
        }
    }

    fun detach(vararg widgets: BaseViewWidget) {
        widgets.forEach { widget->
            this.widgets.remove(widget)
            widget.unbind()
        }

        widgets.forEach { widget ->
            widget.onUnBind()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                source.lifecycle.removeObserver(this)
                detachAll()
            }
            else -> {}
        }
    }

    private fun detachAll() {
        widgets.forEach { widget->
            widget.unbind()
        }
        widgets.clear()
    }


}