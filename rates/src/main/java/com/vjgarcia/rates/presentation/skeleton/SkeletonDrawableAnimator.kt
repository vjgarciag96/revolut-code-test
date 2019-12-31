package com.vjgarcia.rates.presentation.skeleton

import android.animation.ObjectAnimator
import android.animation.ValueAnimator

class SkeletonDrawableAnimator(private val animationDuration: Long) {
    private var maskAnimator: ValueAnimator? = null
    private val animationCallbacks: MutableSet<Callback> = mutableSetOf()

    @Synchronized
    fun startListeningAnimation(callback: Callback) {
        animationCallbacks.add(callback)
        if (maskAnimator == null) {
            maskAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = animationDuration
                repeatCount = ObjectAnimator.INFINITE
                addUpdateListener { anim -> animationCallbacks.forEach { it.onUpdate(anim.animatedValue as Float) } }
                start()
            }
        }

    }

    @Synchronized
    fun stopListeningAnimation(callback: Callback) {
        animationCallbacks.remove(callback)
        if (animationCallbacks.isEmpty()) {
            maskAnimator?.end()
            maskAnimator?.removeAllUpdateListeners()
            maskAnimator = null
        }
    }

    interface Callback {
        fun onUpdate(value: Float)
    }
}