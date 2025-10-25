package com.example.ejercicio2

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.animation.ValueAnimator
import android.animation.AnimatorSet
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

class ZoomableImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val matrix = Matrix()
    private val savedMatrix = Matrix()

    private var mode = NONE

    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f

    private var scaleFactor = 1f
    private var minScale = 1f
    private var maxScale = 4f
    
    private var currentAnimator: ValueAnimator? = null

    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())

    init {
        isClickable = true
        scaleType = ScaleType.MATRIX
        imageMatrix = matrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG) {
                    val dx = event.x - start.x
                    val dy = event.y - start.y
                    matrix.set(savedMatrix)
                    matrix.postTranslate(dx, dy)
                } else if (mode == ZOOM) {
                    // handled by scaleDetector -> ScaleListener
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
            }
        }
        imageMatrix = matrix
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val factor = detector.scaleFactor
            val newScale = scaleFactor * factor
            val clamped = max(minScale, min(newScale, maxScale))
            val scaleChange = clamped / scaleFactor
            scaleFactor = clamped
            matrix.postScale(scaleChange, scaleChange, detector.focusX, detector.focusY)
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Cancel any running animation
            currentAnimator?.cancel()
            
            val targetScale = if (scaleFactor > minScale) minScale else 2f
            val startScale = scaleFactor
            
            // Animate zoom transition
            currentAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 250
                interpolator = DecelerateInterpolator()
                addUpdateListener { animator ->
                    val progress = animator.animatedValue as Float
                    val currentScale = startScale + (targetScale - startScale) * progress
                    val scaleChange = currentScale / scaleFactor
                    scaleFactor = currentScale
                    matrix.postScale(scaleChange, scaleChange, e.x, e.y)
                    imageMatrix = matrix
                }
                start()
            }
            return true
        }
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }
}
