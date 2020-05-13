package jp.ryuk.deglog.utilities

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

class FlickListener(
    private val listener: Listener
) : View.OnTouchListener {

    interface Listener {
        fun onButtonPressed()
        fun onButtonReleased()
        fun onFlickToLeft()
        fun onFlickToRight()
    }

    private val play = 50f
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchDown(event)
            MotionEvent.ACTION_UP -> touchOff(event)
        }
        return true
    }

    private fun touchDown(event: MotionEvent) {
        startX = event.x
        startY = event.y
        listener.onButtonPressed()
    }

    private fun touchOff(event: MotionEvent) {
        endX = event.x
        endY = event.y
        when {
            leftScope()  -> listener.onFlickToLeft()
            rightScope() -> listener.onFlickToRight()
            else         -> listener.onButtonReleased()
        }
    }

    private fun leftScope(): Boolean = endX < startX - play
    private fun rightScope(): Boolean = startX + play < endX
}