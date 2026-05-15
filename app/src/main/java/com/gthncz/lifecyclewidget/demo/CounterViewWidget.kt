package com.gthncz.lifecyclewidget.demo

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.gthncz.lifecyclewidget.BaseViewWidget
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gthncz.lifecyclewidget.R
import com.gthncz.lifecyclewidget.databinding.LayoutCounterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * A [BaseViewWidget] subclass that observes [MainUiViewModel.count] and
 * displays it in a [TextView], with increment/decrement buttons.
 *
 * All business logic (button actions, state observation, UI updates) is
 * encapsulated within this widget. The host only needs to create and attach it.
 *
 * Lifecycle:
 * - [onBind]: Start observing [viewModel.count]; wire up button click handlers.
 * - [onUnBind]: Cancel observation and tear down button handlers.
 * - [show]/[hide]: Inherited from [BaseViewWidget] — sets visibility and fires lifecycle events.
 */
class CounterViewWidget(
    override val rootView: View?,
    private val viewModel: MainUiViewModel
) : BaseViewWidget(rootView) {

    private var counterBinding: LayoutCounterBinding? = null


    override fun onBind() {
        super.onBind()

        counterBinding = rootView?.findViewById<FrameLayout>(R.id.container)?.let { container->
            LayoutCounterBinding.inflate(LayoutInflater.from(container.context), container, true)
        }

        // Observe ViewModel state and update UI reactively
        lifecycleScope.launch {
            viewModel.count.collect { count ->
                counterBinding?.tvCounter?.text = "Counter: $count"
            }
        }

        rootView?.findViewById<View>(R.id.incrementButton)?.setOnClickListener {
            viewModel.increment()
        }

        rootView?.findViewById<View>(R.id.decrementButton)?.setOnClickListener {
            viewModel.decrement()
        }

        show()
    }

    override fun onShow() {
        counterBinding?.root?.visibility = View.VISIBLE
    }

    override fun onHide() {
        counterBinding?.root?.visibility = View.GONE
    }

    override fun onUnBind() {
        super.onUnBind()

    }
}
