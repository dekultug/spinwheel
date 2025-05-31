package com.amvfunny.dev.wheelist.presentaition.widget.switch

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.amvfunny.dev.wheelist.R
import java.util.concurrent.Flow

//class SwitchView constructor(
//    ctx: Context,
//    attributeSet: AttributeSet?
//) : ConstraintLayout(ctx, attributeSet) {
//
//    companion object {
//        private const val TAG = "SwitchView"
//    }
//
//    private var flRoot: FrameLayout? = null
//    private var viewSwitch: View? = null
//
//    private var coordinateX: Float = 0f
//
//    init {
//        LayoutInflater.from(ctx).inflate(R.layout.switch_layout, this, true)
//    }
//
//    override fun onFinishInflate() {
//        super.onFinishInflate()
//        flRoot = findViewById(R.id.flSwitchRoot)
//        viewSwitch = findViewById(R.id.vSwitch)
//
//        setEventView()
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun setEventView() {
//        viewSwitch?.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    coordinateX = event.rawX + (v.layoutParams as MarginLayoutParams).leftMargin
//                }
//
//                MotionEvent.ACTION_MOVE -> {
//                    val left = event.rawX - coordinateX
//                    Log.d(TAG, "setEventView: $left")
//                    updateLeft(left)
//                    coordinateX = left
//                }
//
//                MotionEvent.ACTION_UP -> {
//                    coordinateX = event.rawX
//                }
//            }
//            true
//        }
//    }
//
//    private fun updateLeft(left: Float) {
//        val param = viewSwitch?.layoutParams as MarginLayoutParams
//        param.leftMargin = left.toInt()
//        viewSwitch?.layoutParams = param
//    }
//}