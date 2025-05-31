package com.amvfunny.dev.wheelist.base.common.loader.image

object LoadImageFactory {
    private val loader by lazy { LoadImageImpl() }

    init {

    }

    fun getLoadImage(): ILoadImage {
        return loader
    }
}
