package rubikstudio.library

import android.animation.Animator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import rubikstudio.library.LuckyWheelUtils.drawableToBitmap
import rubikstudio.library.model.LuckyItem
import java.util.Random
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Created by kiennguyen on 11/5/16.
 */
class PielView : View {
    private var mRange = RectF()
    private var mRadius = 0
    private var mArcPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: TextPaint? = null
    private val mStartAngle = 0f
    private var mCenter = 0
    private var mPadding = 0
    private var mTopTextPadding = 0
    private var mTopTextSize = 0
    private var mSecondaryTextSize = 0
    private var mRoundOfNumber = 4
    private var mEdgeWidth = -1
    private var isRunning = false
    private var borderColor = 0
    private var defaultBackgroundColor = 0
    private var drawableCenterImage: Drawable? = null
    private var textColor = 0
    private var predeterminedNumber = -1
    var viewRotation = 0f
    var fingerRotation = 0.0
    var downPressTime: Long = 0
    var upPressTime: Long = 0
    var newRotationStore = DoubleArray(3)
    private var mLuckyItemList: List<LuckyItem>? = null
    private var mPieRotateListener: PieRotateListener? = null

    private var mAnimation: ViewPropertyAnimator? = null

    interface PieRotateListener {
        fun rotateDone(index: Int)
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setPieRotateListener(listener: PieRotateListener?) {
        mPieRotateListener = listener
    }

    private fun init() {
        mArcPaint = Paint()
        mArcPaint!!.isAntiAlias = true
        mArcPaint!!.isDither = true
        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true
        if (textColor != 0) mTextPaint!!.color = textColor
        mTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14f,
            resources.displayMetrics
        )
        mRange = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (mPadding + mRadius).toFloat(),
            (mPadding + mRadius).toFloat()
        )
    }

    fun getLuckyItemListSize(): Int {
        return mLuckyItemList!!.size
    }

    fun setData(luckyItemList: List<LuckyItem>?) {
        mLuckyItemList = luckyItemList
        invalidate()
    }

    fun setPieBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setBorderColor(color: Int) {
        borderColor = Color.WHITE
        invalidate()
    }

    fun setTopTextPadding(padding: Int) {
        mTopTextPadding = padding
        invalidate()
    }

    fun setPieCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setTopTextSize(size: Int) {
        mTopTextSize = size
        invalidate()
    }

    fun setSecondaryTextSizeSize(size: Int) {
        mSecondaryTextSize = size
        invalidate()
    }

    fun setBorderWidth(width: Int) {
        mEdgeWidth = width
        invalidate()
    }

    fun setPieTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    private fun drawPieBackgroundWithBitmap(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawBitmap(
            bitmap, null, Rect(
                mPadding / 2, mPadding / 2,
                measuredWidth - mPadding / 2,
                measuredHeight - mPadding / 2
            ), null
        )
    }

    /**
     * @param canvas
     */
    @SuppressLint("DrawAllocation", "ResourceAsColor")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mLuckyItemList == null) {
            return
        }
        drawBackgroundColor(canvas, defaultBackgroundColor)
        init()
        var tmpAngle = mStartAngle
        val sweepAngle = 360f / mLuckyItemList!!.size
        for (i in mLuckyItemList!!.indices) {
            if (mLuckyItemList!![i].color != 0) {
                mArcPaint!!.style = Paint.Style.FILL
                mArcPaint!!.strokeWidth = 20f
                mArcPaint!!.color = mLuckyItemList!![i].color
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            }
            if (borderColor != 0 && mEdgeWidth > 0) {
                mArcPaint!!.style = Paint.Style.STROKE
                mArcPaint!!.color = R.color.background
                mArcPaint!!.strokeWidth = mEdgeWidth.toFloat()
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            }
            val sliceColor =
                if (mLuckyItemList!![i].color != 0) mLuckyItemList!![i].color else defaultBackgroundColor
            if (!TextUtils.isEmpty(mLuckyItemList!![i].topText)) drawTopText(
                canvas,
                tmpAngle,
                sweepAngle,
                mLuckyItemList!![i].topText,
                sliceColor
            )
            if (!TextUtils.isEmpty(mLuckyItemList!![i].secondaryText)) drawSecondaryText(
                canvas,
                tmpAngle,
                mLuckyItemList!![i].secondaryText,
                sliceColor
            )
            if (mLuckyItemList!![i].icon != 0) drawImage(
                canvas, tmpAngle, BitmapFactory.decodeResource(
                    resources,
                    mLuckyItemList!![i].icon
                )
            )
            tmpAngle += sweepAngle
        }
//        drawCenterImage(canvas, drawableCenterImage)
    }

    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == 0) return
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.color = color
        canvas.drawCircle(
            mCenter.toFloat(),
            mCenter.toFloat(),
            (mCenter + 5).toFloat(),
            mBackgroundPaint!!
        )
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = Math.min(measuredWidth, measuredHeight)
        mPadding = if (paddingLeft == 0) 10 else paddingLeft
        mRadius = width - mPadding * 2
        mCenter = width / 2
        setMeasuredDimension(width, width)
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param bitmap
     */
    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = mRadius / mLuckyItemList!!.size
        val angle = ((tmpAngle + 360f / mLuckyItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * Math.sin(angle.toDouble())).toInt()
        val rect = Rect(
            x - imgWidth / 2, y - imgWidth / 2,
            x + imgWidth / 2, y + imgWidth / 2
        )
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawCenterImage(canvas: Canvas, drawable: Drawable?) {
        var bitmap = drawableToBitmap(drawable!!)
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            false
        )
        canvas.drawBitmap(
            bitmap, (measuredWidth / 2 - bitmap.width / 2).toFloat(),
            (
                    measuredHeight / 2 - bitmap.height / 2).toFloat(), null
        )
    }

    private fun isColorDark(color: Int): Boolean {
        val colorValue = ColorUtils.calculateLuminance(color)
        val compareValue = 0.30
        return colorValue <= compareValue
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private fun drawTopText(
        canvas: Canvas,
        tmpAngle: Float,
        sweepAngle: Float,
        mStr: String?,
        backgroundColor: Int
    ) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        val typeface = ResourcesCompat.getFont(context, R.font.baloo);
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textAlign = Paint.Align.LEFT
        mTextPaint!!.textSize = mTopTextSize.toFloat()
        val textWidth = mTextPaint!!.measureText(mStr)
        val hOffset = (mRadius * Math.PI / mLuckyItemList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mTopTextPadding
        canvas.drawTextOnPath(mStr!!, path, hOffset.toFloat(), vOffset.toFloat(), mTextPaint!!)
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param mStr
     * @param backgroundColor
     */
    private fun drawSecondaryText(
        canvas: Canvas,
        tmpAngle: Float,
        mStr: String?,
        backgroundColor: Int
    ) {
        canvas.save()
        val arraySize = mLuckyItemList!!.size
        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        val typeface = ResourcesCompat.getFont(context, R.font.baloo);
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textSize = mSecondaryTextSize.toFloat()
        mTextPaint!!.textAlign = Paint.Align.LEFT
        val textWidth = mTextPaint!!.measureText(mStr)
        val initFloat = tmpAngle + 360f / arraySize / 2
        val angle = (initFloat * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * Math.sin(angle.toDouble())).toInt()
        val rect = RectF(
            x + textWidth, y.toFloat(),
            x - textWidth, y.toFloat()
        )
        val path = Path()
        path.addRect(rect, Path.Direction.CW)
        path.close()
        canvas.rotate(initFloat + arraySize / 18f, x.toFloat(), y.toFloat())
        canvas.drawTextOnPath(
            mStr!!,
            path,
            mTopTextPadding / 1.50f,
            mTextPaint!!.textSize / 2.75f,
            mTextPaint!!
        )
        canvas.restore()
    }

    /**
     * @return
     */
    private fun getAngleOfIndexTarget(index: Int): Float {
        return 360f / mLuckyItemList!!.size * index
    }

    /**
     * @param numberOfRound
     */
    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }

    fun setPredeterminedNumber(predeterminedNumber: Int) {
        this.predeterminedNumber = predeterminedNumber
    }

    var startTime = 0L
    fun rotateTo(index: Int, duration: Int?, speed: Int?) {
        val rand = Random()
        rotateTo(index, rand.nextInt() * 3 % 2, true, duration, speed)
    }

    fun resetWheel() {

    }

    fun stopAnimation(index: Int) {
        mAnimation?.setListener(null)
        mAnimation?.cancel()
        mAnimation = null

        val rotationTarget = getCenterOfWheelItem(index)
        // 1 -- 5 --10
        val rotationSpeed: Int = 1//

        val animatorListener = object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                isRunning = false
                if (mPieRotateListener != null) {
                    mPieRotateListener!!.rotateDone(index)
                }
                clearAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) { // no need
            }
        }

        val rotateNew = ((360 * rotationSpeed) + rotationTarget)
        mAnimation = animate()
        mAnimation!!.apply {
            interpolator = LinearInterpolator()
            this.duration = 1L
            rotation(rotateNew)
            setListener(animatorListener)
            start()
        }
    }

    private val TAG = "PielView"

    /**
     * @param index
     * @param rotation,  spin orientation of the wheel if clockwise or counterclockwise
     * @param startSlow, either animates a slow start or an immediate turn based on the trigger
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun rotateTo(
        index: Int,
        @SpinRotation rotation: Int,
        startSlow: Boolean,
        duration: Int?,
        speed: Int?
    ) {
        if (isRunning) {
            return
        }
        setRotation(0f)

        mAnimation?.cancel()
        mAnimation = null

        val animatorListener = object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isRunning = true
                startTime = System.currentTimeMillis()
            }

            override fun onAnimationEnd(animation: Animator) {
                isRunning = false
                if (mPieRotateListener != null) {
                    mPieRotateListener!!.rotateDone(index)
                }
                clearAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) { // no need
            }
        }

        var rotation = getCenterOfWheelItem(index)
        // logic => duration và rotate tỷ lệ thuận => cùng tăng (3 = min duration)
        // 1 -- 5 --10
        val rotationSpeed = when (speed) {
            1 -> {
                rotation = when  {
                    duration in 3.. 6 -> rotation + 360f
                    duration in 6.. 10 -> rotation + 2 * 360f
                    else -> rotation
                }
                1
            } // slow
            2 -> {
                rotation += 360f * ((duration ?: 3) - 3)
                5
            } // normal
            3 -> {
                rotation += 360f * ((duration ?: 3) - 1)
                10
            } // flash
            else -> 5
        }

        mAnimation = animate()
        mAnimation!!.apply {
            interpolator = DecelerateInterpolator()
            this.duration = ((duration ?: 1) * 1000).toLong()
            rotation((360 * rotationSpeed) + rotation)
            setListener(animatorListener)
            start()
        }
    }

    private fun getCenterOfWheelItem(target: Int): Float {
        val sweepAngle: Float = (360 / mLuckyItemList!!.size).toFloat()
        val halfOfWheelItem: Float = sweepAngle / 2
        val targetItemAngle: Float = sweepAngle * (target + 1)
        return 270 - targetItemAngle + halfOfWheelItem
    }

    fun getTargetByAngle(angle: Float): Int {
        val sweepAngle: Float = (360 / mLuckyItemList!!.size).toFloat()
        val halfOfWheelItem: Float = sweepAngle / 2

        val index = sweepAngle / (270 - angle + halfOfWheelItem) - 1
        return index.toInt()
    }

    private var touchEnabled = true
    fun isTouchEnabled(): Boolean {
        return touchEnabled
    }

    fun setTouchEnabled(touchEnabled: Boolean) {
        this.touchEnabled = touchEnabled
    }

    fun getDefaultDuration() = mRoundOfNumber * 1000 + 900L

    private fun newRotationValue(
        originalWheenRotation: Float,
        originalFingerRotation: Double,
        newFingerRotation: Double
    ): Float {
        val computationalRotation = newFingerRotation - originalFingerRotation
        return (originalWheenRotation + computationalRotation.toFloat() + 360f) % 360f
    }

    private fun getFallBackRandomIndex(): Int {
        val rand = Random()
        return rand.nextInt(mLuckyItemList!!.size - 1) + 0
    }

    /**
     * This detects if your finger movement is a result of an actual raw touch event of if it's from a view jitter.
     * This uses 3 events of rotation temporary storage so that differentiation between swapping touch events can be determined.
     *
     * @param newRotValue
     */
    private fun isRotationConsistent(newRotValue: Double): Boolean {
        if (newRotationStore[2].compareTo(newRotationStore[1]) != 0) {
            newRotationStore[2] = newRotationStore[1]
        }
        if (newRotationStore[1].compareTo(newRotationStore[0]) != 0) {
            newRotationStore[1] = newRotationStore[0]
        }
        newRotationStore[0] = newRotValue
        return !(newRotationStore[2].compareTo(newRotationStore[0]) == 0 || newRotationStore[1].compareTo(
            newRotationStore[0]
        ) == 0 || newRotationStore[2].compareTo(newRotationStore[1]) == 0 || newRotationStore[0] > newRotationStore[1] && newRotationStore[1] < newRotationStore[2] || newRotationStore[0] < newRotationStore[1] && newRotationStore[1] > newRotationStore[2])
    }

    //    @IntDef(SpinRotation.CLOCKWISE, SpinRotation.COUNTERCLOCKWISE)
    internal annotation class SpinRotation {
        companion object {
            var CLOCKWISE = 0
            var COUNTERCLOCKWISE = 1
        }
    }
}