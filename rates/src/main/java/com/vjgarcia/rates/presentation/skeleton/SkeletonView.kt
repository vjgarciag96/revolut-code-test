package com.vjgarcia.rates.presentation.skeleton

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.vjgarcia.rates.R

class SkeletonView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    init {
        background = SkeletonDrawable(
            context,
            getRadius(context, attrs),
            getShape(context, attrs)
        )
    }

    private fun getRadius(context: Context, attrs: AttributeSet?): Float? {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SkeletonView, 0, 0)
        val hasCorner = typedArray.hasValue(R.styleable.SkeletonView_cornerRadius)
        val radius = if (hasCorner) typedArray.getDimension(R.styleable.SkeletonView_cornerRadius, 0f) else null
        typedArray.recycle()
        return radius
    }

    private fun getShape(context: Context, attrs: AttributeSet?): SkeletonShape {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SkeletonView, 0, 0)
        val shapeValue = typedArray.getInt(R.styleable.SkeletonView_shape, 0)
        val shape = SkeletonShape.values()[shapeValue]
        typedArray.recycle()
        return shape
    }
}