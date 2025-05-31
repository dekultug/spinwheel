package com.amvfunny.dev.wheelist.base.common.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.amvfunny.dev.wheelist.base.BaseDialog
import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.IEventHandler
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class SpinWheelDialog<DB : ViewDataBinding>(layoutId: Int) : BaseDialog(layoutId), IEventHandler {
    protected val binding get() = _binding!!
    private var _binding: DB? = null

    override fun attachView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(myInflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onInitView() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    override fun onEvent(event: IEvent) {}
}
