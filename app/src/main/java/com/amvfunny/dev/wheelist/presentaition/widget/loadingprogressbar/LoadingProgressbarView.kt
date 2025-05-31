package com.amvfunny.dev.wheelist.presentaition.widget.loadingprogressbar

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension

class LoadingProgressbarView constructor(
    ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    companion object {
        private const val TAG = "LoadingProgressbarView"
    }

    private var ivTop: ImageView? = null
    private var progressbarBottom: LinearProgressIndicator? = null

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var duration: Long = 1000

    private var widthScreen: Int = 0

    var listener: ILoadingProgressCallback? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.loading_progressbar_layout, this, true)

        initView(attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthScreen =
            (MeasureSpec.getSize(widthMeasureSpec) - 2 * getAppDimension(R.dimen.dimen_32) - getAppDimension(
                R.dimen.dimen_12
            )).toInt()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initHandler()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearHandler()
    }

    private fun initView(attrs: AttributeSet?) {
        ivTop = findViewById(R.id.ivLoadingProgressbar)
        progressbarBottom = findViewById(R.id.lpiLoadingProgressbar)

        setEventView()
    }

    private fun initHandler() {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
    }

    private fun setEventView() {
        setUpLoading()
    }

    private fun setUpLoading() {
        initHandler()

        runnable = object : Runnable {
            override fun run() {
                var currentProgress = progressbarBottom?.progress ?: 0
                if (currentProgress < 100) {
                    currentProgress += (duration / 100).toInt()
                    progressbarBottom?.progress = currentProgress
                    setUpLeftMargin(currentProgress)
                    handler?.postDelayed(this, 100)
                } else {
                    clearHandler()
                    listener?.onComplete()
                }
            }
        }
        runnable?.let {
            handler?.postDelayed(it, 100)
        }
    }

    private fun setUpLeftMargin(progressValue: Int) {
        val newLeft = (progressValue * widthScreen) / 100
        val newParam = ivTop?.layoutParams as ViewGroup.MarginLayoutParams
        newParam.leftMargin = newLeft
        ivTop?.layoutParams = newParam
    }

    private fun clearHandler() {
        runnable?.let {
            handler?.removeCallbacks(it)
        }
        handler = null
    }

    interface ILoadingProgressCallback {
        fun onComplete()
    }
}