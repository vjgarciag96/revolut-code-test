package com.vjgarcia.currencies.presentation.skeleton

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.ColorInt
import com.vjgarcia.currencies.R
import kotlin.math.max
import kotlin.math.sqrt

enum class SkeletonShape {
    RECTANGLE,
    OVAL
}

class SkeletonDrawable(
    context: Context,
    radiusPx: Float? = null,
    private val shape: SkeletonShape = SkeletonShape.RECTANGLE,
    private val skeletonDrawableAnimator: SkeletonDrawableAnimator = DEFAULT_ANIMATOR
) : Drawable() {
    private var maskOffsetX: Int = 0
    private var maskRect: Rect? = null
    private var viewRect: RectF? = null
    private var gradientTexturePaint: Paint? = null

    private var maskBitmap: Bitmap? = null
    private var canvasForShimmerMask: Canvas? = null

    private var isAnimationStarted: Boolean = false
    private var shimmerAnimationDuration: Int = 0
    @ColorInt
    private var shimmerColor: Int = context.resources.getColor(R.color.skeletonWave)
    private var shimmerAngle: Int = DEFAULT_ANGLE
    private var maskWidth: Float = 0.toFloat()
    private var gradientCenterColorWidth: Float = 0.toFloat()
    private var skeletonDrawableAnimatorCallback: SkeletonDrawableAnimator.Callback? = null

    private val paint = Paint()
    private val radius = radiusPx ?: context.resources.getDimension(R.dimen.default_skeleton_radius)
    @ColorInt
    private val backgroundColor = context.resources.getColor(R.color.skeletonBackground)
    private val maxWidthAnimation: Int

    init {
        shimmerAngle = DEFAULT_ANGLE
        shimmerAnimationDuration = DEFAULT_ANIMATION_DURATION
        maskWidth = DEFAULT_MASK_WIDTH
        gradientCenterColorWidth = DEFAULT_GRADIENT_CENTER_COLOR_WIDTH
        paint.isAntiAlias = true
        paint.color = backgroundColor
        viewRect = RectF(bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())

        val metrics = DisplayMetrics()
        val windowManager = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        maxWidthAnimation = metrics.widthPixels
        setMaskWidth(maskWidth)
        setGradientCenterColorWidth(gradientCenterColorWidth)
        setShimmerAngle(shimmerAngle)
        startShimmerAnimation()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        this.bounds = bounds
        viewRect = RectF(bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())
        invalidateSelf()
        resetIfStarted()
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        val hasChanged = super.setVisible(visible, restart)
        if (hasChanged) {
            if (visible) {
                startShimmerAnimation()
            } else {
                stopShimmerAnimation()
            }
        }
        return hasChanged
    }

    override fun draw(canvas: Canvas) {
        val viewRect = viewRect ?: return

        when (shape) {
            SkeletonShape.RECTANGLE -> canvas.drawRoundRect(viewRect, radius, radius, paint)
            SkeletonShape.OVAL -> canvas.drawOval(viewRect, paint)
        }

        drawShimmer(canvas)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    /**
     * Set the angle of the shimmer effect in clockwise direction in degrees.
     * The angle must be between {@value #MIN_ANGLE_VALUE} and {@value #MAX_ANGLE_VALUE}.
     *
     * @param angle The angle to be set
     */
    private fun setShimmerAngle(angle: Int) {
        require(angle in MIN_ANGLE_VALUE..MAX_ANGLE_VALUE) {
            "shimmerAngle value must be between $MIN_ANGLE_VALUE and $MAX_ANGLE_VALUE"
        }
        this.shimmerAngle = angle
        resetIfStarted()
    }

    /**
     * Sets the width of the shimmer line to a value higher than 0 to less or equal to 1.
     * 1 means the width of the shimmer line is equal to half of the width of the ShimmerLayout.
     * The default value is 0.5.
     *
     * @param maskWidth The width of the shimmer line.
     */
    private fun setMaskWidth(maskWidth: Float) {
        require(maskWidth > MIN_MASK_WIDTH_VALUE && MAX_MASK_WIDTH_VALUE >= maskWidth) {
            "maskWidth value must be higher than $MIN_MASK_WIDTH_VALUE and less or equal to $MAX_MASK_WIDTH_VALUE"
        }
        this.maskWidth = maskWidth
        resetIfStarted()
    }

    /**
     * Sets the width of the center gradient color to a value higher than 0 to less than 1.
     * 0.99 means that the whole shimmer line will have this color with a little transparent edges.
     * The default value is 0.1.
     *
     * @param gradientCenterColorWidth The width of the center gradient color.
     */
    private fun setGradientCenterColorWidth(gradientCenterColorWidth: Float) {
        require(gradientCenterColorWidth > MIN_GRADIENT_CENTER_COLOR_WIDTH_VALUE && MAX_GRADIENT_CENTER_COLOR_WIDTH_VALUE > gradientCenterColorWidth) {
            "gradientCenterColorWidth value must be higher than $MIN_GRADIENT_CENTER_COLOR_WIDTH_VALUE and less than $MAX_GRADIENT_CENTER_COLOR_WIDTH_VALUE"
        }
        this.gradientCenterColorWidth = gradientCenterColorWidth
        resetIfStarted()
    }

    @Synchronized
    private fun startShimmerAnimation() {
        if (isAnimationStarted) {
            return
        }

        isAnimationStarted = true
        if (!initShimmeringResources()) {
            //We leave as we cannot make the animation without having prepared the resources successfully.
            //But we keep the isAnimationStarted flag to true to start the animation when the next onBoundsChange will be called.
            return
        }

        getSkeletonDrawableAnimatorCallback()?.let {
            skeletonDrawableAnimator.startListeningAnimation(it)
        }

    }

    @Synchronized
    private fun stopShimmerAnimation() {
        releaseShimmeringResources()
    }

    private fun resetIfStarted() {
        if (isAnimationStarted) {
            releaseShimmeringResources()
            startShimmerAnimation()
        }
    }

    private fun drawShimmer(destinationCanvas: Canvas?) {
        val canvas = destinationCanvas ?: return
        val shimmerRect = maskRect ?: return
        val shimmerPaint = gradientTexturePaint ?: return
        val canvasForShimmerMask = canvasForShimmerMask ?: return

        updateShimmerMask(canvasForShimmerMask)

        canvas.save()
        canvas.translate(maskOffsetX.toFloat(), 0f)
        canvas.drawRect(shimmerRect, shimmerPaint)
        canvas.restore()
    }

    private fun updateShimmerMask(canvasForShimmerMask: Canvas) {
        canvasForShimmerMask.apply {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            save()
            translate(-maskOffsetX.toFloat(), 0.toFloat())
            viewRect?.let {
                when (shape) {
                    SkeletonShape.RECTANGLE -> drawRoundRect(it, radius, radius, paint)
                    SkeletonShape.OVAL -> drawOval(it, paint)
                }
            }
            restore()
        }
    }

    private fun initShimmeringResources(): Boolean {
        maskBitmap = createBitmap(getWidth(), getHeight())

        if (maskBitmap == null) {
            return false
        }
        canvasForShimmerMask = Canvas(maskBitmap!!)
        gradientTexturePaint = createShimmerPaint()
        if (gradientTexturePaint == null) {
            return false
        }
        return true
    }

    private fun releaseShimmeringResources() {
        skeletonDrawableAnimatorCallback?.let(skeletonDrawableAnimator::stopListeningAnimation)
        skeletonDrawableAnimatorCallback = null
        gradientTexturePaint = null
        isAnimationStarted = false
        releaseBitMaps()
    }

    private fun releaseBitMaps() {
        canvasForShimmerMask = null
        maskBitmap?.recycle()
        maskBitmap = null
    }


    fun getHeight(): Int = Math.abs(bounds.height())

    fun getWidth(): Int = Math.abs(bounds.width())

    private fun createShimmerPaint(): Paint? {
        val maskBitmap = maskBitmap ?: return null
        val edgeColor = reduceColorAlphaValueToZero(shimmerColor)
        val shimmerLineWidth = maxWidthAnimation / 2 * maskWidth
        val yPosition = (if (0 <= shimmerAngle) getHeight() else 0).toFloat()

        val gradient = when (shape) {
            SkeletonShape.RECTANGLE -> LinearGradient(
                0f,
                yPosition,
                Math.cos(Math.toRadians(shimmerAngle.toDouble())).toFloat() * shimmerLineWidth,
                yPosition + Math.sin(Math.toRadians(shimmerAngle.toDouble())).toFloat() * shimmerLineWidth,
                intArrayOf(edgeColor, shimmerColor, shimmerColor, edgeColor),
                getGradientColorDistribution(),
                Shader.TileMode.CLAMP
            )
            SkeletonShape.OVAL -> RadialGradient(
                getWidth() / 2f,
                getHeight() / 2f,
                (max(getWidth(), getHeight()) / sqrt(2.0)).toFloat(),
                intArrayOf(edgeColor, shimmerColor, shimmerColor, edgeColor),
                getGradientColorDistribution(),
                Shader.TileMode.CLAMP
            )
        }
        val maskBitmapShader = BitmapShader(maskBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val composeShader = ComposeShader(gradient, maskBitmapShader, PorterDuff.Mode.DST_IN)
        return Paint().apply {
            isAntiAlias = true
            isDither = false
            isFilterBitmap = false
            shader = composeShader
        }
    }

    private fun getSkeletonDrawableAnimatorCallback(): SkeletonDrawableAnimator.Callback? {
        skeletonDrawableAnimatorCallback?.let {
            return it
        }
        maskRect = calculateBitmapMaskRect()
        val widthMask = maskRect?.width() ?: return null
        val animationToX = maxWidthAnimation
        val animationFromX: Int

        animationFromX = if (getWidth() > widthMask) {
            -animationToX
        } else {
            -widthMask
        }

        val shimmerAnimationFullLength = animationToX - animationFromX

        skeletonDrawableAnimatorCallback = object : SkeletonDrawableAnimator.Callback {
            override fun onUpdate(value: Float) {
                maskOffsetX = animationFromX + (shimmerAnimationFullLength * value).toInt()
                if (maskOffsetX + widthMask >= 0 && maskOffsetX < getWidth()) {
                    invalidateSelf()
                }
            }
        }
        return skeletonDrawableAnimatorCallback
    }

    private fun createBitmap(width: Int, height: Int): Bitmap? {
        if (width == 0 || height == 0)
            return null
        try {
            return Bitmap.createBitmap(Math.abs(width), Math.abs(height), Bitmap.Config.ALPHA_8)
        } catch (e: OutOfMemoryError) {
            return null
        }

    }

    private fun reduceColorAlphaValueToZero(actualColor: Int): Int = Color.argb(0, Color.red(actualColor), Color.green(actualColor), Color.blue(actualColor))

    private fun calculateBitmapMaskRect(): Rect = Rect(0, 0, calculateMaskWidth(), getHeight())

    private fun calculateMaskWidth(): Int {
        val shimmerLineBottomWidth = maxWidthAnimation / 2 * maskWidth / Math.cos(Math.toRadians(Math.abs(shimmerAngle).toDouble()))
        val shimmerLineRemainingTopWidth = getHeight() * Math.tan(Math.toRadians(Math.abs(shimmerAngle).toDouble()))

        return (shimmerLineBottomWidth + shimmerLineRemainingTopWidth).toInt()
    }

    private fun getGradientColorDistribution(): FloatArray = floatArrayOf(
        COLOR_DISTRIBUTION_ALPHA_TRANSPARENT,
        COLOR_DISTRIBUTION_ALPHA_MIDDLE - gradientCenterColorWidth / HALF,
        COLOR_DISTRIBUTION_ALPHA_MIDDLE + gradientCenterColorWidth / HALF,
        COLOR_DISTRIBUTION_ALPHA_OPAQUE
    )

    companion object {
        private const val DEFAULT_ANIMATION_DURATION = 1500
        private const val DEFAULT_MASK_WIDTH = 0.90f
        private const val DEFAULT_GRADIENT_CENTER_COLOR_WIDTH = 0.2f

        private const val DEFAULT_ANGLE: Int = 0

        private const val MIN_ANGLE_VALUE: Int = -45
        private const val MAX_ANGLE_VALUE: Int = 45
        private const val MIN_MASK_WIDTH_VALUE: Int = 0
        private const val MAX_MASK_WIDTH_VALUE: Int = 1

        private const val MIN_GRADIENT_CENTER_COLOR_WIDTH_VALUE: Int = 0
        private const val MAX_GRADIENT_CENTER_COLOR_WIDTH_VALUE: Int = 1
        private val DEFAULT_ANIMATOR = SkeletonDrawableAnimator(DEFAULT_ANIMATION_DURATION.toLong())

        private const val COLOR_DISTRIBUTION_ALPHA_TRANSPARENT = 0f
        private const val COLOR_DISTRIBUTION_ALPHA_MIDDLE = 0.5f
        private const val COLOR_DISTRIBUTION_ALPHA_OPAQUE = 1f
        private const val HALF = 2f
    }

}