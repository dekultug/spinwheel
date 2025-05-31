package com.amvfunny.dev.wheelist.presentaition.widget.spinview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.setColor
import com.amvfunny.dev.wheelist.presentaition.widget.spinview.ripple.RippleBackground
import java.util.Collections
import java.util.LinkedList
import java.util.Random
import java.util.TreeMap
import kotlin.math.abs

class SpinView constructor(
    private val ctx: Context,
    attributeSet: AttributeSet?
) : RippleBackground(ctx, attributeSet) {

    companion object {
        private const val TAG = "SpinView"
        private const val OFFSET_TOP = 10f
        private const val OFFSET_MOVE = 20f
        private const val TIME_DELAY_SHOW_RESULT = 3500L
    }

    private val touchSlop: Int = ViewConfiguration.get(ctx).scaledTouchSlop

    // ui
    private val paintText = Paint().apply {
        color = getAppColor(R.color.orange_primary_bold)
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = getAppDimension(R.dimen.dimen_20)
        typeface = ResourcesCompat.getFont(context, R.font.bold)
    }

    private val paintLine = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        textSize = 100f
        strokeCap = Paint.Cap.BUTT
    }

    var listener: ISpinListener? = null

    private var startTime = 0L
    private var isEnd = false

    private var type: SPIN_TYPE = SPIN_TYPE.COUPLE
    private var sizeSpinner = 3

    private var mapPointToDraw: HashMap<Int, PointF> = hashMapOf()
    private var mapAnimation: HashMap<Int, AnimatorSet> = hashMapOf()
    private var mapView: HashMap<Int, List<RippleView>> = hashMapOf()

    private var mapColor: HashMap<Int, Int> = hashMapOf()

    private var indexCancel = 0
    private var isCancel = false
    private var isCheckAtHandler = false

    // chooser
    private val setTypeRandom: HashSet<Int> = hashSetOf()

    // rank
    private val mapRank: HashMap<PointF, Int> = hashMapOf()

    // homo
    // nối
    private val mapHomo: HashMap<Int, List<PointF>> = hashMapOf()

    // change color
    private val mapHomoChangeColorView: HashMap<Int, List<Int>> = hashMapOf()

    private var handler: Handler? = null
    private var runnable = object : Runnable {
        override fun run() {
            Log.d(TAG, "run: running")
            if (mapView.size > sizeSpinner) {
                when (type) {
                    SPIN_TYPE.CHOOSE -> {
                        Log.d(TAG, "run: CHOOSE")
                        typeChoose()
                        isEnd = true
                        handler = null
                    }

                    SPIN_TYPE.COUPLE -> {
                        Log.d(TAG, "run: COUPLE")
                        typeCouple()
                        isEnd = true
                        handler = null
                    }

                    SPIN_TYPE.RANK -> {
                        Log.d(TAG, "run: RANK")
                        typeRank()
                        isEnd = true
                        handler = null
                    }
                }
                isCheckAtHandler = true
            }
            handler?.postDelayed(this, TIME_DELAY_SHOW_RESULT * 2)
        }
    }

    init {
        handler = Handler(Looper.getMainLooper())
    }

    // ============== DRAW ==============

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawType(canvas)
        if (mapView.size * 3 < childCount) {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view is RippleView) {
                    if (!mapView.values.flatten().contains(view)) {
                        view.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun drawType(canvas: Canvas) {
        Log.d(TAG, "drawRank: canvas ${mapPointToDraw.size}")
        mapPointToDraw.forEach { (k, v) ->
            if (isEnd) {
                when {
                    type == SPIN_TYPE.CHOOSE -> {
                        drawChoose(canvas, k, v)
                    }

                    type == SPIN_TYPE.RANK -> {
                        drawRank(canvas, k, v)
                    }
                }
            }
        }

        if (isEnd && type == SPIN_TYPE.COUPLE) {
            paintText.color = getAppColor(R.color.white)
            mapHomo.forEach { (t, u) ->
                if (mapColor.contains(t)) {
                    val path = Path()
                    path.moveTo(u[0].x, u[0].y)

                    for (i in 1 until u.size) {
                        path.lineTo(u[i].x, u[i].y)
                    }

                    path.close()
                    canvas.drawPath(path, paintLine.apply { color = mapColor[t]!! })
                }

            }
            mapHomoChangeColorView.forEach { (t, u) ->
                u.forEach {
                    if (mapView.contains(it) && mapColor.contains(t)) {
                        mapView[it]?.setColor(mapColor[t]!!)
                    }

                    if (mapPointToDraw.contains(it)) {
                        canvas.drawText(
                            "${t + 1}",
                            mapPointToDraw[it]!!.x,
                            mapPointToDraw[it]!!.y + OFFSET_TOP,
                            paintText
                        )
                    }
                }
            }
        }
    }

    private fun drawChoose(canvas: Canvas, key: Int, value: PointF) {
        paintText.color = getAppColor(R.color.orange_primary_bold)
        if (setTypeRandom.contains(key)) {
            canvas.drawText(
                getAppString(R.string.user_winner, ctx),
                value.x,
                value.y - SIZE_CIRCLE,
                paintText
            )
        } else {
            // animation
            if (mapAnimation.contains(key)) {
                mapAnimation[key]!!.end()
                mapAnimation.remove(key)
            }

            // view
            if (mapView.contains(key)) {
                mapView[key]!!.forEach {
                    removeView(it)
                }
                mapView.remove(key)
            }
        }
    }

    private fun drawRank(canvas: Canvas, key: Int, value: PointF) {
        paintText.color = getAppColor(R.color.white)
        if (mapRank.contains(value)) {
            val rankNumber = (mapRank[value] ?: 0) + 1

            when (rankNumber) {
                1 -> {
                    val bitmap = BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_rank_top1
                    )
                    canvas.drawBitmap(
                        bitmap,
                        value.x - (bitmap.width / 2),
                        value.y - SIZE_CIRCLE - 2 * OFFSET_TOP,
                        null
                    );
                }

                2 -> {
                    val bitmap = BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_rank_top2
                    )
                    canvas.drawBitmap(
                        bitmap,
                        value.x - (bitmap.width / 2),
                        value.y - SIZE_CIRCLE - OFFSET_TOP,
                        null
                    );
                }

                3 -> {
                    val bitmap = BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_rank_top3
                    )
                    canvas.drawBitmap(
                        bitmap,
                        value.x - (bitmap.width / 2),
                        value.y - SIZE_CIRCLE,
                        null
                    );
                }
            }
            canvas.drawText(
                "$rankNumber",
                value.x,
                value.y + OFFSET_TOP,
                paintText
            )
        }
    }


    // ============== ACTION MOTION EVENT ==============
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.actionMasked
        if (isEnd) return false
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d(TAG, "onTouchEvent: ACTION_DOWN $indexCancel")
                setActionDown(event)
            }

            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                setActionUp(event)
                Log.d(TAG, "onTouchEvent: ACTION_UP $indexCancel")
            }

            MotionEvent.ACTION_MOVE -> {
                setActionMove(event)
                Log.d(TAG, "onTouchEvent: ACTION_MOVE ${indexCancel}")
            }

            MotionEvent.ACTION_CANCEL -> {
                isCancel = true
                indexCancel = mapPointToDraw.size
                Log.d(TAG, "onTouchEvent: ACTION_CANCEL ${indexCancel}")
                handler?.removeCallbacks(runnable)
                handler?.postDelayed(runnable, TIME_DELAY_SHOW_RESULT)
            }
        }
        return true
    }

    @SuppressLint("Recycle")
    private fun addViewAndAnimation(index: Int, pointF: PointF) {
        val rippleParams = LayoutParams(
            SIZE_CIRCLE,
            SIZE_CIRCLE
        )

        rippleParams.leftMargin = pointF.x.toInt() - SIZE_CIRCLE / 2
        rippleParams.topMargin = pointF.y.toInt() - SIZE_CIRCLE / 2

        val animatorSet = AnimatorSet()
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        val animatorList = ArrayList<Animator>()
        val rippleViewList: MutableList<RippleView> = arrayListOf()
        val newIndex = if (isCancel) {
            indexCancel + index
        } else {
            index
        }

        for (i in 0 until rippleAmount) {
            val rippleView = RippleView(context)
            mapColor[newIndex]?.let { rippleView.setColor(it) }
            addView(rippleView, rippleParams)
            rippleViewList.add(rippleView)
            if (i > 0) {
                val scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale)
                scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
                scaleXAnimator.repeatMode = ObjectAnimator.RESTART
                scaleXAnimator.startDelay = (i * rippleDelay).toLong()
                scaleXAnimator.setDuration(rippleDurationTime.toLong())
                animatorList.add(scaleXAnimator)

                val scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale)
                scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
                scaleYAnimator.repeatMode = ObjectAnimator.RESTART
                scaleYAnimator.startDelay = (i * rippleDelay).toLong()
                scaleYAnimator.setDuration(rippleDurationTime.toLong())
                animatorList.add(scaleYAnimator)

                val alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f)
                alphaAnimator.repeatCount = ObjectAnimator.INFINITE
                alphaAnimator.repeatMode = ObjectAnimator.RESTART
                alphaAnimator.startDelay = (i * rippleDelay).toLong()
                alphaAnimator.setDuration(rippleDurationTime.toLong())
                animatorList.add(alphaAnimator)
            }
        }

        animatorSet.playTogether(animatorList)
        rippleViewList.forEach {
            it.visibility = View.VISIBLE
        }
        animatorSet.start()
        Log.d(TAG, "onTouchEvent addViewAndAnimation: $newIndex")
        mapView[newIndex] = rippleViewList
        mapAnimation[newIndex] = animatorSet
        mapPointToDraw[newIndex] = pointF
    }

    private fun setActionDown(event: MotionEvent) {
        val index = event.actionIndex
        val id = event.getPointerId(index)

        val newIndex = if (isCancel) {
            indexCancel + index
        } else {
            index
        }

        if (isCancel) {
            mapColor[newIndex] = getColorRandomWithoutColorInMapColor()
        } else {
            mapColor[newIndex] = getColorRandomWithoutColorInMapColor()
        }
        addViewAndAnimation(id, PointF(event.getX(index), event.getY(index)))

        checkStart()
        invalidate()
    }

    private fun setActionUp(event: MotionEvent) {
        val index = event.actionIndex
        val id = event.getPointerId(index)
        val newKey = if (isCancel) {
            id + indexCancel
        } else {
            id
        }
        if (mapAnimation.contains(newKey) && mapView.contains(newKey)) {
            mapAnimation[newKey]!!.end()
            mapView[newKey]!!.forEach {
                removeView(it)
            }
            mapAnimation.remove(newKey)
            mapView.remove(newKey)
        }

        if (mapPointToDraw.contains(newKey)) {
            if (mapRank.contains(mapPointToDraw[newKey])) {
                mapRank.remove(mapPointToDraw[newKey])
            }
            mapPointToDraw.remove(newKey)
        }
        invalidate()
    }

    private fun setActionMove(event: MotionEvent) {
        val count = event.pointerCount

        for (index in 0 until count) {
            val id = event.getPointerId(index)
            checkPointToRemoveView(PointF(event.getX(index), event.getY(index)), id)
        }
        checkFinish()
    }

    private fun checkPointToRemoveView(point: PointF, index: Int) {
        val newIndex = if (isCancel) {
            indexCancel + index
        } else {
            index
        }
        // point
        if (mapPointToDraw.contains(newIndex)) {
            val currentPoint = mapPointToDraw[newIndex]!!
            val deltaX = abs(currentPoint.x - point.x)
            val deltaY = abs(currentPoint.y - point.y)
            if (deltaX >= touchSlop || deltaY >= touchSlop) {
                //update point
                mapPointToDraw[newIndex] = point
                // animation
                if (mapAnimation.contains(newIndex)) {
                    mapAnimation[newIndex]!!.end()
                    mapAnimation.remove(newIndex)
                }

                // view
                if (mapView.contains(newIndex)) {
                    mapView[newIndex]!!.forEach {
                        removeView(it)
                    }
                    mapView.remove(newIndex)
                }

                // color
                if (!mapColor.contains(newIndex)) {
                    if (isCancel) {
                        mapColor[newIndex] = getColorRandomWithoutColorInMapColor()
                    } else {
                        mapColor[newIndex] = getColorRandomWithoutColorInMapColor()
                    }
                }
                Log.d(TAG, "checkPointToRemoveView: ${mapPointToDraw.size}")
                addViewAndAnimation(index, point)
            }
        }
    }

    // ============== CHECK RESULT ==============
    private fun checkStart() {
        when (type) {
            SPIN_TYPE.CHOOSE, SPIN_TYPE.COUPLE -> {
                if (mapView.size == sizeSpinner + 1) {
                    startTime = System.currentTimeMillis()
                } else if (mapView.size < sizeSpinner + 1) {
                    startTime = 0L
                }
            }

            SPIN_TYPE.RANK -> {
                if (mapView.size == 2) {
                    startTime = System.currentTimeMillis()
                } else if (mapView.size < 2) {
                    startTime = 0L
                }
            }
        }
        if (startTime == 0L && !isEnd) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (mapView.size <= sizeSpinner) {
                    listener?.onToastAskMore(true)
                }
            }, TIME_DELAY_SHOW_RESULT)
        }
    }

    private fun checkFinish() {
        if (startTime != 0L && System.currentTimeMillis() - startTime > TIME_DELAY_SHOW_RESULT && !isCheckAtHandler) {
            when {
                type == SPIN_TYPE.CHOOSE -> {
                    typeChoose()
                }

                type == SPIN_TYPE.COUPLE -> {
                    typeCouple()
                }

                type == SPIN_TYPE.RANK -> {
                    typeRank()
                }
            }

            isEnd = true
        }
    }

    // ============== ACTION TYPE ==============

    private fun typeChoose() {
        setTypeRandom.clear()
        val randomBound = HashSet<Int>(mapPointToDraw.keys)
        for (i in 0 until sizeSpinner) {
            val randomValue = randomBound.random()
            setTypeRandom.add(randomValue)
            randomBound.remove(randomValue)
        }
        invalidate()
        listener?.onComplete()
    }

    private fun typeRank() {
        Log.d(TAG, "typeRank: ${mapPointToDraw.size}")
        mapRank.clear()
        var randomBound: MutableList<Int> = arrayListOf()
        for (i in 0 until mapPointToDraw.size){
            randomBound.add(i)
        }
        randomBound = randomBound.shuffled().toMutableList()

        mapPointToDraw.forEach { (k, v) ->
            val random = randomBound.random()
            mapRank[v] = random
            randomBound.remove(random)
        }

        Log.d(TAG, "typeRank: rank${mapRank.size}")
        mapRank.forEach {
            Log.d(TAG, "typeRank: ${it.value}")
        }
        invalidate()
        listener?.onComplete()
    }

    private fun typeCouple() {
        mapHomo.clear()
        mapHomoChangeColorView.clear()
        mapPointToDraw.forEach { t, u ->
            val newList = arrayListOf<PointF>()
            newList.add(u)
            mapHomo[t] = newList
        }

        var index = 0
        Log.d(TAG, "typeCouple: ${mapView.size} -- ${mapPointToDraw.size} -- ${mapAnimation.size} - ${childCount}")
        mapPointToDraw.forEach { k, v ->
            if (mapHomo.contains(index)) {
                val data = mapHomo[index]!!
                val currentList = data.toMutableList()
                currentList.add(v)
                mapHomo[index] = currentList

                if (mapHomoChangeColorView.contains(index)) {
                    val currentListKey = mapHomoChangeColorView[index]!!.toMutableList()
                    currentListKey.add(k)
                    mapHomoChangeColorView[index] = currentListKey
                } else {
                    mapHomoChangeColorView[index] = listOf(k)
                }
            }
            index = (index + 1) % sizeSpinner;
        }
        invalidate()
        listener?.onComplete()
    }

    // ============== SET DATA ==============

    private fun getColorRandomWithoutColorInMapColor(): Int {
        var randomColor: Int
        val random = Random()
        do {
            randomColor = Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
            )
        } while (mapColor.containsValue(randomColor))
        return randomColor
    }

    private fun resetData() {
        // clear view
        mapView.forEach {
            it.value.forEach { view->
                removeView(view)
            }
        }
        mapView.clear()
        removeAllViews()

        // clear animation
        mapAnimation.forEach {
            it.value.end()
        }
        mapAnimation.clear()

        mapPointToDraw.clear()
        mapRank.clear()
        setTypeRandom.clear()
        mapHomo.clear()
        mapHomoChangeColorView.clear()

        isCancel = false
        indexCancel = 0
        isEnd = false
        startTime = 0L
        handler = null
        isCheckAtHandler = false
    }

    fun again() {
        resetData()
        handler = Handler(Looper.getMainLooper())
        invalidate()
    }

    fun setType(type: SPIN_TYPE) {
        this.type = type
    }

    fun setSize(size: Int) {
        this.sizeSpinner = size
    }

    interface ISpinListener {
        fun onComplete()
        fun onToastAskMore(canToast: Boolean)
    }
}