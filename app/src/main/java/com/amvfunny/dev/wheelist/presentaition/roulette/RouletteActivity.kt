package com.amvfunny.dev.wheelist.presentaition.roulette

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.ReLoadNativeAllRoulette
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeAllSpinRoulette
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeHome
import com.amvfunny.dev.wheelist.base.common.eventbus.UpdateWheelRouletteEvent
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.databinding.RouletteActivityBinding
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette.AddEditRouletteActivity
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette.AddEditRouletteActivity.Companion.WHEEL_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.delete.DeleteRouletteFragment
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.REPEAT_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.TITLE_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.WHEEL_DATA_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouletteActivity : SpinWheelActivity<RouletteActivityBinding>() {

    companion object {
        const val SEE_LIST_KEY = "SEE_LIST_KEY"
        private const val TAG = "RouletteActivity"
    }

    private val adapter by lazy { RouletteAdapter() }

    private val viewModel by viewModels<RouletteViewModel>()

    override fun setBinding(layoutInflater: LayoutInflater): RouletteActivityBinding {
        return RouletteActivityBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        EventBusManager.instance?.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusManager.instance?.unregister(this)
    }

    override fun onEvent(event: IEvent) {
        super.onEvent(event)
        when (event) {
            is UpdateWheelRouletteEvent -> {
                event.wheel?.let {
                    viewModel.addOrEditWheel(it)
                }
                EventBusManager.instance?.removeSticky(event)
            }

            is ReLoadNativeAllRoulette -> {
                EventBusManager.instance?.removeSticky(event)
            }
        }
    }

    override fun setUpView() {
        super.setUpView()

        binding.tvRouletteAdd.setGradientButton(getAppDimension(R.dimen.dimen_12))

        binding.ivRouletteClose.setOnSafeClick {
           onBackPressed()
        }

        binding.tvRouletteAdd.setOnSafeClick {
            navigateTo(AddEditRouletteActivity::class.java)
        }

        binding.clHomeHeader.setGradientMain()

        if (viewModel.isOnlySeeList) {
            binding.tvRouletteAdd.gone()
        } else {
            binding.tvRouletteAdd.show()
        }
    }

    override fun removeListener() {
        super.removeListener()
        adapter.listener = null
    }


    override fun setUpAdapter() {
        super.setUpAdapter()
        binding.rvRoulette.adapter = adapter
        binding.rvRoulette.layoutManager = LinearLayoutManager(this)

        adapter.listener = object : RouletteAdapter.IRouletteListener {
            override fun onAdd() {
                navigateTo(AddEditRouletteActivity::class.java)
            }

            override fun onEdit(wheel: Wheel?) {
                navigateTo(AddEditRouletteActivity::class.java, bundleOf(WHEEL_KEY to wheel))
            }

            override fun onDuplicate(wheel: Wheel?) {
                viewModel.duplicateWheel(wheel)
            }

            override fun onShare(wheel: Wheel?) {
                Toast.makeText(
                    this@RouletteActivity,
                    getAppString(R.string.feature_improve, this@RouletteActivity),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onRemove(id: Int?, title: String?) {
                val deleteFra = DeleteRouletteFragment.getInstance(title)
                deleteFra.show(supportFragmentManager, deleteFra.tag)
                binding.rlRouletteAdmob.gone()
                deleteFra.listener = object : DeleteRouletteFragment.IDeleteListener {
                    override fun onDelete() {
                        binding.rlRouletteAdmob.show()
                        viewModel.removeWheel(id)
                    }

                    override fun onDismiss() {
                        binding.rlRouletteAdmob.show()
                    }
                }
            }

            override fun onClickRoulette(wheel: Wheel) {
                navigateTo(
                    SpinRouletteActivity::class.java, bundleOf(
                        WHEEL_DATA_KEY to wheel,
                        REPEAT_KEY to wheel.countRepeat,
                        TITLE_KEY to wheel.title
                    )
                )
            }

            override fun onShowPopup() {

            }

            override fun onHidePopup() {
            }
        }
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.rouletteListState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    val list = it.data!!.toMutableList()
                    if (list.isEmpty()) {
                        list.add(Wheel(wheelType = WHEEL_TYPE.EXAMPLE.value))
                    }
                    list.forEach {
                        Log.d("TAG", "onSuccess: $it")
                    }
                    adapter.submitList(list)
                    scrollIndex()
                }
            })
        }
    }

    private fun scrollIndex() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (viewModel.indexScroll != -1) {
                binding.rvRoulette.scrollToPosition(viewModel.indexScroll)
                viewModel.indexScroll = -1
            }
        }, 150)
    }

    override fun onDestroy() {
        EventBusManager.instance?.postPending(ReloadNativeHome())
        if (viewModel.isOnlySeeList) {
            EventBusManager.instance?.postPending(ReloadNativeAllSpinRoulette())
        }
        super.onDestroy()
    }
}