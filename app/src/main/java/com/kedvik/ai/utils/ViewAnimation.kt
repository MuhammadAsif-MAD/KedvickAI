package com.kedvik.ai.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

object ViewAnimation {
    fun showScale(v: View) {
        showScale(v, null)
    }

    fun showScale(v: View, animListener: AnimListener?) {
        v.scaleX = 0.5f
        v.scaleY = 0.5f
        v.animate()
            .scaleY(1f)
            .scaleX(1f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animListener?.onFinish()
                    super.onAnimationEnd(animation)
                }
            })
            .start()
    }

    interface AnimListener {
        fun onFinish()
    }
}
