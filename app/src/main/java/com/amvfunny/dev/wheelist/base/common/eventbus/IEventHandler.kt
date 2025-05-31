package com.amvfunny.dev.wheelist.base.common.eventbus

import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

interface IEventHandler {
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: IEvent)
}
